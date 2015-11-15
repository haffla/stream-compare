package models.service.oauth

import models.service.library.SpotifyLibrary
import models.service.oauth.SpotifyService.apiEndpoints
import models.service.Constants
import models.util.Logging
import models.service.oauth.SpotifyService._
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WS, WSResponse}

import scala.concurrent.Future

class SpotifyService(userId:Int) {

  val library = new SpotifyLibrary(userId)

  def requestUserData(code:String): Future[Option[JsValue]] = {
    val data = apiEndpoints.data + ("code" -> Seq(code))
    val futureResponse: Future[WSResponse] = WS.url(apiEndpoints.token).post(data)
    for {
      token <- getAccessToken(futureResponse)
      response <- requestUsersTracks(token)
      seq = library.convertJsonToSeq(response)
      result = library.convertSeqToMap(seq)
    } yield response
  }
}

object SpotifyService extends StreamingServiceAbstract {

  def apply(userId:Int) = new SpotifyService(userId)
  val clientIdKey = "spotify.client.id"
  val clientSecretKey = "spotify.client.secret"

  val redirectUriPath = "/spotify/callback"
  override lazy val redirectUri = "http://localhost:9000/spotify/callback"
  val scope:Seq[String] = Seq(
    "user-read-private",
    "playlist-read-private",
    "user-follow-read",
    "user-library-read"
  )
  val cookieKey = "spotify_auth_state"

  val queryString:Map[String,Seq[String]] = Map(
    "response_type" -> Seq("code"),
    "client_id" -> Seq(clientId),
    "scope" -> Seq(scope.mkString(" ")),
    "redirect_uri" -> Seq(redirectUri)
  )

  object apiEndpoints {
    val tracks = "https://api.spotify.com/v1/me/tracks"
    val token = "https://accounts.spotify.com/api/token"
    val authorize = "https://accounts.spotify.com/authorize"
    val search = "https://api.spotify.com/v1/search"

    val data = Map(
      "redirect_uri" -> Seq(redirectUri),
      "grant_type" -> Seq("authorization_code"),
      "client_id" -> Seq(clientId),
      "client_secret" -> Seq(clientSecret)
    )
  }

  private def requestUsersTracks(token:Option[String]):Future[Option[JsValue]] = {
    token match {
      case Some(accessToken) =>
        WS.url(apiEndpoints.tracks).withHeaders("Authorization" -> s"Bearer $accessToken").get() map { response =>
          response.status match {
            case 200 =>
              val json = Json.parse(response.body)
              Some(json)
            case http_code =>
              Logging.error(ich, Constants.userTracksRetrievalError + ": " +  http_code + "\n" + response.body)
              None
          }
        }
      case None => throw new Exception (Constants.accessTokenRetrievalError)
    }

  }
}
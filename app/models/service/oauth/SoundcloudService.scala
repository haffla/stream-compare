package models.service.oauth

import com.github.haffla.soundcloud.Client
import models.service.importer.SoundcloudImporter
import models.service.oauth.SoundcloudService._
import models.service.util.ServiceAccessTokenHelper
import models.util.Constants
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.Future

class SoundcloudService(identifier: Either[Int, String]) extends ApiDataRequest("soundcloud", identifier) {

  override val importer = new SoundcloudImporter(identifier)
  val serviceAccessTokenHelper = new ServiceAccessTokenHelper("soundcloud", identifier)

  override def doDataRequest(code:String):Future[(Option[String],Option[String])] = {
    for {
      authCredentials <- client.exchange_token(code)
      userId <- getUserId(authCredentials)
      response <- requestUsersTracks(userId)
      seq <- importer.convertJsonToSeq(response)
      result = importer.convertSeqToMap(seq)
    } yield ((Json.parse(authCredentials) \ "access_token").asOpt[String],None)
  }
}

object SoundcloudService extends OAuthStreamingService with OAuthRouting {

  def apply(identifier: Either[Int, String]) = new SoundcloudService(identifier)

  override val clientIdKey = "soundcloud.client.id"
  override val clientSecretKey = "soundcloud.client.secret"

  override val redirectUriPath="/soundcloud/callback"
  override val cookieKey = "soundcloud_auth_state"

  override val queryString:Map[String,Seq[String]] = Map(
    "response_type" -> Seq("code"),
    "client_id" -> Seq(clientId),
    "redirect_uri" -> Seq(redirectUri),
    "scope" -> Seq("non-expiring")
  )

  override val authorizeEndpoint: String = "https://api.soundcloud.com/connect"

  val client = Client(clientId, clientSecret, redirectUri)

  def requestUsersTracks(token:Option[String]):Future[Option[JsValue]] = {
    token match {
      case Some(userId) =>
        client.users(userId)("favorites") map { favorites =>
          Some(Json.parse(favorites))
        }
      case None => Future.failed(new Exception(Constants.accessTokenRetrievalError))
    }
  }

  private def getUserId(authCredentials: String):Future[Option[String]] = {
    (Json.parse(authCredentials) \ "access_token").asOpt[String] match {
      case Some(token) => client.me(token)().map { user =>
        val json = Json.parse(user)
        Some((json \ "id").as[Int].toString)
      }
      case None => Future.failed(new Exception(Constants.accessTokenRetrievalError))
    }
  }

}

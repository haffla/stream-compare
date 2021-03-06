package models.service.analysis

import models.database.alias.Artist
import models.database.facade.service.NapsterArtistFacade
import models.service.api.NapsterApiFacade
import models.service.oauth.NapsterService
import models.util.Constants
import play.api.Play
import play.api.Play.current
import play.api.libs.json.JsValue
import play.api.libs.ws.{WS, WSRequest}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class NapsterAnalysis(identifier:Either[Int,String],
                      usersFavouriteArtists: List[Artist])
                      extends ServiceAnalysis(identifier, usersFavouriteArtists, Constants.serviceNapster) {

  override val searchEndpoint = NapsterService.apiEndpoints.artists
  override val serviceArtistFacade = NapsterArtistFacade
  override val apiFacade = NapsterApiFacade

  protected def urlForRequest(artistId:String):String = searchEndpoint + "/" + artistId + "/albums?limit=200"

  protected override def handleJsonResponse(jsResp:JsValue):List[(String,String)] = {
    val items = jsResp.as[List[JsValue]]
    val raw = items.map {
      item =>
        val typeOfAlbum = (item \ "type" \ "id").as[Int]
        // 0 = Main Release, 1 = Single, We don't want compilations and stuff
        if(typeOfAlbum == 0 || typeOfAlbum == 1) {
          val albumName = (item \ "name").as[String]
          val id = (item \ "id").as[String]
          Some((albumName, id))
        }
        else None
    }
    raw.flatten
  }

  protected override def getServiceFieldFromArtist(artist: Artist): Option[String] = artist.napsterId

  protected override def getAuthenticatedRequest(url:String, accessToken:String):WSRequest = {
    WS.url(url).withQueryString("apikey" -> accessToken)
  }

  protected override def testAndGetAccessToken():Future[Option[String]] = {
    Future {
      Play.current.configuration.getString(NapsterService.clientIdKey)
    }
  }
}

object NapsterAnalysis extends ServiceAnalysisTrait {
  def apply(identifier:Either[Int,String], userFavouriteArtists: List[Artist])
              = new NapsterAnalysis(identifier, userFavouriteArtists)
}



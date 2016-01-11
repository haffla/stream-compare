package models.database.facade

import play.api.cache.Cache
import scalikejdbc._
import play.api.Play.current

abstract class ServiceFacade extends Facade {

  val serviceFieldName:SQLSyntax

  def saveArtistWithServiceId(artistName: String, serviceId: String):Unit = {
    val cacheKey = s"$artistName|$serviceId|UPDATED"
    Cache.get(cacheKey) match {
      case Some(_) =>
      case None =>
        sql"select artist_name, $serviceFieldName from artist where artist_name=$artistName".map(
          rs => (rs.string("artist_name"), rs.string(serviceFieldName.value))
        ).single().apply() match {
          case Some((artName, i)) =>
            if(i != serviceId) updateServiceId(artistName, serviceId)
          case None => createNewArtistWithId(artistName, serviceId)
        }
        Cache.set(cacheKey, true)
    }
  }

  private def updateServiceId(artistName: String, serviceId: String):Unit = {
    sql"update artist set $serviceFieldName=$serviceId where artist_name=$artistName".update().apply()
  }

  private def createNewArtistWithId(artistName: String, serviceId: String) = {
    sql"insert into artist (artist_name, $serviceFieldName) VALUES ($artistName, $serviceId)".update().apply()
  }
}

object Services {

  def getFieldForService(service:String) = {
    service match {
      case "spotify" => sqls"spotify_token"
      case "deezer" => sqls"deezer_token"
      case "soundcloud" => sqls"soundcloud_token"
      case "lastfm" => sqls"lastfm_token"
      case _ => throw new IllegalArgumentException("The given service '$service' is not supported")
    }
  }
}

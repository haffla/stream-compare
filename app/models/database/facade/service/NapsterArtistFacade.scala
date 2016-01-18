package models.database.facade.service

import models.database.alias._
import models.database.alias.service.NapsterArtist
import org.squeryl.PrimitiveTypeMode._
import play.api.libs.json.JsValue

class NapsterArtistFacade(identifier:Either[Int,String]) extends ServiceArtistFacade(identifier) {

  val serviceName = "napster"

  override def joinWithArtistsAndAlbums(usersArtists:List[Long]):List[(Album,Artist,String)] = {
    join(AppDB.albums,
         AppDB.artists,
         AppDB.napsterAlbums,
         AppDB.napsterArtists)( (alb,art,npAlb,npArt) =>
      where(art.id in usersArtists)
        select(alb, art, npAlb.napsterId)
        on(
        alb.artistId === art.id,
        alb.id === npAlb.id,
        art.id === npArt.id
        )
    ).toList
  }

}

object NapsterArtistFacade extends ServiceArtistTrait {

  def apply(identifier: Either[Int,String]) = new NapsterArtistFacade(identifier)

  override def insertIfNotExists(id:Long):Long = {
    from(AppDB.napsterArtists)(sa =>
      where(sa.id === id)
        select sa.id
    ).headOption match {
      case None => insert(id)
      case _ => id
    }
  }

  override def insert(id: Long):Long = {
    AppDB.napsterArtists.insert(NapsterArtist(id)).id
  }

  override def saveInfoAboutArtist(js: JsValue): Unit = {
    //TODO do something here
  }
}



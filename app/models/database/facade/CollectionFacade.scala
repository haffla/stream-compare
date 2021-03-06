package models.database.facade

import models.database.AppDB
import models.database.alias._
import models.util.GroupMeasureConversion
import org.squeryl.PrimitiveTypeMode._

object CollectionFacade {
  def apply(identifier:Either[Int,String]) = new CollectionFacade(identifier)
}

class CollectionFacade(identifier:Either[Int,String]) extends Facade with GroupMeasureConversion {

  def save(trackId: Long, timePlayed:Int = 1) = {
    inTransaction {
      byId(trackId) match {
        case Some(_) =>
        case None => insert(trackId)
      }
    }
  }

  def insert(trackId: Long) = {
    val collection = identifier match {
      case Left(userId) => UserCollection(trackId = trackId, userId = Some(userId))
      case Right(userSession) => UserCollection(trackId = trackId, userSession = Some(userSession))
    }
    AppDB.collections.insert(collection)
  }

  def byId(trackId:Long):Option[UserCollection] = {
    from(AppDB.collections)(c =>
      where(c.trackId === trackId and AppDB.userWhereClause(c,identifier))
        select c
    ).headOption
  }

  //TODO maybe apply limit by taking only some from weighted
  def userCollection:List[(Album,Artist,Track,UserCollection,UserArtistLiking,Long)] = {
    transaction {
      val weighted = ArtistFacade(identifier).mostListenedToArtists()
      // Use this list to filter in the where clause
      val weightedArtistIds:List[Long] = weighted.map(_.key)
      // This map is used then in the select statement
      val artistTrackCountMap:Map[Long,Long] = toMap(weighted)
      join(AppDB.artists, AppDB.tracks, AppDB.albums, AppDB.collections, AppDB.userArtistLikings)(
        (art,tr,alb,col,ual) =>
          where(
            AppDB.userWhereClause(col,identifier)
            and art.id.in(weightedArtistIds)
          )
          select (alb,art,tr,col,ual,artistTrackCountMap.getOrElse(art.id, 1L))
          on(
            tr.artistId === art.id,
            tr.albumId === alb.id,
            col.trackId === tr.id,
            art.id === ual.artistId and AppDB.joinUserRelatedEntities(col,ual,identifier)
            )
        ).toList
    }
  }
}
package models.service.importer

import models.util.{TextWrangler, Constants}
import play.api.libs.json.JsValue

object AudioJsonImporter {
  def apply(identifier: Either[Int, String]) = new AudioJsonImporter(identifier)
}

class AudioJsonImporter(identifier: Either[Int, String]) extends Importer(identifier, "audio") {

  def handleFiles(files:JsValue):Seq[Map[String,String]] = {
    val jsList = files.as[List[JsValue]]
    val totalLength = jsList.length
    jsList.zipWithIndex.map { case (file,i) =>
      val position = i + 1
      apiHelper.setRetrievalProcessProgress(position.toDouble / totalLength)
      val artist = (file \ "artist").asOpt[String]
      val album = (file \ "album").asOpt[String]
      val track = (file \ "title").asOpt[String]
      (artist,album,track) match {
        case (Some(art),Some(alb),Some(tr)) =>
          Map(
            Constants.mapKeyArtist -> TextWrangler.cleanupString(art),
            Constants.mapKeyAlbum -> alb,
            Constants.mapKeyTrack -> tr
          )
        case _ => Map[String,String]()
      }
    }.filterNot(_.isEmpty)
  }

  def process(files:JsValue):Unit = {
    apiHelper.setRetrievalProcessPending()
    convertSeqToMap(handleFiles(files))
  }
}

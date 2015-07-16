package com.beastserver.dao

import java.util.{NoSuchElementException, UUID}

import com.beastserver.core.Models._
import com.beastserver.gen.Tables
import slick.driver.PostgresDriver.api._
import spray.http.MediaTypes

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

/**
 * debal on 15.07.2015.
 */
class MediaDAO (implicit val db: Database, implicit val ex: ExecutionContext) extends MediaComponent
{
  def getExactlyOne(uuid: UUID): Future[Option[Media]] = {
    db.run {
      getMediaQuery(uuid).result
    } map {
      seq => seq.map {
        case (x, y) =>
          val arr = x split '/'
          require(arr.length == 2, "something wrong with mime type definition")
          Media(MediaTypes.getForKey(arr(0) -> arr(1)) get, y.fold(Array[Byte]()){x => x})
      }
    } map {
      seq: Seq[Media] => seq.headOption
    }
  }
}

trait MediaComponent
{
  private def getMediaForCompile(uuid: Rep[UUID]) = {
    for {
      mc <- Tables.MediaCache if mc.uuid === uuid
      m <- Tables.Media if mc.mediaId === m.id
      ct <- Tables.Mime if m.mimeId === ct.id
    } yield (ct.mimeType, m.content)
  }

  val getMediaQuery = Compiled {
    getMediaForCompile _
  }

  def insertMediaCache(id: Int, imgUUID: UUID) = DBIO.seq {
      Tables.MediaCache += Tables.MediaCacheRow(imgUUID, id)
  }

  def insertMediaCache(pairs: Map[UUID, Int]) = DBIO.seq {
    Tables.MediaCache ++= pairs map {
      case (uuid, id) => Tables.MediaCacheRow(uuid, id)
    }
  }
}

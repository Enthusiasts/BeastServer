package com.beastserver.dao

import java.util.UUID

import com.beastserver.core.Models._
import com.beastserver.gen.Tables
import slick.driver.PostgresDriver.api._
import spray.http.MediaTypes

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

/**
 * debal on 15.07.2015.
 */
class MediaDAO (implicit val db: Database, implicit val ex: ExecutionContext)
{
  def getExactlyOne(uuid: UUID): Future[Option[Media]] = {
    db.run {
      Queries.getExactlyOne(uuid).result
    } map {
      seq => seq.map {
        case (x, y) =>
          val arr = x split '/'
          require(arr.length == 2, "something wrong with mime type definition")
          Media(MediaTypes.getForKey(arr(0) -> arr(1)) get, y get)
      }
    } map {
      seq: Seq[Media] => seq.headOption
    }
  }

  def insertMediaCache(id: Int, imgUUID: UUID) = db.run{
    DBIO.seq {
      Tables.MediaCache += Tables.MediaCacheRow(imgUUID, id)
    }
  }

  private object Queries
  {
    private def getExactlyOneQuery(uuid: Rep[UUID]) = for {
      mc <- Tables.MediaCache if mc.uuid === uuid
      m <- Tables.Media if mc.mediaId === m.id
      ct <- Tables.Mime if m.mimeId === ct.id
    } yield (ct.mimeType, m.content)

    val getExactlyOne = Compiled {
      getExactlyOneQuery _
    }
  }
}

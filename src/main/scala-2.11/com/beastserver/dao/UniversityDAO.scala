package com.beastserver.dao

import java.util.{Base64, UUID}

import com.beastserver.core.Models._
import com.beastserver.gen._
import slick.driver.PostgresDriver.api._

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

/**
 * debal on 11.07.2015.
 */
class UniversityDAO (implicit val db: Database, implicit val ex: ExecutionContext) extends UniversityComponent with MediaComponent
{
  def getSequence(num: Int): Future[Seq[University]] = {
    require(num > 0)
    val future = db.run{
      Tables.University.take(num).to[Seq].result
    }
    future.map {seq =>
      seq.map{ uni => University(uni.id, uni.title.fold(""){x => x}, uni.title.fold(""){x => x})}
    }
  }

  def getSequenceOf(ids: Seq[Int]): Future[Seq[University]] = {
    require(ids.nonEmpty && !ids.exists(_ < 0))
    val future = db.run{
      Tables.University.withFilter(_.id inSet ids).to[Seq].result
    }
    future.map {seq =>
      seq.map{ uni => University(uni.id, uni.title.fold(""){x => x}, uni.title.fold(""){x => x})}
    }
  }

  def getExactlyOne(id: Int): Future[Option[University]] = db.run{
    getUniversityQuery(id).result
  } map {
    _ map {
      case (idUni, title, img) => University(idUni, title.fold(""){x => x}, title.fold(""){x => x})
    }
  } map {_.headOption}

  //TODO: handle transactions
  /*def insertMediaCache(uniId: Int, imgUUID: UUID) = {
    for {
      uni <- Tables.University if uni.id === uniId
    } yield uni.mediaId
  }.result andThen {
    DBIO.seq {
      Tables.MediaCache += Tables.MediaCacheRow(imgUUID, id)
    }
  }.transactionally*/

  def getOneTest(id: Int, uuid: UUID): Future[Option[(Int, String, UUID)]] = db.run{
    {
      for{
        uni <- getUniversityQuery(id).result
        _ <- insertMediaCache(uni.headOption.get._3, uuid) if uni nonEmpty
      } yield {
        uni.headOption map {
          case (uniId, title, img) =>
            (uniId, title.fold(""){t => t}, uuid)
        }
      }
    }.transactionally
  }

}

sealed trait UniversityComponent
{
  private def getUniversityForCompile(id: Rep[Int]) = for {
    uni <- Tables.University if uni.id === id
    img <- Tables.Media if uni.mediaId === img.id
    mime <- Tables.Mime if img.mimeId === mime.id
  } yield (uni.id, uni.title, img.id)

  val getUniversityQuery = Compiled {
    getUniversityForCompile _
  }
}

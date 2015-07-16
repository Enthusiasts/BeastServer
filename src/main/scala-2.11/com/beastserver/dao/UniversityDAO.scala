package com.beastserver.dao

import java.util.{NoSuchElementException, Base64, UUID}

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

  def getExactlyOne(id: Int, uuid: UUID): Future[Option[(Int, String, UUID)]] = {
    require(id >= 0)
    db.run{
      {
        for{
          uni <- getUniversityQuery(id).result
          _ <- insertMediaCache(uni.headOption.get._3, uuid)
        } yield {
          uni.headOption map {
            case (uniId, title, img) =>
              (uniId, title.fold(""){t => t}, uuid)
          }
        }
      }.transactionally
    } recover {
      case nse: NoSuchElementException => None
    }
  }

  def getSequenseTest(uuids: Seq[UUID]): Future[Seq[(Int, String, UUID)]] = {
    require(uuids nonEmpty)
    db.run{
      {
        for {
          unis <- getUniversitySeqQuery(uuids.length).result
          _ <- insertMediaCache {
            uuids zip (unis map {x => x._3}) toMap
          }
        } yield {
          (unis zip uuids) map {
            case (uni, uuid) =>
              (uni._1, uni._2.fold(""){x=>x}, uuid)
          }
        }
      }.transactionally
    }
  }
}

sealed trait UniversityComponent
{
  private def generalize(query: Query[Tables.University, Tables.UniversityRow, Seq]) = for {
    uni <- query
    img <- Tables.Media if uni.mediaId === img.id
  } yield (uni.id, uni.title, img.id)

  private def getUniversityForCompile(id: Rep[Int]) = generalize(Tables.University.withFilter(_.id === id))

  lazy val getUniversityQuery = Compiled {
    getUniversityForCompile _
  }

  def getUniversitySeqQuery(count: Int) = generalize(Tables.University.take(count))

  def getUniversitySeqOfQuery(ids: Seq[Int]) = generalize(Tables.University.withFilter(_.id inSet ids))
}

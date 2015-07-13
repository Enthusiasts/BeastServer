package com.beastserver.dao

import com.beastserver.core.Models._
import com.beastserver.gen._
import slick.driver.PostgresDriver.api._

import scala.concurrent.{ExecutionContext, Future}

/**
 * debal on 11.07.2015.
 */
class UniversityDAO (implicit val db: Database, implicit val ex: ExecutionContext)
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

  def getExactlyOne(id: Int): Future[Option[University]] = getSequenceOf(Seq(id)).map{_.headOption}
}

package com.beastserver.dao

import com.beastserver.gen._
import slick.driver.PostgresDriver.api._
import slick.lifted.{Compiled, Rep}

import scala.concurrent.{ExecutionContext, Future}

/**
 * debal on 03.10.2015.
 */
class NGUniversityDAO (implicit val db: Database, implicit val ex: ExecutionContext)
  extends NGDAO[Int, Tables.UniversityRow, SlickFilter[Tables.University]]
  with NGUniversityComponent
{
  lazy val filters = new NGUniversitiesFilters()

  override def getExactlyOne(id: Int): Future[Option[Tables.UniversityRow]] = {
    db.run{
      for {
        uni <- matchUniversity(id).result
      } yield uni.headOption
    }
  }

  override def deleteAll(ids: Seq[Int]): Future[Seq[Tables.UniversityRow]] = {
    deleteAllBy(Seq(filters.inside(ids)))
  }

  override def insertOrUpdateOne(inst: Tables.UniversityRow): Future[Option[Tables.UniversityRow]] = {
    db.run {
      for {
        uni <- insertOrUpdate(Seq(inst))
      } yield uni.headOption
    }
  }

  override def insertOrUpdateAll(seq: Seq[Tables.UniversityRow]): Future[Seq[Tables.UniversityRow]] = {
    db.run {
      for {
        unis <- insertOrUpdate(seq)
      } yield unis
    }
  }

  override def getSequence(ids: Seq[Int]): Future[Seq[Tables.UniversityRow]] = {
    getSequenceBy(ids.length, Seq(filters.any()))
  }

  override def deleteAllBy(where: Seq[SlickFilter[Tables.University]]): Future[Seq[Tables.UniversityRow]] = {
    db.run {
      val matched = matchAllBy(where)
      for {
        uni <- matched.result
        countAffected <- matched.delete
      } yield uni
    }
  }

  override def delete(id: Int): Future[Option[Tables.UniversityRow]] = {
    db.run {
      val matched = matchUniversity(id)
      for {
        uni <- matched.result
        countAffected <- matched.delete
      } yield uni.headOption
    }
  }

  override def getSequenceBy(count: Int, where: Seq[SlickFilter[Tables.University]]): Future[Seq[Tables.UniversityRow]] = {
    db.run {
      val matched = matchAllBy(where) take count
      for {
        uni <- matched.result
      } yield uni
    }
  }
}

sealed trait NGUniversityComponent
{
  private def matchUniversityForCompile(id: Rep[Int]) = for {
    uni <- Tables.University if uni.id === id
  } yield uni

  protected lazy val matchUniversity = Compiled {
    matchUniversityForCompile _
  }

  protected def matchAllBy(count: Int, filters: Seq[SlickFilter[Tables.University]]): Query[Tables.University, Tables.UniversityRow, Seq] = {
    matchAllBy(filters) take count
  }

  protected def matchAllBy(filters: Seq[SlickFilter[Tables.University]]): Query[Tables.University, Tables.UniversityRow, Seq] = {
    require(filters.nonEmpty)
    for {
      uni <- Tables.University if filters.foldLeft(filters.head(uni))((x, y) => x && y(uni))
    } yield uni
  }

  protected def insertOrUpdate(list: Seq[Tables.UniversityRow]) = {
    (Tables.University returning Tables.University.map(_.id) into ((uni, newId) => uni.copy(id = newId))) ++= list
  }
}

sealed class NGUniversitiesFilters
{
  lazy val prefix = (sample: String) => new SlickFilter[Tables.University] {
    override def apply(row: Tables.University): Rep[Boolean] = row.title.getOrElse("").toLowerCase.startsWith(sample)
  }

  lazy val any = () => new SlickFilter[Tables.University] {
    override def apply(inst: Tables.University): Rep[Boolean] = true
  }

  lazy val inside = (ids: Seq[Int]) => new SlickFilter[Tables.University] {
    override def apply(inst: Tables.University): Rep[Boolean] = inst.id.inSet(ids)
  }
}

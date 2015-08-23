package com.beastserver.dao

import com.beastserver.core.Models
import com.beastserver.core.Models.Course
import com.beastserver.gen.Tables
import slick.lifted.{Rep, Compiled}
import slick.driver.PostgresDriver.api._

import scala.concurrent.{ExecutionContext, Future}

/**
 * debalid on 23.08.2015.
 */
class CourseDAO (implicit val db: Database, implicit val ex: ExecutionContext)
  extends DAO[Models.Course, Int, Tables.Course] with CourseComponent
{
  //Contains filters that we can use with *By methods.
  lazy val filters = new CourseFilters()

  //Mapping from tuple to model.
  private def persistent2model(row: Tables.CourseRow) =
    Models.Course(row.id, row.title.fold(""){x=>x}, row.universityId)

  override def getExactlyOne(id: Int): Future[Option[Course]] = {
    require(id > 0)
    db.run{
      matchCourse(id).result
    } map {
      seq => seq.headOption.map(persistent2model)
    }
  }

  override def deleteAll(ids: Seq[Int]): Future[Seq[Course]] = {
    deleteAllBy(filters.inside(ids))
  }

  override def insertOrUpdateOne(inst: Course): Future[Option[Course]] = ???

  override def insertOrUpdateAll(seq: Seq[Course]): Future[Option[Course]] = ???

  override def getSequence(ids: Seq[Int]): Future[Seq[Course]] = {
    getSequenceBy(filters.inside(ids))
  }

  override def delete(id: Int): Future[Option[Course]] = {
    db.run {
      {
        for {
          course <- matchCourse(id).result
          _ <- matchCourse(id).delete
        } yield course
      }.transactionally
    } map {
      seq => seq.headOption.map(persistent2model)
    }
  }

  override def getSequenceBy(where: Filter[Tables.Course]): Future[Seq[Course]] = {
    db.run {
      matchAllByQuery(where).result
    } map {
      seq => seq.map(persistent2model)
    }
  }

  override def deleteAllBy(where: Filter[Tables.Course]): Future[Seq[Course]] = {
    db.run {
      {
        for {
          to <- matchAllByQuery(where).result
          _ <- matchAllByQuery(where).delete
        } yield to
      }.transactionally
    } map {
      seq => seq map persistent2model
    }
  }
}

//All common slick queries are here
sealed trait CourseComponent
{
  //Queries to compile
  private def matchCourseForCompile(id: Rep[Int]) = for {
    course <- Tables.Course if course.id === id
  } yield course

  lazy val matchCourse = Compiled {
    matchCourseForCompile _
  }

  def matchAllByQuery(filter: Filter[Tables.Course]) = {
    for {
      course <- Tables.Course if filter(course)
    } yield course
  }
}

//Some common in use filters
sealed class CourseFilters
{
  lazy val prefix = (sample: String, count: Int) => new Filter[Tables.Course] {
    override def apply(row: Tables.Course): Rep[Boolean] = row.title.getOrElse("").startsWith(sample)
  }

  lazy val byUniversityId = (sample: Int, count: Int) => new Filter[Tables.Course] {
    override def apply(inst: Tables.Course): Rep[Boolean] = inst.id === sample
  }

  lazy val any = (count: Int) => new Filter[Tables.Course] {
    override def apply(inst: Tables.Course): Rep[Boolean] = true
  }

  lazy val inside = (ids: Seq[Int]) => new Filter[Tables.Course] {
    override def apply(inst: Tables.Course): Rep[Boolean] = inst.id.inSet(ids)
  }
}

package com.beastserver.dao

import com.beastserver.core.Models
import com.beastserver.core.Models.Course
import com.beastserver.gen.Tables
import slick.driver.PostgresDriver.api._
import slick.lifted.{Compiled, Rep}

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
  private def persistent2model(row: Tables.CourseRow): Models.Course =
    Models.Course(row.id, row.title.fold(""){x=>x}, row.universityId)

  private def model2persistent(model: Models.Course): Tables.CourseRow =
    Tables.CourseRow(model.id, Option(model.title), model.university_id)

  override def getExactlyOne(id: Int): Future[Option[Course]] = {
    require(id > 0)
    db.run{
      matchCourse(id).result
    } map {
      seq => seq.headOption.map(persistent2model)
    }
  }

  override def deleteAll(ids: Seq[Int]): Future[Book[Course]] = {
    deleteAllBy(Seq(filters.inside(ids)))
  }

  override def insertOrUpdateOne(inst: Course): Future[Option[Course]] = {
    db.run{
      insertOrUpdateQuery(Seq(model2persistent(inst)))
    } map {
      _.headOption map persistent2model
    }
  }

  override def insertOrUpdateAll(seq: Seq[Course]): Future[Book[Course]] = {
    db.run{
      insertOrUpdateQuery(seq map model2persistent)
    } map {
      x => Book(x map persistent2model)
    }
  }

  override def getSequence(ids: Seq[Int]): Future[Book[Course]] = {
    getSequenceBy(ids.length, Seq(filters.inside(ids)))
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

  override def getSequenceBy(count: Int, where: Seq[Filter[Tables.Course]]): Future[Book[Course]] = {
    db.run {
      matchAllByQuery(count, where).result
    } map {
      seq => Book(seq map persistent2model)
    }
  }
  //def readAllBy(count: Int, where: Filter[Tables.Course]*): Future[Book[Course]] = readAllBy(count, where)

  override def deleteAllBy(where: Seq[Filter[Tables.Course]]): Future[Book[Course]] = {
    db.run {
      {
        for {
          to <- matchAllByQuery(where).result
          _ <- matchAllByQuery(where).delete
        } yield to
      }.transactionally
    } map {
      seq => Book(seq map persistent2model)
    }
  }
  //def deleteAllBy(where: Filter[Tables.Course]*): Future[Book[Course]] = deleteAllBy(where)
}

//All common slick queries are here
sealed trait CourseComponent
{
  //Queries to compile
  private def matchCourseForCompile(id: Rep[Int]) = for {
    course <- Tables.Course if course.id === id
  } yield course

  protected lazy val matchCourse = Compiled {
    matchCourseForCompile _
  }

  protected def matchAllByQuery(count: Int, filters: Seq[Filter[Tables.Course]]): Query[Tables.Course, Tables.CourseRow, Seq] = {
    matchAllByQuery(filters) take count
  }

  protected def matchAllByQuery(filters: Seq[Filter[Tables.Course]]): Query[Tables.Course, Tables.CourseRow, Seq] = {
    require(filters.nonEmpty)
    for {
      course <- Tables.Course if filters.foldLeft(filters.head(course))((x, y) => x && y(course)) //Hail to Scala and Slick magic!
    } yield course
  }

  protected def insertOrUpdateQuery(list: Seq[Tables.CourseRow]) = {
    (Tables.Course returning Tables.Course.map(_.id) into ((course, newId) => course.copy(id = newId))) ++= list
  }
}

//Some common in use filters
sealed class CourseFilters
{
  lazy val prefix = (sample: String) => new Filter[Tables.Course] {
    override def apply(row: Tables.Course): Rep[Boolean] = row.title.getOrElse("").toLowerCase.startsWith(sample)
  }

  lazy val byUniversityId = (sample: Int) => new Filter[Tables.Course] {
    override def apply(inst: Tables.Course): Rep[Boolean] = inst.universityId === sample
  }

  lazy val any = () => new Filter[Tables.Course] {
    override def apply(inst: Tables.Course): Rep[Boolean] = true
  }

  lazy val inside = (ids: Seq[Int]) => new Filter[Tables.Course] {
    override def apply(inst: Tables.Course): Rep[Boolean] = inst.id.inSet(ids)
  }
}

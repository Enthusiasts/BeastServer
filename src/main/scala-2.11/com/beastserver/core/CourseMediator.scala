package com.beastserver.core

import akka.actor.Actor
import akka.pattern.pipe
import com.beastserver.boot.Config
import com.beastserver.dao._
import com.beastserver.gen.Tables

/**
 * debal on 30.08.2015.
 */
object CourseMediator
{
  case class GetCourse(id: Int) extends Messages.RestRequest

  case class GetCourseByPrefix(prefix: String, pageSize:Int, page: Int) extends Messages.RestRequest
  case class GetCourseByPrefixWithUniversity(uni_id:Int, prefix: String, pageSize:Int, page: Int) extends Messages.RestRequest

  case class GetCourseByTop(pageSize:Int, page: Int) extends Messages.RestRequest
  case class GetCourseByTopWithUniversity(uni_id:Int, pageSize:Int, page: Int) extends Messages.RestRequest

  case class CreateCourse(title: String, uni_id: Int) extends Messages.RestRequest
  case class UpdateCourse(id: Int, title: String, uni_id: Int) extends Messages.RestRequest
  case class DeleteCourse(id: Int) extends Messages.RestRequest
}

//TODO: add caching
trait CourseMediator
{
  this: Actor with Mediator with Config =>

  private lazy val course = new CourseDAO()

  import com.beastserver.core.CourseMediator._
  def handleCourse: Receive = {
    case GetCourse(id: Int) =>
      require(id > 0)
      course.getExactlyOne(id) map {
        _.fold[Messages.RestResponse](Messages.NotFoundFailure()){Messages.SuccessResponse.WithoutMeta(_)}
      } pipeTo sender()

    case GetCourseByPrefix(prefix, pageSize, page) =>
      require(prefix.nonEmpty && pageSize > 0 && page >= 0)
      handleMessageOfSequenceByFilters(
        page,
        pageSize,
        BeastDefaults.bookSize,
        course.filters.prefix(prefix)
      ) pipeTo sender()

    case  GetCourseByPrefixWithUniversity(uni_id, prefix, pageSize, page) =>
      require(uni_id >= 0 && prefix.nonEmpty && pageSize > 0 && page >= 0)
      handleMessageOfSequenceByFilters(
        page,
        pageSize,
        BeastDefaults.bookSize,
        course.filters.prefix(prefix),
        course.filters.byUniversityId(uni_id)
      ) pipeTo sender()

    case GetCourseByTop(pageSize, page) =>
      require(pageSize > 0 && page >= 0)
      handleMessageOfSequenceByFilters(
        page,
        pageSize,
        BeastDefaults.bookSize,
        course.filters.any()
      ) pipeTo sender()

    case GetCourseByTopWithUniversity(uni_id, pageSize, page) =>
      require(uni_id >= 0 && pageSize > 0 && page >= 0)
      handleMessageOfSequenceByFilters(
        page,
        pageSize,
        BeastDefaults.bookSize,
        course.filters.byUniversityId(uni_id)
      ) pipeTo sender()

    case CreateCourse(title: String, uni_id: Int) =>
      require(title.nonEmpty && uni_id >= 0)
      //TODO: -1?
      //TODO: Handle possible duplicates
      //TODO: handle non-existing
      course.insertOrUpdateOne(Models.Course(-1, title, uni_id)) map {
        _.fold[Messages.RestResponse](Messages.InternalErrorFailure()){Messages.SuccessResponse.WithoutMeta(_)}
      } pipeTo sender()

    //TODO: handle non-existing
    case UpdateCourse(id, title: String, uni_id: Int) =>
      require(id >= 0 && title.nonEmpty && uni_id >= 0)
      course.insertOrUpdateOne(Models.Course(id, title, uni_id)) map {
        _.fold[Messages.RestResponse](Messages.InternalErrorFailure()){Messages.SuccessResponse.WithoutMeta(_)}
      } pipeTo sender()

    //TODO: handle non-existing
    case DeleteCourse(id: Int) =>
      require(id >= 0)
      course.delete(id) map {
        _.fold[Messages.RestResponse](Messages.InternalErrorFailure()){Messages.SuccessResponse.WithoutMeta(_)}
      } pipeTo sender()
  }

  private def handleMessageOfSequenceByFilters(page: Int, pageSize: Int, bookSize: Int, filters: Filter[Tables.Course]*) = {
    course.getSequenceBy(bookSize, filters) map {
      book =>
        val (content, meta) = book(page, pageSize)
        Messages.SuccessResponse.WithMeta(content, meta)
    }
  }
}

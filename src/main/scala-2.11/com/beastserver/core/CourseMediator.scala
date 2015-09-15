package com.beastserver.core

import akka.actor.Actor
import akka.pattern.pipe
import com.beastserver.dao._

/**
 * debal on 30.08.2015.
 */
object CourseMediator
{
  case class GetCourse(id: Int) extends Messages.RestRequest
  case class GetCourseByPrefix(prefix: String, count: Int) extends Messages.RestRequest
  case class GetCourseByTop(count: Int) extends Messages.RestRequest

  case class CreateCourse(title: String, uni_id: Int) extends Messages.RestRequest
  case class UpdateCourse(id: Int, title: String, uni_id: Int) extends Messages.RestRequest
  case class DeleteCourse(id: Int) extends Messages.RestRequest
}

//TODO: add paginator
trait CourseMediator
{
  this: Actor with Mediator =>

  private lazy val course = new CourseDAO()

  import com.beastserver.core.CourseMediator._
  def handleCourse: Receive = {
    case GetCourse(id: Int) =>
      course.getExactlyOne(id) map {
        _.fold[Messages.RestResponse](Messages.NotFoundFailure()){Messages.SuccessResponse.WithoutMeta(_)}
      } pipeTo sender()

    case GetCourseByPrefix(prefix, count) =>
      course.getSequenceBy(course.filters.prefix(prefix, count)) map {
        Messages.SuccessResponse.WithoutMeta(_)
      } pipeTo sender()

    case GetCourseByTop(count) =>
      course.getSequenceBy(course.filters.any(count)) map {
        Messages.SuccessResponse.WithoutMeta(_)
      } pipeTo sender()

    case CreateCourse(title: String, uni_id: Int) =>
      //TODO: -1?
      //TODO: Handle possible duplicates
      //TODO: handle non-existing
      course.insertOrUpdateOne(Models.Course(-1, title, uni_id)) map {
        _.fold[Messages.RestResponse](Messages.InternalErrorFailure()){Messages.SuccessResponse.WithoutMeta(_)}
      } pipeTo sender()

    //TODO: handle non-existing
    case UpdateCourse(id, title: String, uni_id: Int) =>
      course.insertOrUpdateOne(Models.Course(id, title, uni_id)) map {
        _.fold[Messages.RestResponse](Messages.InternalErrorFailure()){Messages.SuccessResponse.WithoutMeta(_)}
      } pipeTo sender()

    //TODO: handle non-existing
    case DeleteCourse(id: Int) =>
      course.delete(id) map {
        _.fold[Messages.RestResponse](Messages.InternalErrorFailure()){Messages.SuccessResponse.WithoutMeta(_)}
      } pipeTo sender()
  }
}

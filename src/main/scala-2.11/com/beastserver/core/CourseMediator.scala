package com.beastserver.core

import akka.actor.Actor
import akka.pattern.pipe
import com.beastserver.dao._
import com.beastserver.gen.Tables

/**
 * debal on 30.08.2015.
 */
object CourseMediator
{
  case class GetCourse(id: Int)
  case class GetCourseBy(filter: Filter[Tables.Course])

  case class DeleteCourse(id: Int)
  case class DeleteCourseBy(filter: Filter[Tables.Course])
}

class CourseMediator
{
  this: Actor with Mediator =>

  private lazy val course = new CourseDAO()

  import com.beastserver.core.CourseMediator._
  override def receive: Receive = {
    case GetCourse(id: Int) =>
      course.getExactlyOne(id) pipeTo sender()

    case GetCourseBy(filter) =>
      course.getSequenceBy(filter) pipeTo sender()

    case DeleteCourse(id: Int) =>
      course.delete(id) pipeTo sender()

    case DeleteCourseBy(filter) =>
      course.deleteAllBy(filter)
  }
}

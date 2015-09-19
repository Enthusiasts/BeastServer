package com.beastserver.route

import akka.actor.Actor
import com.beastserver.boot.Config
import spray.http.MediaTypes._

/**
 * debal on 15.09.2015.
 */
trait CourseRoute {
  this: Actor with Routing with PerRequestToMediator with Config=>

  import com.beastserver.core.CourseMediator._

  import scala.language.postfixOps
  addRoute{
    pathPrefix("course")
    {
      path(IntNumber) {
        id =>
          //Get that exactly
          get {
            //Actually creates per-request actor with current request context to complete
            //Then this per-request actor sends given message to mediator-actor
            if (id > 0)
              toMediator{
                GetCourse(id)
              }
            else respondWithInvalidArguments("id")
          }~
          //Update that exactly
          put {
            respondWithMediaType(`text/html`) { complete {<html><body>placeholder</body></html>} }
          }~
          //Delete that exactly
          delete {
            respondWithMediaType(`text/html`) { complete {<html><body>placeholder</body></html>} }
          }
      } ~
      path("top" /) {
        get {
          parameters("university_id".as[Int] ? -1, "page_size".as[Int] ? BeastDefaults.pageSize, "page_number".as[Int] ? 0) {
            (uni_id, pageSize, page) =>
              if ((uni_id >= 0 || uni_id == -1) && pageSize > 0 && page >= 0)
                toMediator{
                  if (uni_id < 0) GetCourseByTop(pageSize, page)
                  else GetCourseByTopWithUniversity(uni_id, pageSize, page)
                }
              else respondWithInvalidArguments("university_id", "page_size", "page_number")
          }
        }
      } ~
      path("prefix" / Rest) {
        prefix =>
          get {
            parameters("university_id".as[Int] ? -1, "page_size".as[Int] ? BeastDefaults.pageSize, "page_number".as[Int] ? 0) {
              (uni_id, pageSize, page) =>
                if ((uni_id > 0 || uni_id == -1) && pageSize > 0 && page >= 0 && prefix.nonEmpty)
                  toMediator{
                    val decoded = java.net.URLDecoder.decode(prefix, "UTF-8").toLowerCase
                    if (uni_id < 0) GetCourseByPrefix(decoded, pageSize, page)
                    else GetCourseByPrefixWithUniversity(uni_id, decoded, pageSize, page)
                  }
                else respondWithInvalidArguments("instance, university_id", "page_size", "page_number")
            }
          }
      }
    }
  }
}

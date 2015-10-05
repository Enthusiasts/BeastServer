package com.beastserver.route

import akka.actor.Actor
import com.beastserver.boot.Config
import com.beastserver.core.NGUniversityActor
import spray.http.MediaTypes._

/**
 * DeBalid on 12.07.2015.
 */
trait UniversityRoute extends Config
{
  this: Actor with Routing with PerRequestToMediator =>

  addRoute{
    pathPrefix("university")
    {
      path(IntNumber) {
        id =>
          //Get that exactly
          get {
            //Actually creates per-request actor with current request context to complete
            //Then this per-request actor sends given message to mediator-actor
            toUniversity{
              NGUniversityActor.GetUniversity(id)
            }
          } ~
          post {
            respondWithMediaType(`text/html`) { complete {<html><body>placeholder</body></html>} }
          }~
          //Delete that exactly
          delete {
            respondWithMediaType(`text/html`) { complete {<html><body>placeholder</body></html>} }
          }
      } ~
      path("top") {
        get {
          parameters("page_number".as[Int] ? 0, "page_size".as[Int] ? BeastDefaults.pageSize) {
            (page, pageSize) =>
              if (pageSize > 0 && page >= 0)
                toUniversity{
                  NGUniversityActor.GetUniversityByTop(page, pageSize)
                }
              else respondWithInvalidArguments("page_number", "page_size")
          }
        }
      } ~
      path("prefix" / Rest) {
        prefix =>
          get {
            parameters("page_number".as[Int] ? 0, "page_size".as[Int] ? BeastDefaults.pageSize) {
              (page, pageSize) =>
                if (pageSize > 0 && page >= 0 && prefix.nonEmpty)
                  toUniversity{
                    val decoded = java.net.URLDecoder.decode(prefix, "UTF-8").toLowerCase
                    NGUniversityActor.GetUniversityByPrefix(decoded, page, pageSize)
                  }
                else respondWithInvalidArguments("instance, university_id", "page_size", "page_number")
            }
          }
      } ~
      pathEndOrSingleSlash {
        //Create new ones
        put {
          respondWithMediaType(`text/html`) { complete {<html><body>placeholder</body></html>} }
        }
      }
    }
  }
}

package com.beastserver.route

import akka.actor.Actor
import spray.http.MediaTypes._

/**
 * debal on 15.09.2015.
 */
trait CourseRoute {
  this: Actor with Routing with PerRequestToMediator =>

  import com.beastserver.core.CourseMediator._
  addRoute{
    pathPrefix("course")
    {
      path(IntNumber) {
        id =>
          //Get that exactly
          get {
            //Actually creates per-request actor with current request context to complete
            //Then this per-request actor sends given message to mediator-actor
            toMediator{
              GetCourse(id)
            }
          }~
            //Update that exactly
            put {
              respondWithMediaType(`text/html`) { complete {<html><body>placeholder</body></html>} }
            }~
            //Delete that exactly
            delete {
              respondWithMediaType(`text/html`) { complete {<html><body>placeholder</body></html>} }
            }
      }
    }
  }
}

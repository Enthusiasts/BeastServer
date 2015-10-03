package com.beastserver.route

import akka.actor.Actor
import com.beastserver.core.NGUniversityActor
import spray.http.MediaTypes._

/**
 * DeBalid on 12.07.2015.
 */
trait UniversityRoute {
  this: Actor with Routing with PerRequestToMediator =>

  import com.beastserver.core.UniversityMediator._

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
          }~
            //Update that exactly
            post {
              respondWithMediaType(`text/html`) { complete {<html><body>placeholder</body></html>} }
            }~
            //Delete that exactly
            delete {
              respondWithMediaType(`text/html`) { complete {<html><body>placeholder</body></html>} }
            }
      } ~
        path("all" / IntNumber)
        {
          count =>
            //Get a number of these
            get {
              toMediator {
                GetUniversitySeq(count)
              }
            }
        } ~
        pathEndOrSingleSlash
        {
          //Create new ones
          put {
            respondWithMediaType(`text/html`) { complete {<html><body>placeholder</body></html>} }
          }
        }
    }
  }
}

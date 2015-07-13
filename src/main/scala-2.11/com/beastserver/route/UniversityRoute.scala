package com.beastserver.route

import akka.actor.{Actor, Props}
import com.beastserver.core.{MediatorActor, PerRequestToMediator, UniversityMediator}
import spray.http.MediaTypes._
import spray.routing.{HttpService, RequestContext}

/**
 * DeBalid on 12.07.2015.
 */
trait UniversityRoute extends HttpService {
  this: Actor with PerRequestToMediator =>

  val universityRoute = pathPrefix("university")
  {
    path(IntNumber) {
      id =>
        //Get that exactly
        get {
          //Actually creates per-request actor with current request context to complete
          //Then this per-request actor sends given message to mediator-actor
          toMediator{
            UniversityMediator.GetExactlyOne(id)
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
          case any: RequestContext =>
            createPerRequest(any, context.actorOf(Props(new MediatorActor)), UniversityMediator.GetSequence(count))
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

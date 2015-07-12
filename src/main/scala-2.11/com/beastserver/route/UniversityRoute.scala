package com.beastserver.route

import akka.actor.{Actor, Props}
import com.beastserver.dal.{DBRouteeActor, UniversityDAO}
import spray.http.MediaTypes._
import spray.routing.HttpService

/**
 * DeBalid on 12.07.2015.
 */
trait UniversityRoute extends HttpService with PerRequestCreator {
  this: Actor =>

  val route = pathPrefix("university")
  {
    path(IntNumber) {
      id =>
        //Get that exactly
        get {
          //respondWithMediaType(`text/html`) { complete{<html><body>pfff</body></html>}}
          //TODO: remove DBRoutee Actor
          case any => createPerRequest(any, context.actorOf(Props(new DBRouteeActor)), UniversityDAO.GetSequence(3))
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
          respondWithMediaType(`text/html`) { complete{<html><body>pfff to all</body></html>}}
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

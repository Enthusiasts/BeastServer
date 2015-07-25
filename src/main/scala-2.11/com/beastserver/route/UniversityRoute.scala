package com.beastserver.route

import akka.actor.{Actor, Props}
import com.beastserver.core._
import spray.http.MediaTypes._
import spray.routing.{HttpService, RequestContext}

/**
 * DeBalid on 12.07.2015.
 */
trait UniversityRoute {
  this: Actor with HttpService with PerRequestToMediator =>

  import com.beastserver.core.UniversityMediator._

  val universityRoute = pathPrefix("university")
  {
    path(IntNumber) {
      id =>
        //Get that exactly
        /**
         * @api {get} /university/:id Получение вуза по идентификатору
         * @apiName GetExactlyOneUniversity
         * @apiGroup University
         * @apiVersion 0.1.0
         *
         * @apiParam {Number} id Идентификатор вуза
         * */
        get {
          //Actually creates per-request actor with current request context to complete
          //Then this per-request actor sends given message to mediator-actor
          toMediator{
            GetExactlyOne(id)
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
        /**
         * @api {get} /university/all/:count Получение списка вузов
         * @apiName GetSequenceUniversity
         * @apiGroup University
         * @apiVersion 0.1.0
         *
         * @apiParam {Number} count Количество вузов
         * */
        get {
          toMediator {
            GetSequence(count)
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

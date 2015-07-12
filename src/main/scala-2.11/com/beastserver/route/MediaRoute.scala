package com.beastserver.route

import spray.http.MediaTypes._
import spray.routing.HttpService

/**
 * debal on 12.07.2015.
 */
trait MediaRoute extends HttpService
{
  val route = path("media" / IntNumber) { uuid =>
    get {
      respondWithMediaType(`text/html`) { complete {<html><body>placeholder</body></html>} }
    }
  }
}

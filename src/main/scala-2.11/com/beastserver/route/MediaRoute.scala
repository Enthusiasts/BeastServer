package com.beastserver.route

import akka.actor.Actor
import com.beastserver.core.PerRequestToMediator
import spray.routing.HttpService

/**
 * debal on 12.07.2015.
 */
trait MediaRoute extends HttpService
{
  this: Actor with PerRequestToMediator =>

  import com.beastserver.core.MediaMediator._

  val mediaRoute = path("media" / Rest) { uuid =>
    get {
      toMediator {
        GetExactlyOne(uuid)
      }
    }
  }
}

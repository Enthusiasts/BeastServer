package com.beastserver.route

import akka.actor.Actor
import spray.routing.HttpService

/**
 * debal on 12.07.2015.
 */
trait MediaRoute //extends HttpService
{
  this: Actor with Routing with PerRequestToMediator =>

  import com.beastserver.core.MediaMediator._

  addRoute{
    path("media" / Rest) { uuid =>
      get {
        toMediator {
          GetMedia(uuid)
        }
      }
    }
  }
}

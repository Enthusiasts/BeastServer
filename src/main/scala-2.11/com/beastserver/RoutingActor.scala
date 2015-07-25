package com.beastserver

import akka.actor.{Actor, ActorRef, Props}
import com.beastserver.route._
import spray.http.HttpHeaders.RawHeader
import spray.routing.HttpService

/**
 * DeBalid on 21.04.2015.
 */
class RoutingActor(val mediatorActor: ActorRef) extends Actor with HttpService
with PerRequestToMediator
with UniversityRoute
with MediaRoute
{
  def receive = runRoute{
    respondWithHeader(RawHeader("Access-Control-Allow-Origin", "*")) {
      universityRoute~mediaRoute
    }
  }

  def actorRefFactory = context
}

object RoutingActor
{
  def props(mediator: ActorRef): Props = Props(classOf[RoutingActor], mediator)
}
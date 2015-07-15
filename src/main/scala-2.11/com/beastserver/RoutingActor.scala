package com.beastserver

import akka.actor.{Actor, ActorRef, Props}
import com.beastserver.core.PerRequestToMediator
import com.beastserver.route._

/**
 * DeBalid on 21.04.2015.
 */
class RoutingActor(mediator: ActorRef) extends Actor with PerRequestToMediator
with UniversityRoute
with MediaRoute
{
  //Implementing PerRequestToMediator
  lazy val mediatorActor = mediator

  def receive = runRoute(universityRoute~mediaRoute)

  def actorRefFactory = context
}

object RoutingActor
{
  def props(mediator: ActorRef): Props = Props(classOf[RoutingActor], mediator)
}
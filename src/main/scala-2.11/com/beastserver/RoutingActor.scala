package com.beastserver

import akka.actor.{Actor, ActorRef, Props}
import com.beastserver.core.PerRequestToMediator
import com.beastserver.route.UniversityRoute

/**
 * DeBalid on 21.04.2015.
 */
class RoutingActor(mediator: ActorRef) extends Actor with PerRequestToMediator with UniversityRoute
{
  //Implementing PerRequestToMediator
  lazy val mediatorActor = mediator

  def receive = runRoute(universityRoute)

  def actorRefFactory = context
}

object RoutingActor
{
  def props(mediator: ActorRef): Props = Props(classOf[RoutingActor], mediator)
}
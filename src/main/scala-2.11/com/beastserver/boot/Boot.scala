package com.beastserver.boot

import akka.actor.ActorSystem
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.beastserver.RoutingActor
import com.beastserver.core.MediatorActor
import spray.can.Http

import scala.concurrent.duration._

/**
 * debalid on 21.04.2015.
 */
object Boot extends App with Config
{
  implicit val system = ActorSystem()

  val mediator = system.actorOf(MediatorActor.props(), "mediator-service")
  val service = system.actorOf(RoutingActor.props(mediator), "routing-service")

  implicit val timeout = Timeout(5.seconds)

  IO(Http) ? Http.Bind(service, beastHost, beastPort)
}

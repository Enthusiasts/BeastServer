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

  //Responsible for db connection
  val mediator = system.actorOf(MediatorActor.props(), "mediator-service")
  //Responsible for routing (suprisingly, I know)
  val service = system.actorOf(RoutingActor.props(mediator), "routing-service")

  implicit val timeout = Timeout(5.seconds)

  IO(Http) ? Http.Bind(service, beastHost, beastPort)
}

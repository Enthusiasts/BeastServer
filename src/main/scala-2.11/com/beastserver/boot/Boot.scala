package com.beastserver.boot

import akka.actor.ActorSystem
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.beastserver.core.{CryptologistActor, MediatorActor, NGMediaCacheActor, NGUniversityActor}
import com.beastserver.dao.PostgresInjection
import com.beastserver.route.RoutingActor
import slick.driver.PostgresDriver.api._
import spray.can.Http

import scala.concurrent.duration._

/**
 * debalid on 21.04.2015.
 */
object Boot extends App with Config
{
  implicit val system = ActorSystem()

  val postgres = new PostgresInjection(Database.forConfig("db"), system.dispatchers.lookup("custom-dispatcher"))

  //Responsible for db connection
  val mediator = system.actorOf(MediatorActor.props(), "mediator-service")

  val cryptologist = system.actorOf(CryptologistActor.props(), "cryptologist")

  val mediaCache = system.actorOf(NGMediaCacheActor.props(cryptologist, postgres), "media-cache")

  val university = system.actorOf(NGUniversityActor.props(cryptologist, mediaCache, postgres), "university")

  //Responsible for routing (suprisingly, I know)
  val service = system.actorOf(RoutingActor.props(mediator, university), "routing-service")

  implicit val timeout = Timeout(5.seconds)

  IO(Http) ? Http.Bind(service, BeastDefaults.beastHost, BeastDefaults.beastPort)
}

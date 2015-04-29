package com.beastserver

import akka.actor.{Props, ActorSystem}
import akka.io.IO
import akka.util.Timeout
import spray.can.Http
import akka.pattern.ask
import scala.concurrent.duration._

/**
 * Created by DeBalid on 21.04.2015.
 */
object Boot extends App
{
  implicit val system = ActorSystem()

  val service = system.actorOf(Props[MainServiceActor], "main-service")

  implicit val timeout = Timeout(5.seconds)

  IO(Http) ? Http.Bind(service, interface = "localhost", port = 80)
}

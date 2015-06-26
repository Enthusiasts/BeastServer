package com.beastserver.boot

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.beastserver.MainServiceActor
import spray.can.Http

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

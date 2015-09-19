package com.beastserver.route

import akka.actor.{Actor, ActorRef, Props}
import com.beastserver.boot.Config
import com.beastserver.util.Cors
import spray.http.MediaTypes._
import spray.http.StatusCodes
import spray.routing.{HttpService, Route}

/**
 * DeBalid on 21.04.2015.
 */
trait Routing extends HttpService
{
  private var routes: List[Route] = {
    path("empty") { complete(StatusCodes.NotFound) }//some hack
  } :: Nil
  def route: Route = routes.reduce(_~_) //ASS???
  def addRoute(inst: Route) = {
    routes = inst :: routes
  }

  def respondWithInvalidArguments(args: String*): Route = {
    respondWithMediaType(`application/json`) {
      complete (
        StatusCodes.BadRequest,
        "{\"reason\": \"One of arguments is invalid: " + args.reduce((x, y) => x + ", " + y) + ". Check http://docs.beast2.apiary.io/\"}"
      )
    }
  }
}

class RoutingActor(val mediatorActor: ActorRef) extends Actor
with Config
with Routing
with PerRequestToMediator
with UniversityRoute
with MediaRoute
with CourseRoute
with Cors
{
  def receive = runRoute{
    withCors{
      //universityRoute~mediaRoute
      route
    }
  }

  def actorRefFactory = context
}

object RoutingActor
{
  def props(mediator: ActorRef): Props = Props(classOf[RoutingActor], mediator)
}
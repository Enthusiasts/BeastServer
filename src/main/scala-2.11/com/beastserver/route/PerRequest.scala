package com.beastserver.route

import akka.actor.{Actor, ActorRef, Props, ReceiveTimeout}
import com.beastserver.boot.Config
import org.json4s.DefaultFormats
import spray.http.{StatusCode, StatusCodes}
import spray.httpx.Json4sSupport
import spray.routing.RequestContext

/**
 * debal on 12.07.2015.
 * Inspired by net-a-porter
 */
trait RestMessage //A wrapper for rest messages

trait PerRequest extends Actor with Config with Json4sSupport
{
  //Request context this route-actor should complete
  def requestContext: RequestContext
  //Core-actor it is bounded to
  def target: ActorRef
  //Message to the bounded core-actor
  def message: RestMessage

  //TODO: find out
  val json4sFormats = DefaultFormats

  //Actually what this route-actor should do
  context.setReceiveTimeout(receiveTimeout)
  target ! message

  def receive = {
    case rm : RestMessage => complete(StatusCodes.OK, rm)
    case ReceiveTimeout => complete(StatusCodes.GatewayTimeout, PerRequest.Error("Timeout"))
  }

  //Complete request
  private def complete[T <: AnyRef](status: StatusCode, content: T) = {
    //TODO: improve
    requestContext.complete(content)
    context.stop(context.self)
  }

  //TODO: REMOVE
  //implicit def
}

object PerRequest {

  case class Error(reason: String)

  case class WithActorRef(requestContext: RequestContext, target: ActorRef, message: RestMessage) extends PerRequest

  case class WithProps(requestContext: RequestContext, props: Props, message: RestMessage) extends PerRequest {
    lazy val target = context.actorOf(props)
  }
}

trait PerRequestCreator
{
  this: Actor =>

  import PerRequest._

  def createPerRequest(reqContext: RequestContext, target: ActorRef, message: RestMessage) = {
    context.actorOf(Props(new WithActorRef(reqContext, target, message)))
  }

  def perRequest(r: RequestContext, props: Props, message: RestMessage) =
    context.actorOf(Props(new WithProps(r, props, message)))
}
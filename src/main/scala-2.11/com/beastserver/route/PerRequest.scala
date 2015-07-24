package com.beastserver.route

import akka.actor.{Actor, ActorRef, Props, ReceiveTimeout}
import akka.event.Logging
import com.beastserver.boot.Config
import com.beastserver.core._
import org.json4s.DefaultFormats
import spray.http.{StatusCode, StatusCodes}
import spray.httpx.Json4sSupport
import spray.routing.{RequestContext, Route}

/**
 * debal on 12.07.2015.
 * Inspired by net-a-porter
 */

trait PerRequest extends Actor with Config with Json4sSupport
{
  //Request context this route-actor should complete
  def requestContext: RequestContext
  //Core-actor it is bounded to
  def target: ActorRef
  //Message to the bounded core-actor
  def message: RestRequest

  //TODO: find out
  val json4sFormats = DefaultFormats

  //Actually what this route-actor should do
  context.setReceiveTimeout(receiveTimeout)
  target ! message

  def receive = {
    case rr:  SuccessResponse       => complete(StatusCodes.OK, rr)
    case nff: NotFoundFailure       => complete(StatusCodes.BadRequest, FailureMessage("Such resource doesn't exist"))
    case ief: InternalErrorFailure  => complete(StatusCodes.InternalServerError, FailureMessage("Some internal error"))
    case ReceiveTimeout             => complete(StatusCodes.GatewayTimeout, FailureMessage("Timeout"))
  }

  //Complete request
  private def complete[T <: AnyRef](statusCode: StatusCode, content: T) = {
    requestContext.complete(statusCode, content)
    context.stop(context.self)
  }
}

object PerRequest
{
  //Per-request actor implementations
  case class WithActorRef(requestContext: RequestContext, target: ActorRef, message: RestRequest) extends PerRequest

  case class WithProps(requestContext: RequestContext, props: Props, message: RestRequest) extends PerRequest {
    lazy val target = context.actorOf(props)
  }
}

//Something that can create per-request actors (with sugar)
trait PerRequestCreator
{
  this: Actor =>

  import PerRequest._

  def createPerRequest(reqContext: RequestContext, target: ActorRef, message: RestRequest) = {
    context.actorOf(Props(classOf[WithActorRef], reqContext, target, message), "per-request")
  }

  def createPerRequest(r: RequestContext, props: Props, message: RestRequest) =
    context.actorOf(Props(classOf[WithProps], r, props, message), "per-request")

  //Wrappers to implement some sugar
  def perRequest(props: Props, message: RestRequest): Route =
    (r: RequestContext) => createPerRequest(r,props,message)
  def perRequest(target: ActorRef, message: RestRequest): Route =
    (r: RequestContext) => createPerRequest(r,target,message)
}

//Trait to implement some sugar in routes layer
//Actually creates per-request actor with current request context to complete
//Then this per-request actor sends given message to mediator-actor
trait PerRequestToMediator extends PerRequestCreator
{
  this: Actor =>

  def mediatorActor: ActorRef
  def toMediator(message: RestRequest): Route = {
    log.debug("toMediator!!!\n"+mediatorActor)
    perRequest(mediatorActor, message)
  }

  private val log = Logging.getLogger(this.context.system, this)
}
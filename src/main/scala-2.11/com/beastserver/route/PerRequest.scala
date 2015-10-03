package com.beastserver.route

import java.util.UUID

import akka.actor.{Actor, ActorRef, Props, ReceiveTimeout}
import akka.event.Logging
import com.beastserver.boot.Config
import com.beastserver.core._
import org.json4s.DefaultFormats
import spray.http._
import spray.httpx.Json4sSupport
import spray.httpx.marshalling.Marshaller
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
  def message: Messages.RestRequest

  val json4sFormats = DefaultFormats

  //Actually what this route-actor should do
  context.setReceiveTimeout(BeastDefaults.receiveTimeout)
  target ! message

  def receive = {
    case am:  Messages.SuccessResponse.AsMedia =>
      completeWithMedia(am.body)
    case rr:  Messages.SuccessResponse =>
      complete(StatusCodes.OK, rr)
    case nff: Messages.NotFoundFailure =>
      complete(StatusCodes.NotFound, Messages.FailureMessage("Such resource doesn't exist"))
    case ief: Messages.InternalErrorFailure =>
      complete(StatusCodes.InternalServerError, Messages.FailureMessage("Some internal error"))
    case ReceiveTimeout =>
      complete(StatusCodes.GatewayTimeout, Messages.FailureMessage("Timeout"))
  }

  //Complete request with json
  private def complete[T <: AnyRef](statusCode: StatusCode, content: T) = {
    requestContext.withHttpResponseHeadersMapped {
      list =>
        HttpHeaders.RawHeader("X-DebugInfo", "answer: rest") :: list
    } complete(statusCode, content)
    context.stop(context.self)
  }

  //Complete with http request representing a file.
  private def completeWithMedia(media: Models.Media) = {
    //Our model contains its mime-type itself, so we implicitly catch model marshaller here.
    implicit val mediaMarshaller = Marshaller.apply[Models.Media] {
      (value, ctx) =>
        ctx.marshalTo(HttpEntity(value.contentType, value.content))
    }

    requestContext.withHttpResponseHeadersMapped {
      list =>
        HttpHeaders.RawHeader("X-DebugInfo", "answer: media") :: list
    } complete media
    context.stop(context.self)
  }
}

object PerRequest
{
  //Per-request actor implementations
  case class WithActorRef(requestContext: RequestContext, target: ActorRef, message: Messages.RestRequest) extends PerRequest

  case class WithProps(requestContext: RequestContext, props: Props, message: Messages.RestRequest) extends PerRequest {
    lazy val target = context.actorOf(props)
  }
}

//Something that can create per-request actors (with sugar)
trait PerRequestCreator
{
  this: Actor =>

  import PerRequest._

  def createPerRequest(reqContext: RequestContext, target: ActorRef, message: Messages.RestRequest) = {
    context.actorOf(Props(classOf[WithActorRef], reqContext, target, message), "per-request-" + UUID.randomUUID().toString)
  }

  def createPerRequest(r: RequestContext, props: Props, message: Messages.RestRequest) =
    context.actorOf(Props(classOf[WithProps], r, props, message), "per-request-" + UUID.randomUUID().toString)

  //Wrappers to implement some sugar
  def perRequest(props: Props, message: Messages.RestRequest): Route =
    (r: RequestContext) => createPerRequest(r,props,message)
  def perRequest(target: ActorRef, message: Messages.RestRequest): Route =
    (r: RequestContext) => createPerRequest(r,target,message)
}

//Trait to implement some sugar in routes layer
//Actually creates per-request actor with current request context to complete
//Then this per-request actor sends given message to mediator-actor
trait PerRequestToMediator extends PerRequestCreator
{
  this: Actor =>

  def mediatorActor: ActorRef
  def universityActor: ActorRef

  def toMediator(message: Messages.RestRequest): Route = {
    log.debug("toMediator!!!\n"+mediatorActor)
    perRequest(mediatorActor, message)
  }

  def toUniversity(message: Messages.RestRequest): Route = {
    log.debug("toUniversity!!!\n"+universityActor)
    perRequest(universityActor, message)
  }

  private val log = Logging.getLogger(this.context.system, this)
}
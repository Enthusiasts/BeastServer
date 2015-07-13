package com.beastserver.core

import akka.actor.{Actor, ActorRef, Props}
import com.beastserver.route.PerRequest.RestRequest
import com.beastserver.route.PerRequestCreator
import slick.driver.PostgresDriver.api._
import spray.routing.Route

import scala.concurrent.ExecutionContext

/**
 * debal on 12.07.2015.
 */
//Trait to establish connection with db (and hold it)
trait Mediator
{
  implicit def db: Database
  implicit def execContext: ExecutionContext
}

//Trait to implement some sugar in routes layer
//Actually creates per-request actor with current request context to complete
//Then this per-request actor sends given message to mediator-actor
trait PerRequestToMediator extends PerRequestCreator
{
  this: Actor =>

  def mediatorActor: ActorRef
  def toMediator(message: RestRequest): Route = perRequest(mediatorActor, message)
}

object MediatorActor
{
  //Message to stop mediator
  case class Stop()

  def props(): Props = Props(classOf[MediatorActor])
}

class MediatorActor extends Actor with Mediator with UniversityMediator
{
  final lazy val db = Database.forConfig("db")
  final lazy val execContext = context.dispatcher

  def receive = handleUniversity orElse stop

  final def stop: Receive = {
    case MediatorActor.Stop =>
      db.close()
      context.stop(context.self)
  }
}
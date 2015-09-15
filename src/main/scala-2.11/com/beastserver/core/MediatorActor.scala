package com.beastserver.core

import akka.actor.{Actor, Props}
import akka.event.Logging
import com.beastserver.boot.Config
import slick.driver.PostgresDriver.api._

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

object MediatorActor
{
  //Message to stop mediator
  case class Stop()

  def props(): Props = Props(classOf[MediatorActor])
}

class MediatorActor extends Actor with Config
with Mediator
with UniversityMediator
with MediaMediator
with CourseMediator
{
  final lazy val db = Database.forConfig("db")
  final lazy val execContext = context.system.dispatchers.lookup("custom-dispatcher")

  def receive = handleUniversity orElse handleMedia orElse handleCourse orElse stop

  final def stop: Receive = {
    case MediatorActor.Stop =>
      db.close()
      context.stop(context.self)
  }

  override def preRestart(reason: Throwable, message: Option[Any]) = {
    log.debug("mediator preRestart")
    db.close()
    super.preRestart(reason, message)
  }

  override def postRestart(reason: Throwable): Unit = {
    log.debug("mediator postRestart!")
    reason.printStackTrace()
    preStart()
  }

  override def postStop() = {
    log.debug("mediator postStop")
  }

  override def preStart() = {
    log.debug("mediator preStart")
  }

  private val log = Logging.getLogger(context.system, this)
}
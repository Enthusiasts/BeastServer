package com.beastserver.dal

import akka.actor.Actor
import com.beastserver.boot.Config
import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext

/**
 * debal on 12.07.2015.
 */
class DBRouteeActor extends Actor with Config with UniversityDAORoutee
{
  //TODO: handle
  lazy val db =  Database.forURL(url = "jdbc:postgresql://%s:%d/%s".format(dbHost, dbPort, dbName),
    user = dbUser, password = dbPassword, driver = "org.postgresql.Driver")

  lazy val execContext = context.dispatcher

  def receive = handleUniversity

  def stop: Receive = {
    case DBRouteeActor.Stop =>
      db.close()
      context.stop(context.self)
  }
}

object DBRouteeActor
{
  case class Stop() // TODO: handle
}

//TODO: make a better name
trait DBRoutee
{
  implicit def db: Database
  implicit def execContext: ExecutionContext
}

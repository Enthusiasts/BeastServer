package com.beastserver.boot

import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Try

/**
 * debalid on 09.07.2015.
 */
trait Config {
  //Holding pieces together
  lazy val factory = ConfigFactory.load()

  //Beast settings
  lazy val beastHost: String  = Try {factory.getString("beast.host")} filter{_.nonEmpty} get
  lazy val beastPort: Int = Try {factory.getInt("beast.port")} filter{_ > 0} get
  lazy val receiveTimeout: Duration = (Try {factory.getInt("beast.receiveTimeout")} filter {_>0} get).seconds

  /*lazy val dbHost =     Try(factory.getString("db.host")) get
  lazy val dbPort =     Try(factory.getInt("db.port")) get
  lazy val dbName =     Try(factory.getString("db.name")) get
  lazy val dbUser =     Try(factory.getString("db.user")) get
  lazy val dbPassword = Try(factory.getString("db.password")) get*/
}

package com.beastserver.boot

import com.typesafe.config.ConfigFactory

import scala.language.postfixOps
import scala.util.Try

/**
 * Created by debal on 09.07.2015.
 */
trait Config {
  //Holding pieces together
  lazy val factory = ConfigFactory.load()

  //Service settings
  lazy val beastHost: String  = Try {factory.getString("beast.host")} filter(_.nonEmpty) get
  lazy val beastPort: Int = Try {factory.getInt("beast.port")} filter(_ > 0) get

  //Database settings using in app
  lazy val dbHost: String = Try { factory.getString("db.host") } filter (_.nonEmpty) get
  lazy val dbPort: Int = Try { factory.getInt("db.port") } filter (_ > 0) get
  lazy val dbName: String = Try { factory.getString("db.name") } filter (_.nonEmpty) get
  lazy val dbUser: String = Try { factory.getString("db.user") } filter (_.nonEmpty) get
  lazy val dbPassword: String = Try { factory.getString("db.password") } get
}

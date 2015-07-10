package com.beastserver.boot

import com.typesafe.config.ConfigFactory

import scala.language.postfixOps
import scala.util.Try

/**
 * debalid on 09.07.2015.
 */
trait Config {
  //Holding pieces together
  lazy val factory = ConfigFactory.load()

  //Service settings
  lazy val beastHost: String  = Try {factory.getString("beast.host")} filter(_.nonEmpty) get
  lazy val beastPort: Int = Try {factory.getInt("beast.port")} filter(_ > 0) get
}

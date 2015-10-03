package com.beastserver.dao

import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext

/**
 * debal on 03.10.2015.
 */
class PostgresInjection(val database: Database, val executionContext: ExecutionContext)

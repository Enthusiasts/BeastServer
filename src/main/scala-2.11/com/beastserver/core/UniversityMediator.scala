package com.beastserver.core

import java.util.{Base64, UUID}
import java.nio.ByteBuffer

import akka.actor.Actor
import akka.pattern.pipe
import com.beastserver.core.Models.University
import com.beastserver.dao.UniversityDAO

import scala.concurrent.Future

/**
 * debal on 13.07.2015.
 */

object UniversityMediator
{
  //Actor related messages
  case class GetSequence(num: Int) extends RestRequest
  case class GetExactlyOne(id: Int) extends RestRequest

  case class Sequence(content: Seq[University]) extends SuccessResponse
  case class ExactlyOne(content: University) extends SuccessResponse
}

//Used in MediatorActor
trait UniversityMediator
{
  //Helps avoid multiple inheritance
  this: Actor with Mediator =>

  private lazy val dao = new UniversityDAO()

  import UniversityMediator._

  def handleUniversity: Receive = {

    case GetSequence(x: Int) =>
      if (x > 0) {
        dao.getSequence(x) map {
          Sequence
        } recover{
          case any => InternalErrorFailure()
        } pipeTo sender()
      }
      else sender() ! NotFoundFailure()

    case GetExactlyOne(id: Int) =>
      if (id > 0) {
        /*dao.getExactlyOne(id) map {
          _.fold[RestResponse](NotFoundFailure())(ExactlyOne)*/
        val uuidFuture = Future { UUID.randomUUID() }
        val urlFuture = uuidFuture map {
          x => Base64.getEncoder.encode {
            ByteBuffer.allocate(16).putLong(x.getMostSignificantBits).putLong(x.getLeastSignificantBits).array()
          }
        }
        val result = for {
          uuid <- uuidFuture
          opt <- dao.getOneTest(id, uuid)
          url <- urlFuture
        } yield {
          opt map {
            case (x, y, z) =>
              University(x, y, url.toString)
          }
        }

        result map {
          _.fold[RestResponse](NotFoundFailure())(ExactlyOne)
        } recover {
          case any => InternalErrorFailure()
        } pipeTo sender()
      }
      else sender() ! NotFoundFailure()
  }
}

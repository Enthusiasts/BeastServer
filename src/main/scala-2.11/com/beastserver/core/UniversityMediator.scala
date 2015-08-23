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
  case class GetUniversitySeq(num: Int) extends Messages.RestRequest
  case class GetUniversity(id: Int) extends Messages.RestRequest
}

//Used in MediatorActor
trait UniversityMediator
{
  //Helps avoid multiple inheritance
  this: Actor with Mediator =>

  private lazy val dao = new UniversityDAO()

  import UniversityMediator._

  def handleUniversity: Receive = {

    /*case GetSequence(x: Int) =>
      if (x > 0) {
        dao.getSequence(x) map {
          Sequence
        } recover{
          case any => InternalErrorFailure()
        } pipeTo sender()
      }
      else sender() ! NotFoundFailure()*/

    case GetUniversity(id: Int) =>
      if (id >= 0) {
        /*dao.getExactlyOne(id) map {
          _.fold[RestResponse](NotFoundFailure())(ExactlyOne)*/
        val uuidFuture = Future { UUID.randomUUID() }
        val urlFuture = uuidFuture map {
          x => new String(Base64.getUrlEncoder.encode {
            ByteBuffer.allocate(16).putLong(x.getMostSignificantBits).putLong(x.getLeastSignificantBits).array()
          })
        }
        val result = for {
          uuid <- uuidFuture
          opt <- dao.getExactlyOne(id, uuid)
          url <- urlFuture
        } yield {
          opt map {
            case (x, y, z) =>
              University(x, y, url)
          }
        }

        result map {
          _.fold[Messages.RestResponse](Messages.NotFoundFailure())(Messages.SuccessResponse.WithoutMeta(_))
        } recover {
          case any => any.printStackTrace();Messages.InternalErrorFailure()
        } pipeTo sender()
      }
      else sender() ! Messages.NotFoundFailure()

      //TODO: think about generalizing
    case GetUniversitySeq(count: Int) =>
      if (count > 0) {
        /*dao.getExactlyOne(id) map {
          _.fold[RestResponse](NotFoundFailure())(ExactlyOne)*/
        val uuidsFuture: Future[Seq[UUID]] = Future { (0 until count) map (x => UUID.randomUUID) }

        val urlsFuture = uuidsFuture map {
          seq => seq map {
            x => new String(Base64.getUrlEncoder.encode {
              ByteBuffer.allocate(16).putLong(x.getMostSignificantBits).putLong(x.getLeastSignificantBits).array()
            })
          }
        }

        val result = for {
          uuids <- uuidsFuture
          seq <- dao.getSequenseTest(uuids)
          urls <- urlsFuture
        } yield {
            seq zip urls map {
              case (uni, url) =>
                University(uni._1, uni._2, url)
            }
          }

        result map {
          x =>
            Messages.SuccessResponse.WithMeta(x, Map{
              "length" -> x.length
            })
        } recover {
          case any => any.printStackTrace();Messages.InternalErrorFailure()
        } pipeTo sender()
      }
      else sender() ! Messages.NotFoundFailure()
  }
}

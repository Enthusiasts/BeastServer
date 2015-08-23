package com.beastserver.core

import java.nio.{BufferUnderflowException, ByteBuffer}
import java.util.{Base64, UUID}

import akka.actor.Actor
import akka.pattern.pipe
import com.beastserver.dao.MediaDAO

import scala.concurrent.Future
import scala.language.postfixOps

/**
 * debal on 15.07.2015.
 */
object MediaMediator
{
  case class GetMedia(uuid: String) extends Messages.RestRequest
}

trait MediaMediator
{
  this: Actor with Mediator =>

  lazy val mediaDAO = new MediaDAO()

  import MediaMediator._

  def handleMedia: Receive = {
    case GetMedia(input: String) =>
      if (input nonEmpty) Future {
        val decoded = Base64.getUrlDecoder.decode(input)
        //Some not trivial steps to retrieve UUID from byte array
        val bb = ByteBuffer.wrap(decoded)
        val uuid = new UUID(bb.getLong, bb.getLong)
        //log.info("decoded uuid: "+ uuid)
        //log.info("encoded in base 64: "+ Base64.getUrlEncoder.encodeToString(decoded))
        //log.info("original: "+ input)
        uuid
      } flatMap {
        mediaDAO.getExactlyOne
      } map {
        _.fold[Messages.RestResponse](Messages.NotFoundFailure())(Messages.SuccessResponse.AsMedia)
      } recover {
        //TODO: set up slick logging
        case iae: IllegalArgumentException => Messages.NotFoundFailure() //may caused by base64 decoder
        case bae: BufferUnderflowException => Messages.NotFoundFailure() //the same
        case any => any.printStackTrace(); Messages.InternalErrorFailure()
      } pipeTo sender()
  }
}

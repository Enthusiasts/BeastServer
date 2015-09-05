package com.beastserver.core

import java.nio.ByteBuffer
import java.util.{Base64, UUID}

import akka.actor.Actor
import akka.actor.Actor.Receive

/**
 * debal on 30.08.2015.
 */
class CryptologistActor extends Actor
{
  override def receive: Receive = handleIncoming

  import scala.language.postfixOps

  private def handleIncoming: Receive = {
    case CryptologistActor.CreateURL(count) =>
      require(count >= 1)
      sender() ! CryptologistActor.EncodedResult {
        (1 to count) map {
          _ =>
            val uuid = UUID.randomUUID()
            val URL = new String(Base64.getUrlEncoder.encode {
              ByteBuffer.allocate(16).putLong(uuid.getMostSignificantBits).putLong(uuid.getLeastSignificantBits).array()
            })
            (uuid, URL)
        } toMap //Scala magic!
      }
    case CryptologistActor.DecodeURL(from) =>
      sender() ! CryptologistActor.DecodedResult {
        from map {
          str =>
            val decoded = Base64.getUrlDecoder.decode(str)
            //Some not trivial steps to retrieve UUID from byte array
            val bb = ByteBuffer.wrap(decoded)
            val uuid = new UUID(bb.getLong, bb.getLong)
            (str, uuid)
        } toMap
      }
  }
}

object CryptologistActor
{
  case class CreateURL(count: Int)
  case class DecodeURL(from: Seq[String])
  case class EncodedResult(content: Map[UUID, String])
  case class DecodedResult(content: Map[String, UUID])
}

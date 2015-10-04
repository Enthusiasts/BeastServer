package com.beastserver.core

import java.util.UUID

import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.pipe
import com.beastserver.dao.{NGMediaCacheDAO, PostgresInjection}
import com.beastserver.gen.Tables

/**
 * debal on 04.10.2015.
 */
class NGMediaCacheActor(cryptologistActor: ActorRef, postgresInjection: PostgresInjection) extends Actor
{
  implicit val db = postgresInjection.database
  implicit val exec = postgresInjection.executionContext

  lazy val dao = new NGMediaCacheDAO()

  def receive: Receive = {
    case NGMediaCacheActor.CreateMediaCache(rows) =>
      if (rows.size == 1) {
        dao.createOne(Tables.MediaCacheRow(rows.head._1, rows.head._2)) map {x => NGMediaCacheActor.Done(x.toSeq)} pipeTo self
      }
      else {
        val seq = rows map Function.tupled { (x, y) => Tables.MediaCacheRow(x, y) } toSeq;
        dao.createAll(seq) map NGMediaCacheActor.Done pipeTo self
      }
      context.become(waitingMediaCache(sender()))

    case NGMediaCacheActor.ReadMediaCache(uuid) =>
      cryptologistActor ! CryptologistActor.DecodeURL(Seq(uuid))
      context.become(waitingCryptologist(sender(), uuid))

  }

  def waitingMediaCache(client: ActorRef): Receive = {
    case done: NGMediaCacheActor.Done =>
      client ! done
      context.become(receive)
  }

  def waitingCryptologist(client:ActorRef, uuid: String): Receive = {
    case CryptologistActor.DecodedResult(map) =>
      dao.readExactlyOne(map(uuid)) map {x => NGMediaCacheActor.Done(x.toSeq)} pipeTo self
      context.become(waitingMediaCache(client))
  }
}

object NGMediaCacheActor
{
  case class CreateMediaCache(map: Map[UUID, Int])
  case class ReadMediaCache(uuid: String)

  case class Done(content: Seq[Tables.MediaCacheRow])

  def props(cryptologistActor: ActorRef, postgresInjection: PostgresInjection): Props =
    Props(classOf[NGMediaCacheActor], cryptologistActor, postgresInjection)
}
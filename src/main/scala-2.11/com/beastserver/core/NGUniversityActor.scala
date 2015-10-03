package com.beastserver.core

import java.util.UUID

import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.pipe
import com.beastserver.dao.{NGUniversityDAO, PostgresInjection}
import com.beastserver.gen.Tables

/**
 * debal on 03.10.2015.
 */
class NGUniversityActor(cryptologistActor: ActorRef, postgres: PostgresInjection) extends Actor
{
  implicit val db = postgres.database
  implicit val exec = postgres.executionContext

  lazy val dao = new NGUniversityDAO()

  def receive = {
    case NGUniversityActor.GetUniversity(id) =>
      cryptologistActor ! CryptologistActor.CreateURL(1)
      dao.getExactlyOne(id) map NGUniversityActor.University pipeTo self
      context.become(waitingUniversityModel(
        sender(), Option.empty[Option[Tables.UniversityRow]], Option.empty[Map[UUID, String]]
      ))
  }

  def waitingUniversityModel (client: ActorRef,
                         uni: Option[Option[Tables.UniversityRow]],
                         uuidMap: Option[Map[UUID, String]]): Receive = {
    case NGUniversityActor.University(row) =>
      context.become(waitingUniversityModel(client, Some(row), uuidMap))
      if (uuidMap.isDefined) context.self ! NGUniversityActor.UniversityReady()

    case CryptologistActor.EncodedResult(map) =>
      context.become(waitingUniversityModel(client, uni, Some(map)))
      if (uni.isDefined)  context.self ! NGUniversityActor.UniversityReady()

    case NGUniversityActor.UniversityReady() =>
      client ! uni.get.fold[Messages.RestResponse](Messages.NotFoundFailure()){
        x =>
          Messages.SuccessResponse
            .WithoutMeta(Models.University(x.id, x.title.fold(""){x=>x}, x.title.fold(""){x=>x}))
      }
      context.become(receive)
  }
}

object NGUniversityActor
{
  case class GetUniversity(id: Int) extends Messages.RestRequest
  case class University(persist: Option[Tables.UniversityRow])
  //case class UUIDMap(map: Map[UUID, String])
  private case class UniversityReady()

  def props(cryptologist: ActorRef, postgres: PostgresInjection): Props =
    Props(classOf[NGUniversityActor], cryptologist, postgres)
}

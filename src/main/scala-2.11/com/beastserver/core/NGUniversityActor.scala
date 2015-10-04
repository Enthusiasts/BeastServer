package com.beastserver.core

import java.util.UUID

import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.pipe
import com.beastserver.boot.Config
import com.beastserver.dao.{Book, NGUniversityDAO, PostgresInjection}
import com.beastserver.gen.Tables

/**
 * debal on 03.10.2015.
 */
class NGUniversityActor(cryptologistActor: ActorRef, mediaCacheActor: ActorRef, postgres: PostgresInjection)
  extends Actor
  with Config
{
  implicit val db = postgres.database
  implicit val exec = postgres.executionContext

  lazy val dao = new NGUniversityDAO()

  def receive = {
    case NGUniversityActor.GetUniversity(id) =>
      //Tell cryptologist to make a new URL for university logo
      cryptologistActor ! CryptologistActor.CreateURL(1)

      //Get persistent model from database
      dao.readExactlyOne(id) map { x => NGUniversityActor.UniversityDone(x.toSeq) } pipeTo self

      //Combine results
      context.become(waitingUniversityModel(
        sender(),
        bookOptions = (true, 0, 0),
        Option.empty[Seq[Tables.UniversityRow]],
        Option.empty[Map[UUID, String]],
        Option.empty[Seq[Tables.MediaCacheRow]]
      ))

    case NGUniversityActor.GetUniversityByPrefix(prefix, page, pageSize) =>
      cryptologistActor ! CryptologistActor.CreateURL(BeastDefaults.bookSize)

      //Get persistent model from database
      //TODO: handle book caching (book size)
      dao.readAllBy(BeastDefaults.bookSize, Seq(dao.filters.prefix(prefix))) map {
        x => NGUniversityActor.UniversityDone(x)
      } pipeTo self

      //Combine results
      context.become(waitingUniversityModel(
        sender(),
        bookOptions = (false, page, pageSize),
        Option.empty[Seq[Tables.UniversityRow]],
        Option.empty[Map[UUID, String]],
        Option.empty[Seq[Tables.MediaCacheRow]]
      ))

    case NGUniversityActor.GetUniversityByTop(page, pageSize) =>
      cryptologistActor ! CryptologistActor.CreateURL(BeastDefaults.bookSize)

      //Get persistent model from database
      //TODO: handle book caching (book size)
      dao.readAllBy(BeastDefaults.bookSize, Seq(dao.filters.any())) map {
        x => NGUniversityActor.UniversityDone(x)
      } pipeTo self

      //Combine results
      context.become(waitingUniversityModel(
        sender(),
        bookOptions = (false, page, pageSize),
        Option.empty[Seq[Tables.UniversityRow]],
        Option.empty[Map[UUID, String]],
        Option.empty[Seq[Tables.MediaCacheRow]]
      ))
  }

  def waitingUniversityModel (client: ActorRef,
                              bookOptions: (Boolean, Int, Int), // (is head?, page number, size)
                              unis: Option[Seq[Tables.UniversityRow]],
                              uuidMap: Option[Map[UUID, String]],
                              mcs: Option[Seq[Tables.MediaCacheRow]]): Receive = {

    case NGUniversityActor.UniversityDone(row) =>
      context.become(waitingUniversityModel(client, bookOptions, Some(row), uuidMap, mcs))
      if (uuidMap.isDefined) context.self ! NGUniversityActor.AskCache()

    case CryptologistActor.EncodedResult(map) =>
      context.become(waitingUniversityModel(client, bookOptions, unis, Some(map), mcs))
      if (unis.isDefined)  context.self ! NGUniversityActor.AskCache()

    case NGUniversityActor.AskCache() =>
      if (unis.get.nonEmpty) {
        val m: Map[UUID, Int] = {
          ((uuidMap.get take unis.get.size).toSeq map (_._1) zip (unis.get map (_.mediaId))) toMap
        }

        require(m.size == unis.get.size)

        mediaCacheActor ! NGMediaCacheActor.CreateMediaCache(m)
      } else {
        client ! {
          if(bookOptions._1) Messages.NotFoundFailure()
          else {
            //TODO: better handling not found
            val (content, meta) = new Book(Seq[Models.University]())(0); Messages.SuccessResponse.WithMeta(content,meta)
          }
        }
        context.become(receive)
      }

    case NGMediaCacheActor.Done(cache) =>
      if (cache.nonEmpty) {
        context.become(waitingUniversityModel(client, bookOptions, unis, uuidMap, Some(cache)))
        context.self ! NGUniversityActor.Ready()
      }
      else {
        client ! Messages.InternalErrorFailure()
        context.become(receive)
      }

    case NGUniversityActor.Ready() =>
      val m: Seq[Models.University] = {
        (unis.get zip (uuidMap.get take unis.get.size).toSeq) map {
          case (uni, (uuid, url)) =>
            Models.University(uni.id, uni.title.fold(""){x=>x}, url)
        }
      }

      require(m.size == unis.get.size)

      val book = new Book(m)
      val (head, pageNumber, size) = bookOptions
      if (head) {
        client ! book.headOption.fold[Messages.RestResponse](Messages.InternalErrorFailure()){
          x =>
            Messages.SuccessResponse
              .WithoutMeta(x)
        }
      }
      else {
        val (content, meta) = book(pageNumber, size)
        client ! Messages.SuccessResponse.WithMeta(content, meta)
      }
      context.become(receive)

  }
}

object NGUniversityActor
{
  case class GetUniversity(id: Int) extends Messages.RestRequest
  case class GetUniversityByPrefix(prefix: String, page:Int, pageSize: Int) extends Messages.RestRequest
  case class GetUniversityByTop(page: Int, pageSize:Int) extends Messages.RestRequest

  private case class UniversityDone(persist: Seq[Tables.UniversityRow])
  private case class UniversitySeqDone(persist: Seq[Tables.UniversityRow])
  private case class AskCache()
  private case class Ready()

  def props(cryptologist: ActorRef, mediaCacheActor: ActorRef, postgres: PostgresInjection): Props =
    Props(classOf[NGUniversityActor], cryptologist, mediaCacheActor, postgres)
}

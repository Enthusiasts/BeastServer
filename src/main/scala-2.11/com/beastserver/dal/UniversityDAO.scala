package com.beastserver.dal

import akka.actor.{Actor, ActorRef}
import com.beastserver.core.Models._
import com.beastserver.gen._
import com.beastserver.route.RestMessage
import slick.driver.PostgresDriver.api._

import scala.concurrent.{ExecutionContext, Future}

/**
 * debal on 11.07.2015.
 */
//TODO: read about slick's execution contexts
class UniversityDAO (implicit val db: Database, implicit val ex: ExecutionContext)
{
  def getSequence(num: Int): Future[Seq[University]] = {
    require(num > 0)
    val future = db.run{ Tables.University.take(num).to[Seq].result }
    //TODO: think
    /*for {
      seq <- future
      uni <- seq
    } yield University(uni.id, uni.title.fold(""){x => x}, uni.title.fold(""){x => x})*/
    future.map {seq => seq.map{ uni => University(uni.id, uni.title.fold(""){x => x}, uni.title.fold(""){x => x})} }
  }

  def getSequenceOf(ids: Seq[Int]): Future[Seq[University]] = {
    require(ids.nonEmpty && !ids.exists(_ < 0))
    val future = db.run{ Tables.University.withFilter(_.id inSet ids).to[Seq].result }
    /*for {
      opt <- future
      uni <- opt
    } yield University(uni.id, uni.title.fold(""){x => x}, uni.title.fold(""){x => x})*/
    future.map {seq => seq.map{ uni => University(uni.id, uni.title.fold(""){x => x}, uni.title.fold(""){x => x})} }
  }

  def getExactlyOne(id: Int): Future[Option[University]] = getSequenceOf(Seq(id)).map{_.headOption}
}

object UniversityDAO
{
  //Actor related messages
  case class GetSequence(num: Int) extends RestMessage
  case class GetExactlyOne(id: Int) extends RestMessage

  case class Sequence(content: Seq[University]) extends RestMessage
  case class ExactlyOne(content: University) extends RestMessage
}

//Used in DBRouteeActor
trait UniversityDAORoutee extends Actor with DBRoutee
{
  import UniversityDAO._

  private lazy val dao = new UniversityDAO()

  //TODO: remove callbacks
  def handleUniversity: Receive = {
    case GetSequence(x: Int) =>
      val req: ActorRef = sender()
      dao.getSequence(x).onSuccess{ case any => req ! Sequence(any) }
    case GetExactlyOne(id: Int) =>
      val req: ActorRef = sender()
      //TODO: handle option
      dao.getExactlyOne(id).onSuccess{ case any => req ! ExactlyOne(any.get) }
  }
}

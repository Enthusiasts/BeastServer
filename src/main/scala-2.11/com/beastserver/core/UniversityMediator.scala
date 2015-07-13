package com.beastserver.core

import akka.actor.Actor
import akka.pattern.pipe
import com.beastserver.core.Models.University
import com.beastserver.dao.UniversityDAO
import com.beastserver.route.PerRequest._

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

  import com.beastserver.core.UniversityMediator._

  private lazy val dao = new UniversityDAO()

  def handleUniversity: Receive = {

    case GetSequence(x: Int) =>
      if (x > 0) {
        val resp: Future[RestResponse] = dao.getSequence(x)
          .map(Sequence)
          .recover{ case any => InternalErrorFailure() }
        resp pipeTo sender()
      }
      else sender() ! NotFoundFailure()

    case GetExactlyOne(id: Int) =>
      if (id > 0) {
        val resp: Future[RestResponse] = dao.getExactlyOne(id)
          .map{
          _.fold[RestResponse](NotFoundFailure())(ExactlyOne)
        }
          .recover { case any => InternalErrorFailure() }
        resp pipeTo sender()
      }
      else sender() ! NotFoundFailure()
  }
}

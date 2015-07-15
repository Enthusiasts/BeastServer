package com.beastserver.core

import akka.actor.Actor
import akka.pattern.pipe
import com.beastserver.core.Models.University
import com.beastserver.dao.UniversityDAO

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
        dao.getExactlyOne(id) map {
          _.fold[RestResponse](NotFoundFailure())(ExactlyOne)
        } recover {
          case any => InternalErrorFailure()
        } pipeTo sender()
      }
      else sender() ! NotFoundFailure()
  }
}

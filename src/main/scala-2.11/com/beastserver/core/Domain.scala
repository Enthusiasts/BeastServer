package com.beastserver.core

import spray.http.MediaType

/**
 * debal on 11.07.2015.
 */
object Models
{
  case class University(id: Int, name: String, image: String)
  case class Media(contentType: MediaType, content: Array[Byte])
}

//Wrappers for rest messages
trait RestRequest
sealed trait RestResponse

//Actually possible rest responses
sealed trait SuccessResponse extends RestResponse
case class FailureMessage(reason: String) extends RestResponse
case class InternalErrorFailure() extends  RestResponse
case class NotFoundFailure() extends RestResponse

//TODO: think about generalizing rest responses

object SuccessResponse {
  case class WithMeta[T](content: T, meta: Map[String, Any]) extends SuccessResponse
  case class WithoutMeta[T](content: T) extends SuccessResponse
  case class AsMedia(body: Models.Media) extends SuccessResponse
}
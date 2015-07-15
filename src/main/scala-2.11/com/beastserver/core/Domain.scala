package com.beastserver.core

import spray.http.MediaType

/**
 * debal on 11.07.2015.
 */
object Models
{
  final case class University(id: Int, name: String, image: String)
  final case class Media(contentType: MediaType, content: Array[Byte])
}

//Wrappers for rest messages
trait RestRequest
trait RestResponse

//Actually possible rest responses
trait SuccessResponse extends RestResponse
case class FailureMessage(reason: String) extends RestResponse
case class InternalErrorFailure() extends  RestResponse
case class NotFoundFailure() extends RestResponse

//TODO: think about generalizing rest responses
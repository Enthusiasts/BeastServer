package com.beastserver.util

import spray.http.HttpHeaders.`Access-Control-Allow-Origin`
import spray.http.{AllOrigins, HttpResponse, HttpMethods}
import spray.routing._

/**
 * debalid on 26.07.2015.
 * More details: https://developer.mozilla.org/en-US/docs/Web/HTTP/Access_control_CORS
 */
trait Cors
{
  this: HttpService =>

  def withCors[T]: Directive0 = mapRequestContext{
    context: RequestContext => context.withRouteResponseHandling{
      case Rejected(reasons)
        if context.request.method == HttpMethods.OPTIONS
          && reasons.exists(_.isInstanceOf[MethodRejection])
        =>
        context.complete{
          HttpResponse().withHeaders(`Access-Control-Allow-Origin`(AllOrigins))
        }
    } withHttpResponseHeadersMapped {
      headers => `Access-Control-Allow-Origin`(AllOrigins) :: headers
    }
  }


}

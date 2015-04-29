package com.beastserver

import akka.actor.{ActorRefFactory, Actor}
import spray.httpx.marshalling.ToResponseMarshallable
import spray.routing._
import spray.http._
import MediaTypes._

/**
 * Created by DeBalid on 21.04.2015.
 */
class MainServiceActor extends Actor with MainService
{
  def receive = runRoute(mainRoute)

  def actorRefFactory = context
}

trait MainService extends HttpService
{
  val mainRoute =
  path("")
  {
    get
    {
      respondWithMediaType(`text/html`)
      {
        complete
        {
          <html>
            <body>
              <h1>I'm fuckin working!</h1>
            </body>
          </html>
        }
      }
    }
  }
}

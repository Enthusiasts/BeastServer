package com.beastserver

import akka.actor.Actor
import com.beastserver.route.UniversityRoute
import spray.http.MediaTypes._
import spray.http._
import spray.routing._

/**
 * DeBalid on 21.04.2015.
 */
class MainServiceActor extends Actor /*with MainService*/ with UniversityRoute
{
  def receive = runRoute(route)

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

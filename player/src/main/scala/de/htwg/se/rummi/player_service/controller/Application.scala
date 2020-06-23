package de.htwg.se.rummi.player_service.controller

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.RejectionHandler
import akka.http.scaladsl.server.RouteResult._
import akka.stream.ActorMaterializer
import play.api.libs.json.Json

import scala.concurrent.Future

object Application {

  val PORT: Int = 8801
  val service: PlayerService = new PlayerController()

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  implicit def myRejectionHandler = RejectionHandler.newBuilder()
    .handleNotFound {
      complete((NotFound, "Player not found!"))
    }
    .result()

  private val route = get {
    path("players" / LongNumber) { id =>
      rejectEmptyResponse {
        service.getPlayer(id) match {
          case Some(p) => complete(HttpEntity(ContentTypes.`application/json`, Json.prettyPrint(Json.toJson(p))))
          case None => complete(NotFound -> s"No player with id $id")
        }
      }
    }
  }


  val bindingFuture: Future[Http.ServerBinding] = Http().bindAndHandle(route, "0.0.0.0", PORT)

  def unbind(): Unit = {
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }

  def main(args: Array[String]): Unit = {

  }
}

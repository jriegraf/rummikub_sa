package de.htwg.se.rummi.game_service

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import de.htwg.se.rummi.game_service.controller.{GameController, GameMarshaller}
import de.htwg.se.rummi.model.model.Player
import play.api.libs.json.Json

import scala.concurrent.Future
import scala.util.{Failure, Success}

object Application extends GameMarshaller {

  val PORT: Int = 8802
  val service: GameService = new GameController
  service.newGame(Player(0, "Ju") :: Player(1, "Pa") :: Nil)

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher


  //  endpoint for creating an event with tickets
  val createEventRoute: Route = {
    pathPrefix("games" / LongNumber / "activePlayer") { id ⇒
      post {
        service.gameIdToGame(id) match {
          case Failure(e) => complete(BadRequest, e.getMessage)
          case Success(game) =>
            entity(as[Player]) { player =>
              service.setActivePlayer(game, player) match {
                case Failure(e) => complete(BadRequest, e.getMessage)
                case Success(game) => complete(HttpEntity(ContentTypes.`application/json`, Json.prettyPrint(Json.toJson(game))))
              }
            }
        }
      }
    }
  }

  val route: Route = createEventRoute
  val bindingFuture: Future[Http.ServerBinding] = Http().bindAndHandle(route, "0.0.0.0", PORT)

  def unbind(): Unit = {
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }

  def main(args: Array[String]): Unit = {

  }
}
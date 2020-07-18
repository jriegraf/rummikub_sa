package de.htwg.se.rummi.game_service

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import de.htwg.se.rummi.game_service.controller.{GameController, GameMarshaller}
import de.htwg.se.rummi.model.messages.MoveTileMessage
import de.htwg.se.rummi.model.model.Player
import de.htwg.se.rummi.model.GameState.GameState
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

object GameServiceApplication extends GameMarshaller {

  private val PORT: Int = 8802
  private val service: GameService = new GameController()

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher


  val createEventRoute: Route = {
    pathPrefix("games" / LongNumber) { id ⇒

      path("getNextActivePlayer") {
        post {
          service.getGameById(id) match {
            case Failure(e) => complete(BadRequest, e.getMessage)
            case Success(game) =>
              val player = service.getNextActivePlayer(game)
              complete(HttpEntity(ContentTypes.`application/json`, Json.prettyPrint(Json.toJson(player))))
          }
        }
      } ~
        path("setActivePlayer") {
          post {
            service.getGameById(id) match {
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
        } ~
        path("setGameState") {
          post {
            service.getGameById(id) match {
              case Failure(e) => complete(BadRequest, e.getMessage)
              case Success(game) =>
                entity(as[GameState]) { gameState =>
                  service.setGameState(game, gameState) match {
                    case Failure(e) => complete(BadRequest, e.getMessage)
                    case Success(game) => complete(HttpEntity(ContentTypes.`application/json`, Json.prettyPrint(Json.toJson(game))))
                  }
                }
            }
          }
        } ~
        path("draw") {
          post {
            service.getGameById(id) match {
              case Failure(e) => complete(BadRequest, e.getMessage)
              case Success(game) =>
                service.draw(game) match {
                  case Failure(e) => complete(BadRequest, e.getMessage)
                  case Success(game) => complete(HttpEntity(ContentTypes.`application/json`, Json.prettyPrint(Json.toJson(game))))
                }
            }
          }
        } ~
        path("sortRack") {
          post {
            service.getGameById(id) match {
              case Failure(e) => complete(BadRequest, e.getMessage)
              case Success(game) =>
                service.sortRack(game) match {
                  case Failure(e) => complete(BadRequest, e.getMessage)
                  case Success(game) => complete(HttpEntity(ContentTypes.`application/json`, Json.prettyPrint(Json.toJson(game))))
                }
            }
          }
        } ~
        path("moveTile") {
          post {
            service.getGameById(id) match {
              case Failure(e) => complete(BadRequest, e.getMessage)
              case Success(game) =>
                entity(as[MoveTileMessage]) { message =>
                  service.moveTile(game, message.from, message.to, message.tile, message.newRow, message.newCol) match {
                    case Failure(e) => complete(BadRequest, e.getMessage)
                    case Success(game) => complete(HttpEntity(ContentTypes.`application/json`, Json.prettyPrint(Json.toJson(game))))
                  }
                }
            }
          }
        } ~
        pathSingleSlash {
          get {
            service.getGameById(id) match {
              case Failure(e) => complete(NotFound, e.getMessage)
              case Success(game) => complete(HttpEntity(ContentTypes.`application/json`, Json.prettyPrint(Json.toJson(game))))
            }

          }
        }
    } ~
      path("games" / "newGame") {
        println("newGame")
        post {
          entity(as[List[Player]]) { players =>
            service.newGame(players) match {
              case Failure(e) => complete(BadRequest, e.getMessage)
              case Success(game) => complete(HttpEntity(ContentTypes.`application/json`, Json.prettyPrint(Json.toJson(game))))
            }
          }
        }
      }
  }

  val route: Route = createEventRoute
  val bindingFuture: Future[Http.ServerBinding] = Http().bindAndHandle(route, "0.0.0.0", PORT)
  bindingFuture.foreach(f => println(s"GameService online at ${f.localAddress}"))

  def unbind(): Unit = {
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }

  def main(args: Array[String]): Unit = {

  }
}
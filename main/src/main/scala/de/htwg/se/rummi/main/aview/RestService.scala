package de.htwg.se.rummi.main.aview

import java.net.URLDecoder

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes.BadRequest
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import de.htwg.se.rummi.main.controller.ControllerInterface
import de.htwg.se.rummi.model.model.Game
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success, Try}

class RestService(controller: ControllerInterface) {

  implicit val system: ActorSystem = ActorSystem("my-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val route: Route = {
    pathPrefix("games" / LongNumber) { id ⇒
      path("command" / Segment) { command =>
        get {
          controller.getGameById(id) match {
            case Failure(e) => complete(BadRequest, e.getMessage)
            case Success(game) =>
              processCommand(game, URLDecoder.decode(command.trim, "UTF-8")) match {
                case Failure(e) => complete(BadRequest, e.getMessage)
                case Success(game) => complete(HttpEntity(ContentTypes.`application/json`, Json.prettyPrint(Json.toJson(game))))
              }
          }
        }
      }
    }
  }

  val bindingFuture: Future[Http.ServerBinding] = Http().bindAndHandle(route, "0.0.0.0", 8000)
  bindingFuture.foreach(f => println(s"RestService online at ${f.localAddress}"))

  def unbind(): Unit = {
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }

  def processCommand(game: Game, input: String): Try[Game] = {
    input.split(" ").toList match {
      case "z" :: Nil => controller.undo(game)
      case "y" :: Nil => controller.redo(game)
      case "sort" :: Nil => controller.sortRack(game)
      case "finish" :: Nil => controller.switchPlayer(game)
      case "draw" :: Nil => controller.draw(game)
      case from :: _ :: to :: Nil => controller.moveTile(game, from, to)
      case _ => Failure(new IllegalArgumentException("Can not parse input."))
    }
  }
}

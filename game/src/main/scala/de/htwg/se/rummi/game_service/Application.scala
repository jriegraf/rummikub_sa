package de.htwg.se.rummi.game_service

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCode}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.RejectionHandler
import akka.stream.ActorMaterializer
import de.htwg.se.rummi.game_service.controller.GameController
import de.htwg.se.rummi.model.model.Player
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.Success

object Application {

  val PORT: Int = 8802
  val service: GameService = new GameController
  service.newGame(Player(0, "Ju") :: Player(1, "Pa") :: Nil)

  implicit val system: ActorSystem = ActorSystem("player-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  implicit def myRejectionHandler = RejectionHandler.newBuilder()
    .handleNotFound {
      complete((StatusCode.int2StatusCode(404), "Game not found!"))
    }
    .result()

  private val route = post {
    path("game" / LongNumber / "draw") { id =>
      rejectEmptyResponse {
        service.draw(id) match {
          case Success(game) => complete(HttpEntity(ContentTypes.`application/json`, Json.prettyPrint(Json.toJson(game))))
          case util.Failure(e) => reject()
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

package de.htwg.se.rummi.main.aview

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import de.htwg.se.rummi.game_service.controller.fileIoComponent.jsonImpl.JsonFileIo
import de.htwg.se.rummi.main.controller.ControllerInterface
import de.htwg.se.rummi.model.model.Game

import scala.concurrent.{ExecutionContextExecutor, Future}

class RestService(controller: ControllerInterface, var game: Game) {

  implicit val system: ActorSystem = ActorSystem("my-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val route: Route = get {
    pathSingleSlash {
      complete(HttpEntity(ContentTypes.`application/json`, new JsonFileIo().gameToJson(game)))
    }
  }

  val bindingFuture: Future[Http.ServerBinding] = Http().bindAndHandle(route, "0.0.0.0", 8800)
  bindingFuture.foreach(f => println(s"RestService online at ${f.localAddress}"))

  def unbind(): Unit = {
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }


}

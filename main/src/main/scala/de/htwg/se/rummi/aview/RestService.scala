package de.htwg.se.rummi.aview

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import de.htwg.se.rummi.controller.ControllerInterface
import de.htwg.se.rummi.model.Game

import scala.concurrent.{ExecutionContextExecutor, Future}

class RestService(controller: ControllerInterface, var game: Game) {

  implicit val system: ActorSystem = ActorSystem("my-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val route: Route = get {
    pathSingleSlash {
      complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Hello World!</h1>"))
    }
  }
  val bindingFuture: Future[Http.ServerBinding] = Http().bindAndHandle(route, "localhost", 8080)

  def unbind(): Unit = {
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }


}

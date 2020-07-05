package de.htwg.se.rummi.main.controller.controllerBaseImpl

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import de.htwg.se.rummi.model.model._
import de.htwg.se.rummi.player.controller.PlayerService
import play.api.libs.json.Json

import scala.collection.immutable.List
import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.concurrent.duration._
import scala.util._

class PlayerServiceConnector extends PlayerService {

  val host = "localhost"
  val port = 8801

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  override def getPlayer(name: String): Option[Player] = {
    try {
      val request = HttpRequest(
        method = HttpMethods.GET,
        uri = s"http://$host:$port/players/$name",
        entity = HttpEntity(
          ContentTypes.`application/json`, ""
        )
      )


      val responseFuture = Http().singleRequest(request)
      val jsonString = responseFuture
        .flatMap(_.entity.toStrict(2 seconds))
        .map(_.data.utf8String)
        .map(s => Json.parse(s).as[Player])

      Some(Await.result(jsonString, 10.seconds))

    } catch {
      case e: Throwable => println(e)
        None
    }
  }

  override def getPlayers(playerNames: List[String]): Try[List[Player]] = {

    val players = playerNames.map(name => getPlayer(name))
    if (players.forall(p => p.isDefined))
      Success(players.map(p => p.get))
    else
      Failure(new NoSuchElementException())
  }
}

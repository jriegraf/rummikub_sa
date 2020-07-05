package de.htwg.se.rummi.main.controller.controllerBaseImpl

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest}
import akka.stream.ActorMaterializer
import de.htwg.se.rummi.game_service.GameService
import de.htwg.se.rummi.model.GameState.GameState
import de.htwg.se.rummi.model.messages.MoveTileMessage
import de.htwg.se.rummi.model.model.{Game, Player, Tile}
import de.htwg.se.rummi.model.util.GridType
import play.api.libs.json.Json

import scala.collection.immutable.List
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.util.{Success, Try}

class GameServiceConnector extends GameService {

  val host = "localhost"
  val port = 8802

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  override def newGame(players: List[Player]): Try[Game] = {
    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = s"http://$host:$port/games/newGame",
      entity = HttpEntity(
        ContentTypes.`application/json`, Json.prettyPrint(Json.toJson(players))
      )
    )

    val responseFuture = Http().singleRequest(request)
    val jsonString = responseFuture
      .flatMap(_.entity.toStrict(2 seconds))
      .map(_.data.utf8String).map(s => Json.parse(s).as[Game])

    Success(Await.result(jsonString, 10.seconds))
  }

  override def draw(game: Game): Try[Game] = {
    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = s"http://$host:$port/games/${game.id}/draw",
      entity = HttpEntity(
        ContentTypes.`application/json`, ""
      )
    )

    val responseFuture = Http().singleRequest(request)
    val jsonString = responseFuture
      .flatMap(_.entity.toStrict(2 seconds))
      .map(_.data.utf8String).map(s => Json.parse(s).as[Game])

    Success(Await.result(jsonString, 10.seconds))
  }

  override def moveTile(game: Game, from: GridType, to: GridType, tile: Tile, newRow: Int, newCol: Int): Try[Game] = {
    val message = MoveTileMessage(from, to, tile, newRow, newCol)
    val json = Json.prettyPrint(Json.toJson(message))
    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = s"http://$host:$port/games/${game.id}/moveTile",
      entity = HttpEntity(
        ContentTypes.`application/json`, json
      )
    )

    val responseFuture = Http().singleRequest(request)
    try {
      val jsonString = responseFuture
        .flatMap(_.entity.toStrict(2 seconds))
        .map(_.data.utf8String).map(s => Json.parse(s).as[Game])
      Success(Await.result(jsonString, 10.seconds))
    } catch {
      case e: Throwable => util.Failure(e)
    }
  }

  override def getGameById(id: Long): Try[Game] = {
    val request = HttpRequest(
      method = HttpMethods.GET,
      uri = s"http://$host:$port/games/$id/"
    )

    val responseFuture = Http().singleRequest(request)
    val jsonString = responseFuture
      .flatMap(_.entity.toStrict(2 seconds))
      .map(_.data.utf8String).map(s => Json.parse(s).as[Game])

    Success(Await.result(jsonString, 10.seconds))
  }

  override def sortRack(game: Game): Try[Game] = {
    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = s"http://$host:$port/games/${game.id}/sortRack",
      entity = HttpEntity(
        ContentTypes.`application/json`, ""
      )
    )

    val responseFuture = Http().singleRequest(request)
    val jsonString = responseFuture
      .flatMap(_.entity.toStrict(2 seconds))
      .map(_.data.utf8String).map(s => Json.parse(s).as[Game])

    Success(Await.result(jsonString, 10.seconds))
  }

  override def setActivePlayer(game: Game, player: Player): Try[Game] = ???

  override def getNextActivePlayer(game: Game): Player = ???

  override def setGameState(game: Game, gameState: GameState): Try[Game] = ???
}

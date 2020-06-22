package de.htwg.se.rummi.game_service.controller.fileIoComponent.jsonImpl

import java.io.{File, PrintWriter}

import de.htwg.se.rummi.game_service.controller.fileIoComponent.FileIoInterface
import de.htwg.se.rummi.model.model.Game
import play.api.libs.json.Json

import scala.io.Source
import scala.util.{Failure, Success, Try}

class JsonFileIo extends FileIoInterface {

  override def load(path: String): Try[Game] = {
    try {
      val bufferedSource = Source.fromFile(path)
      val content = bufferedSource.getLines.mkString
      val game = Json.parse(content).as[Game]
      bufferedSource.close
      Success(game)
    }
    catch {
      case e: Throwable => Failure(e)
    }
  }

  override def save(game: Game): Try[String] = {
    val file = new File("grid.json")
    val path = file.getAbsolutePath
    val pw = new PrintWriter(file)
    pw.write(gameToJson(game))
    pw.close()
    Success(path)
  }

  def gameToJson(game: Game): String = {
    Json.prettyPrint(Json.toJson(game))
  }

}

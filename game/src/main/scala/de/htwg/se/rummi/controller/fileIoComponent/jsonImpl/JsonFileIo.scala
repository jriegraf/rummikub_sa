package de.htwg.se.rummi.controller.fileIoComponent.jsonImpl

import de.htwg.se.rummi.controller.GameController
import de.htwg.se.rummi.controller.fileIoComponent.FileIoInterface
import de.htwg.se.rummi.model.Game
import play.api.libs.json.Json

class JsonFileIo extends FileIoInterface {
  override def load: GameController = ???

  override def save(game: Game): String = {
    import java.io._
    val pw = new PrintWriter(new File("grid.json"))
    val json = gameToJson(game)
    pw.write(json)
    pw.close()
    json
  }

  def gameToJson(game: Game): String = {
    Json.prettyPrint(Json.toJson(game))
  }


}

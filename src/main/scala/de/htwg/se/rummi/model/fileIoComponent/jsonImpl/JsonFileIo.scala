package de.htwg.se.rummi.model.fileIoComponent.jsonImpl

import de.htwg.se.rummi.controller.controllerBaseImpl.GameController
import de.htwg.se.rummi.model.fileIoComponent.FileIoInterface
import play.api.libs.json.{JsValue, Json}

class JsonFileIo extends FileIoInterface {
  override def load: GameController = ???

  override def save(game: GameController): String = {
    import java.io._
    val pw = new PrintWriter(new File("grid.json"))
    val json = Json.prettyPrint(gameToJson(game))
    pw.write(json)
    pw.close
    json
  }

  def gameToJson(game: GameController): JsValue = {
    ???
  }


}

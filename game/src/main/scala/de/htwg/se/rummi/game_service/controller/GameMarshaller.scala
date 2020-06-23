package de.htwg.se.rummi.game_service.controller

import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import de.htwg.se.rummi.model.model.Player
import play.api.libs.json.{Json, OFormat}

trait GameMarshaller extends PlayJsonSupport {

  implicit val playerFormat: OFormat[Player] = Json.format[Player]

}
package de.htwg.se.rummi.model

import de.htwg.se.rummi.controller.controllerBaseImpl.GameController
import play.api.libs.json.{JsObject, Json}

case class Player(name: String, inFirstRound: Boolean = true, var points: Int = 0) {
// TODO: Remove var and testing

  def toXml = {
    <player>
      <name>{name}</name>
      <inFirstRound>{inFirstRound}</inFirstRound>
      <points>{points}</points>
    </player>
  }


  def toJson: JsObject = {
    Json.obj(
      "name" -> name,
      "inFirstRound" -> inFirstRound,
      "points" -> points
    )
  }

  implicit val playerWrites = Json.writes[GameController]
  implicit val playerReads = Json.reads[Player]
}
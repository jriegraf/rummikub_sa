package de.htwg.se.rummi.model

case class Player(name: String) {}

object Player {

  import play.api.libs.json._

  implicit val writes = Json.writes[Player]
  implicit val reads = Json.reads[Player]
}
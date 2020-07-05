package de.htwg.se.rummi.model.model

case class Player(name: String, id: Option[Long] = None) {}

object Player {

  import play.api.libs.json._

  implicit val writes = Json.writes[Player]
  implicit val reads = Json.reads[Player]
}
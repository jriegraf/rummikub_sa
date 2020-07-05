package de.htwg.se.rummi.model.util

case class GridType(name: String) {
  override def toString: String = name
}

object GridType {

  import play.api.libs.json._

  implicit val writes: OWrites[GridType] = Json.writes[GridType]
  implicit val reads: Reads[GridType] = Json.reads[GridType]
}

object RACK extends GridType("RACK")

object FIELD extends GridType("FIELD")

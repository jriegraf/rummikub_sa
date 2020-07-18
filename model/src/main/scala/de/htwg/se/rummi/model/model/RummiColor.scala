package de.htwg.se.rummi.model.model

case class RummiColor(name: String) {
  override def toString: String = name
}

object RummiColor {

  import play.api.libs.json._

  implicit val writes: OWrites[RummiColor] = Json.writes[RummiColor]
  implicit val reads: Reads[RummiColor] = Json.reads[RummiColor]
}

object RED extends RummiColor("RED")

object BLUE extends RummiColor("BLUE")

object YELLOW extends RummiColor("YELLOW")

object GREEN extends RummiColor("GREEN")

object WHITE extends RummiColor("WHITE")


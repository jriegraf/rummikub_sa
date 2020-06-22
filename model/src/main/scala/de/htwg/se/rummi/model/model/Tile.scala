package de.htwg.se.rummi.model.model

case class Tile(number: Int, color: RummiColor, joker: Boolean = false) {

  // Necessary because there are the same tiles that we want to differentiate
  override def equals(that: Any): Boolean = {
    that match {
      case t: Tile => t.eq(this)
      case _ => false
    }
  }

  override def toString: String = {
    if (joker) "(JOKER)" else s"(${number}, ${color})"

  }
}

object Tile {

  import play.api.libs.json._

  implicit val tileWrites = Json.writes[Tile]
  implicit val tileReads = Json.reads[Tile]
}
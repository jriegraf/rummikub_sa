package de.htwg.se.rummi.model.model

import scala.util.Random

case class Tile(id: String, number: Int, color: RummiColor, joker: Boolean = false) {

  // Necessary because there are the same tiles that we want to differentiate
  override def equals(that: Any): Boolean = {
    that match {
      case t: Tile => t.id == id
      case _ => false
    }
  }

  override def toString: String = {
    if (joker) "(JOKER)" else s"(${number}, ${color})"

  }
}

object Tile {

  def apply(number: Int, color: RummiColor, joker: Boolean): Tile = {
    Tile.apply(Random.alphanumeric.take(5).mkString(""), number, color, joker)
  }

  def apply(number: Int, color: RummiColor): Tile = {
    Tile.apply(Random.alphanumeric.take(5).mkString(""), number, color)
  }

  import play.api.libs.functional.syntax._
  import play.api.libs.json._

  implicit val tileWrites = Json.writes[Tile]
  implicit val tileReads = (
    (__ \ 'id).read[String] ~
      (__ \ 'number).read[Int] ~
      (__ \ 'color).read[RummiColor] ~
      (__ \ 'joker).read[Boolean]
    ) ((id, number, color, joker) => Tile.apply(id, number, color, joker))
}
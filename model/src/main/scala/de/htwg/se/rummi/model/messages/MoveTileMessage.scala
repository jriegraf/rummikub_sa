package de.htwg.se.rummi.model.messages

import de.htwg.se.rummi.model.model.Tile
import de.htwg.se.rummi.model.util.GridType

case class MoveTileMessage(from: GridType, to: GridType, tile: Tile, newRow: Int, newCol: Int) {}

object MoveTileMessage {

  import play.api.libs.json._

  implicit val writes = Json.writes[MoveTileMessage]
  implicit val reads = Json.reads[MoveTileMessage]
}
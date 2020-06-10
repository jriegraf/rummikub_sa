package de.htwg.se.rummi.model

import de.htwg.se.rummi.Const
import play.api.libs.json._

case class Field(tiles: Map[(Int, Int), Tile]) extends Grid {
  override val rows: Int = Const.GRID_ROWS
  override val cols: Int = Const.GRID_COLS

  tiles.keys
    .find(x => x._1 >= rows || x._2 >= cols)
    .map(x => throw new IllegalArgumentException("Tile indices '" + x + "' out of bounds."))

}

object Field {
  def empty: Field = Field(Map.empty)

  implicit val fieldWrites = new Writes[Field] {
    def writes(field: Field): JsValue = {
      Json.obj(
        "rows" -> field.rows,
        "cols" -> field.cols,
        "tiles" -> JsArray(field.tiles.toList.map(mapTupleToJson))
      )
    }

    private def mapTupleToJson(tuple: ((Int, Int), Tile)): JsObject = {
      Json.obj(
        "row" -> JsNumber(tuple._1._1),
        "col" -> JsNumber(tuple._1._2),
        "tile" -> Json.toJson(tuple._2)
      )
    }
  }
}
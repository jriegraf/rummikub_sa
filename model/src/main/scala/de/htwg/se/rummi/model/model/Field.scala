package de.htwg.se.rummi.model.model

import de.htwg.se.rummi.model.Const
import play.api.libs.json._
import play.api.libs.functional.syntax._


case class Field(tiles: Map[(Int, Int), Tile], rows: Int = Const.RACK_ROWS, cols: Int = Const.RACK_COLS) extends Grid {

  tiles.keys
    .find(x => x._1 >= rows || x._2 >= cols)
    .map(x => throw new IllegalArgumentException("Tile indices '" + x + "' out of bounds."))

}

object Field {
  def empty: Field = Field(Map.empty)

  implicit val readsMap: Reads[Map[(Int, Int), Tile]] = {
    Reads.list {
      (
        (__ \ "row").read[Int] and
          (__ \ "col").read[Int] and
          (__ \ "tile").read[Tile]
        ) { (row, col, tile) => (row, col) -> tile }
    }.map(tuples => tuples.toMap)
  }

  implicit val reads: Reads[Field] = (
    (__ \ "tiles").read[Map[(Int, Int), Tile]] and
      (__ \ "rows").read[Int] and
      (__ \ "cols").read[Int]
    ) (Field.apply _)

  implicit val fieldWrites: Writes[Field] = new Writes[Field] {
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
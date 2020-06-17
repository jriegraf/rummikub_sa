package de.htwg.se.rummi.model.model

import de.htwg.se.rummi.model.Const._
import play.api.libs.json.{JsArray, JsNumber, JsObject, JsValue, Json, Writes}

case class Rack(tiles: Map[(Int, Int), Tile]) extends Grid {

  override val rows: Int = RACK_ROWS
  override val cols: Int = RACK_COLS

  tiles.keys
    .find(x => x._1 > rows || x._2 > cols)
    .map(x => throw new IllegalArgumentException("Tile indices '" + x + "' out of bounds."))

  def sortRack(): Rack = {
    var tilesByColor = tiles.values
      .groupBy(x => x.colour)
    while (tilesByColor.size > RACK_ROWS) {
      // combine colors if there are to many
      val keyOfFirstElement = tilesByColor.keys.toList.head
      val keyOfSecondElement = tilesByColor.keys.toList(1)
      val elements = tilesByColor(keyOfFirstElement) ++ tilesByColor(keyOfSecondElement)
      tilesByColor = tilesByColor + (keyOfSecondElement -> elements)
      tilesByColor = tilesByColor - tilesByColor.keys.toList.head
    }

    var newMap: Map[(Int, Int), Tile] = Map.empty
    var row = 1

    tilesByColor
      .map(x => x._2.toList)
      .foreach(listOfTiles => {
        var col = 1
        listOfTiles.sortBy(t => t.number).foreach(t => {
          newMap = newMap + ((row, col) -> t)
          col += 1
        })
        row += 1
      })
    Rack(newMap)
  }
}

object Rack {

  implicit val writes: Writes[Rack] = new Writes[Rack] {
    def writes(field: Rack): JsValue = {
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
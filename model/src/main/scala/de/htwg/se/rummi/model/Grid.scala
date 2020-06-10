package de.htwg.se.rummi.model

import play.api.libs.json.{JsArray, JsNumber, JsObject, JsValue, Json, Writes}

trait Grid {

  val rows: Int
  val cols: Int
  val tiles: Map[(Int, Int), Tile]

  def getRows = rows

  def getCols = cols

  def getTiles = tiles

  def getTileAt(row: Int, col: Int): Option[Tile] = {
    tiles.get((row, col))
  }

  def getFreeField(): Option[(Int, Int)] = {
    (1 to rows).flatMap(a => (1 to cols).map(b => (a, b)))
      .map(t => (t, getTileAt(t._1, t._2)))
      .find(t => t._2.isEmpty)
      .map(t => t._1)
  }


  def getTilePosition(tile: Tile): Option[(Int, Int)] = tiles.find(x => x._2 == tile).map(x => x._1)

  def size(): Int = tiles.size

  def copyGrid(newTiles: Map[(Int, Int), Tile]): Grid = {
    this match {
      case _: Field => Field(newTiles)
      case _: Rack => Rack(newTiles)
    }
  }

}

object Grid {
  def copyGrid(grid: Grid, newTiles: Map[(Int, Int), Tile]): Grid = {
    grid match {
      case _: Field => Field(newTiles)
      case _: Rack => Rack(newTiles)
    }
  }

  object Grid {
    def empty: Field = Field(Map.empty)

    implicit val writes: Writes[Grid] = new Writes[Grid] {
      def writes(field: Grid): JsValue = {
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

}
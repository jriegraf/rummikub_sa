package de.htwg.se.rummi.model

import de.htwg.se.rummi.Const

case class Field(tiles: Map[(Int, Int), Tile]) extends Grid{
  override val rows: Int = Const.GRID_ROWS
  override val cols: Int = Const.GRID_COLS

  tiles.keys
    .find(x => x._1 >= rows || x._2 >= cols)
    .map(x => throw new IllegalArgumentException("Tile indices '" + x + "' out of bounds."))

}

object Field {
  def empty: Field = Field(Map.empty)

}
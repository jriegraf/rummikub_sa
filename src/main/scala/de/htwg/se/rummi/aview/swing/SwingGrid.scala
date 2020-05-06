package de.htwg.se.rummi.aview.swing

import java.awt.Color

import de.htwg.se.rummi.model.{Grid, RummiSet, Tile}

import scala.collection.mutable
import scala.swing.{Dimension, GridPanel, Swing}

/**
  * Grid holds the runs and groups.
  *
  * @param ROWS Amount of rows in the grid.
  * @param COLS amount of cols in the grid.
  */
class SwingGrid(val ROWS: Int, val COLS: Int) extends GridPanel(rows0 = ROWS, cols0 = COLS) {

  var fields: List[Field] = Nil
  val setsInGrid = mutable.Map[RummiSet, (Field, Field)]()

  preferredSize = new Dimension(ROWS * 11, COLS * 11)

  for (i <- 1 to ROWS) {
    for (j <- 1 to COLS) {
      val field = new Field(i, j)
      field.border = Swing.LineBorder(Color.BLACK, 1)
      contents += field
      fields = field :: fields
    }
  }

  def getLeftField(set: RummiSet): Field = {
    setsInGrid(set)._1
  }

  def getRightField(set: RummiSet): Field = {
    setsInGrid(set)._2
  }

  /**
    * Find the set which is currently placed on this field.
    *
    * @param field the field on which the set is placed
    * @return Some if there is a set, none otherwise
    */
  def getSet(field: Field): Option[RummiSet] = {
    setsInGrid.find(x => {
      val (left, right) = x._2
      left.row == field.row && left.col <= field.col && right.col >= field.col
    }) match {
      case Some(value) => Some(value._1)
      case None => None
    }
  }

  def displayGrid(grid: Grid): Unit = {
    fields.foreach(f => f.unsetTile())
    grid.tiles.foreach(x => {
      val (r,c) = x._1
      val t = x._2
      getField(r, c).map(x => x.setTile(t))
    })

  }

  def isTileOnField(tile: Tile): Boolean = {
    fields.find(t => t.tileOpt.isDefined && t.tileOpt.get == tile).isDefined
  }

  def containsField(field: Field): Boolean = {
    fields.contains(field)
  }

  def getField(x: Int, y: Int): Option[Field] = {
    fields.find(f => (f.row == x && f.col == y))
  }

}

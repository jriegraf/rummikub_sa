package de.htwg.se.rummi.main.aview.swing

import java.awt.Color

import de.htwg.se.rummi.model._
import de.htwg.se.rummi.model.model.{BLUE, GREEN, RED, Tile, WHITE, YELLOW}

import scala.swing.{Button, Swing}

class Field(val row: Int, val col: Int) extends Button {
  var tileOpt = Option.empty[Tile]
  val WIDTH = 10
  val HEIGHT = 10
  preferredSize = new swing.Dimension(WIDTH, HEIGHT)
  minimumSize = new swing.Dimension(WIDTH, HEIGHT)
  maximumSize = new swing.Dimension(WIDTH, HEIGHT)
  unsetTile()

  def setTile(tile: Tile): Unit = {

    tileOpt = Some(tile)

    background = tile.color match {
      case YELLOW => Color.decode("#FFFF33")
      case GREEN => Color.decode("#90EE90")
      case BLUE => Color.decode("#add8e6")
      case RED => Color.decode("#ff0000")
      case WHITE => Color.decode("#000000")
      case _ => Color.BLACK;
    }

    text = tile.joker match {
      case true => "J"
      case false => tile.number.toString
    }
  }

  def unsetTile(): Unit = {
    background = Color.WHITE
    text = ""
    tileOpt = None
    border = Swing.LineBorder(Color.BLACK, 1)
  }
}

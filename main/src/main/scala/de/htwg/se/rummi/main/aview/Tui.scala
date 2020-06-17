package de.htwg.se.rummi.main.aview

import de.htwg.se.rummi.main.controller.ControllerInterface
import de.htwg.se.rummi.model.Const._
import de.htwg.se.rummi.model.model.{Game, Grid}

import scala.swing.Reactor
import scala.util.{Failure, Success, Try}

class Tui(co: ControllerInterface, var game: Game) extends Reactor {


  listenTo(co)

  def processInputLine(input: String): Unit = {
    input match {
      case "q" =>
      case "e" => //controller.createEmptyGrid
      case "n" => //controller.createNewGrid
      case "p" => printTui()
      case "z" => updateGame(game, co.undo)
      case "y" => updateGame(game, co.redo)
      case "s" => println(co.save(game))
      case "sort" => updateGame(game, co.sortRack)
      case "finish" => updateGame(game, co.switchPlayer)
      case "draw" => updateGame(game, co.draw)
      case _ => input.split(" ").toList match {
        case from :: _ :: to :: Nil => co.moveTile(game, from, to) match {
          case Success(value) => {
            game = value
            printTui()
          }
          case Failure(exception) => {
            print("Failure: ")
            println(exception.getMessage)
          }
        }
        case _ => println("Can not parse input.")
      }
    }
  }

  def updateGame(game: Game, fun: Game => Try[Game]): Unit = {
    fun(game) match {
      case Success(value) => {
        this.game = value
        printTui()
      }
      case Failure(exception) => println(exception.getMessage)
    }
  }

  def printTui(): Unit = {
    print("\n   ")
    print(('A' to ('A' + GRID_COLS - 1).toChar).mkString("  ", "  ", "\n"))

    var i = 1
    val gridStrings = printGrid(game.field, GRID_ROWS).map(x => {
      val s = f"$i%2d" + "|" + x
      i += 1
      s
    })

    val rackStrings = printGrid(game.getRackOfActivePlayer, RACK_ROWS).map(x => {
      val s = f"$i%2d" + "|" + x
      i += 1
      s
    })

    ((gridStrings :+ "\n _________________________________________\n") ::: rackStrings).foreach(x => println(x))
  }

  def printGrid(grid: Grid, amountRows: Int): List[String] = {

    var rows: List[String] = Nil
    for (i <- 1 to amountRows) {
      var row = ""
      for (j <- 1 to GRID_COLS) {
        row += " " + (grid.getTileAt(i, j) match {
          case Some(t) => if (t.number < 10) {
            " " + t.toString
          } else {
            t.toString
          }
          case None => " _"
        })
      }
      rows = rows :+ row
    }
    rows
  }
}

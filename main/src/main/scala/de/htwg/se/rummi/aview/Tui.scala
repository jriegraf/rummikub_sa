package de.htwg.se.rummi.aview

import de.htwg.se.rummi.Const._
import de.htwg.se.rummi.GameState
import de.htwg.se.rummi.controller.controllerBaseImpl.{FieldChangedEvent, GameStateChanged, PlayerSwitchedEvent, ValidStateChangedEvent}
import de.htwg.se.rummi.controller.ControllerInterface
import de.htwg.se.rummi.model.{Game, Grid}

import scala.swing.Reactor
import scala.util.{Failure, Success}

class Tui(co: ControllerInterface, var game: Game) extends Reactor {


  listenTo(co)


  def processInputLine(input: String): Unit = {
    input match {
      case "q" =>
      case "e" => //controller.createEmptyGrid
      case "n" => //controller.createNewGrid
      case "z" => co.undo(game)
      case "y" => co.redo(game)
      case "s" => print(co.save(game))
      case "sort" => co.sortRack(game)
      case "finish" => co.switchPlayer(game)
      case "draw" => co.draw(game)
      case _ => input.split(" ").toList match {
        case from :: _ :: to :: Nil => co.moveTile(game, from, to) match {
          case Success(value) => game = value
          case Failure(exception) => println(exception.getMessage)
        }
        case _ => println("Can not parse input.")
      }
    }
  }

  def printTui: Unit = {
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

  reactions += {
    case _: FieldChangedEvent => {
      printTui
    }

    case _: ValidStateChangedEvent => {
      if (game.isValid) {
        println("TUI: Field is valid again.")
      } else {
        println("TUI: Field is not valid anymore.")
      }
    }

    case _: PlayerSwitchedEvent => {
      println("It's " + game.activePlayer.name + "'s turn.")
      printTui
    }

    case _: GameStateChanged => {
      game.gameState match {
        case GameState.WON => {
          println(("---- " + game.activePlayer + " wins! ----").toUpperCase)
        }
        case _ =>
      }
    }
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

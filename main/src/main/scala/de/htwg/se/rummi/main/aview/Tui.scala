package de.htwg.se.rummi.main.aview

import de.htwg.se.rummi.main.controller.ControllerInterface
import de.htwg.se.rummi.model.Const._
import de.htwg.se.rummi.model.model._

import scala.collection.mutable
import scala.io.StdIn
import scala.swing.Reactor
import scala.util.{Failure, Success, Try}

class Tui(co: ControllerInterface, var game: Game) extends Reactor {

  val colorMap: Map[RummiColor, String] = (mutable.Map.empty += (
    RED -> "\u001B[31m",
    BLUE -> "\u001B[34m",
    YELLOW -> "\u001B[33m",
    GREEN -> "\u001B[32m",
    WHITE -> "\u001B[37m"
  )).toMap

  val REST_COLOR = "\u001B[0m"

  // start the repl
  loop()

  def processInputLine(input: String): Unit = {
    input.split(" ").toList match {
      case "q" :: Nil =>
      case "e" :: Nil => //controller.createEmptyGrid
      case "n" :: Nil => //controller.createNewGrid
      case "p" :: Nil => printTui()
      case "z" :: Nil => updateGame(game, co.undo)
      case "y" :: Nil => updateGame(game, co.redo)
      case "save" :: Nil => println(co.save(game))
      case "load" :: path :: Nil => co.load(path) match {
        case Failure(exception) => println(exception.getMessage)
        case Success(value) =>
          this.game = value
          printTui()
      }
      case "sort" :: Nil => updateGame(game, co.sortRack)
      case "finish" :: Nil => updateGame(game, co.switchPlayer)
      case "draw" :: Nil => updateGame(game, co.draw)
      case from :: _ :: to :: Nil => co.moveTile(game, from, to) match {
        case Success(value) =>
          game = value
          printTui()
        case Failure(exception) =>
          print("Failure: ")
          println(exception.getMessage)
      }
      case _ => println("Can not parse input.")
    }
  }


  private def tileColorized(tile: Tile): String = {
    s"${
      colorMap.getOrElse(tile.color, throw new NoSuchElementException)
    }${
      tile.number.toString
    }$REST_COLOR"
  }

  private def loop(): Unit = {
    var input: String = ""
    printTui()
    while (input != "q") {
      print(s"\n${
        game.activePlayer.name
      }>")
      input = StdIn.readLine()
      processInputLine(input)
    }
  }

  def updateGame(game: Game, fun: Game => Try[Game]): Unit = {
    fun(game) match {
      case Success(value) =>
        this.game = value
        printTui()
      case Failure(exception) => println(exception.getMessage)
    }
  }

  def printTui(): Unit = {
    val lettersString = "    " + ('A' to ('A' + GRID_COLS - 1).toChar).mkString("  ", "  ", "\n")
    print(lettersString)

    val addRowNumber = (t: (String, Int)) => f"${
      t._2
    }%2d |${
      t._1
    } | ${
      t._2
    }%2d"

    val rowsField = printGrid(game.field, GRID_ROWS)
    val gridStrings = rowsField.zip(1 to rowsField.size).map(addRowNumber)

    val rowsRack = printGrid(game.getRackOfActivePlayer, RACK_ROWS)
    val rackStrings = rowsRack.zip(rowsField.size + 1 to rowsField.size + 1 + rowsRack.size).map(addRowNumber)

    ((gridStrings :+ "    ________________________________________") ::: rackStrings).foreach(x => println(x))
    print(lettersString)
  }

  def printGrid(grid: Grid, amountRows: Int): List[String] = {

    var rows: List[String] = Nil
    for (i <- 1 to amountRows) {
      var row = ""
      for (j <- 1 to GRID_COLS) {
        row += " " + (grid.getTileAt(i, j) match {
          case Some(t) => if (t.number < 10) {
            " " + tileColorized(t)
          } else {
            tileColorized(t)
          }
          case None => " _"
        })
      }
      rows = rows :+ row
    }
    rows
  }
}

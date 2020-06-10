package de.htwg.se.rummi.controller.controllerBaseImpl

import de.htwg.se.rummi.controller.GameController
import de.htwg.se.rummi.model.{Game, Grid, Tile}
import de.htwg.se.rummi.util.Command

case class MoveTileCommand(gridFrom: Grid, gridTo: Grid, tile: Tile, newRow: Int, newCol: Int, gameController: GameController, game: Game) extends Command {

  val fromPosition = gridFrom.getTilePosition(tile).getOrElse(throw new NoSuchElementException)

  override def doStep = {
    gameController.moveTile(game, gridTo, tile, newRow, newCol)
  }

  override def undoStep = {
    gameController.moveTile(game, gridFrom, tile, fromPosition._1, fromPosition._2)
  }

  override def redoStep = {
    gameController.moveTile(game, gridTo, tile, newRow, newCol)
  }
}


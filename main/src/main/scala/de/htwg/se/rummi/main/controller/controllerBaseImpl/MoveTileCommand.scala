package de.htwg.se.rummi.main.controller.controllerBaseImpl

import de.htwg.se.rummi.game_service.GameService
import de.htwg.se.rummi.main.util.Command
import de.htwg.se.rummi.model.model.{Game, Tile}
import de.htwg.se.rummi.model.util.{GridType, RACK}

import scala.util.{Failure, Success, Try}

case class MoveTileCommand(game: Game, from: GridType, to: GridType, tile: Tile, toPosition: (Int, Int), gameService: GameService) extends Command {

  private val gridFrom = if (from == RACK) game.getRackOfActivePlayer else game.field
  private val fromPosition = gridFrom.getTilePosition(tile).getOrElse(throw new NoSuchElementException)
  private var thisGame = game

  override def doStep: Try[Game] = {
    gameService.moveTile(thisGame, from, to, tile, toPosition._1, toPosition._2) match {
      case Success(g) =>
        thisGame = g
        Success(g)
      case Failure(exception) => Failure(exception)
    }
  }

  override def undoStep: Try[Game] = {
    gameService.moveTile(thisGame, to, from, tile, fromPosition._1, fromPosition._2) match {
      case Success(g) =>
        thisGame = g
        Success(g)
      case Failure(exception) => Failure(exception)
    }
  }

  override def redoStep: Try[Game] = {
    gameService.moveTile(thisGame, from, to, tile, toPosition._1, toPosition._2) match {
      case Success(g) =>
        thisGame = g
        Success(g)
      case Failure(exception) => Failure(exception)
    }
  }
}


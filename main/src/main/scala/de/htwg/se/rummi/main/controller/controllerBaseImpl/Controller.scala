package de.htwg.se.rummi.main.controller.controllerBaseImpl

import java.util.NoSuchElementException

import com.google.inject.{Guice, Inject, Injector}
import de.htwg.se.rummi.game_service.controller.GameController
import de.htwg.se.rummi.game_service.controller.fileIoComponent.FileIoInterface
import de.htwg.se.rummi.main.RummiModule
import de.htwg.se.rummi.main.controller.ControllerInterface
import de.htwg.se.rummi.main.util.UndoManager
import de.htwg.se.rummi.model.Const._
import de.htwg.se.rummi.model.GameState._
import de.htwg.se.rummi.model.model.{Game, Grid, Player, Tile}
import de.htwg.se.rummi.model.{AlreadyDrawnException, FieldNotValidException, InvalidGameStateException}
import de.htwg.se.rummi.player_service.controller.{PlayerController, PlayerService}

import scala.util.{Failure, Success, Try}

class Controller @Inject()() extends ControllerInterface {


  private val undoManager = new UndoManager
  val injector: Injector = Guice.createInjector(new RummiModule)
  val fileIo: FileIoInterface = injector.getInstance(classOf[FileIoInterface])

  val gameController = new GameController()
  val playerController : PlayerService = new PlayerController()

  def createGame(playerNames: List[String]): Try[Game] = {
    if (playerNames == Nil) Failure(new NoSuchElementException())
    gameController.newGame(playerController.getPlayers(playerNames)) match {
      case Failure(err) => throw err
      case Success(game) => {
        Success(game)
      }
    }
  }

  def save(game: Game): String = {
    fileIo.save(game)
  }

  def players(game: Game): List[Player] = {
    game.playerParticipations.map(pp => pp.player)
  }

  def undo(game: Game): Try[Game] = {
    undoManager.undoStep
  }

  def redo(game: Game): Try[Game] = {
    undoManager.redoStep
  }

  def switchPlayer(game: Game): Try[Game] = {

    // RULES FOR SWITCHING PLAYER
    // 1. Player has drawn and no tiles moved
    // or
    // 2. Field is valid but player has moved at least one tile to the grid

    if (game.gameState == DRAWN && game.countMovedTiles > 0) {
      return Failure(InvalidGameStateException("You can not draw and move tiles to the field."))
    }

    if (game.gameState != DRAWN && game.countMovedTiles == 0){
      return Failure(FieldNotValidException("You have to place at least one tile on the field or draw."))
    }

    if (game.gameState != VALID && game.countMovedTiles > 0) {
      return Failure(FieldNotValidException("The Field ist not valid, resolve first."))
    }

    gameController.setActivePlayer(game, gameController.getNextActivePlayer(game)) match {
      case Success(g) => gameController.setGameState(g, WAITING)
      case Failure(x) => Failure(x)
    }
  }


  /**
   * Draw: If the player can not place a stone on the field, he must take a stone from the stack of covered stones.
   */
  def draw(game: Game): Try[Game] = {
    if (game.countMovedTiles > 0) return Failure(new InvalidGameStateException("You have already placed tiles on the field."))
    if (game.gameState == DRAWN) return Failure(new AlreadyDrawnException("Already drawn."))
    gameController.draw(game)
  }


  def moveTile(game: Game, from: String, to: String): Try[Game] = {
    val (f, t) = coordsToFields(from, to).getOrElse(throw new NoSuchElementException("No such field."))
    val field = game.field
    if (f._1 <= GRID_ROWS) {
      val tile: Tile = field.getTileAt(f._1, f._2) match {
        case Some(e) => e
        case None => return Failure(new NoSuchElementException("There is no tile on field " + from))
      }
      if (t._1 <= GRID_ROWS) {
        gameController.moveTile(game, field, tile, t._1, t._2)
      } else {
        gameController.moveTile(game, game.getRackOfActivePlayer, tile, t._1 - GRID_ROWS, t._2)
      }
    } else {
      val tile = game.getRackOfActivePlayer.getTileAt(f._1 - GRID_ROWS, f._2) match {
        case Some(e) => e
        case None => return Failure(new NoSuchElementException("There is no tile on field " + from))
      }
      if (t._1 <= GRID_ROWS) {
        gameController.moveTile(game, field, tile, t._1, t._2)
      } else {
        gameController.moveTile(game, game.getRackOfActivePlayer, tile, t._1 - GRID_ROWS, t._2)
      }
    }
  }

  def toColNumber(col: Char): Option[Int] = {
    val ret = col - 65
    if (ret >= 0 && ret <= GRID_COLS) {
      return Some(ret + 1)
    }
    None
  }

  def coordsToFields(from: String, to: String): Option[((Int, Int), (Int, Int))] = {
    val fromChars = from.toList
    val toChars = to.toList

    val toRow: Int = toChars.filter(x => x.isDigit).mkString("").toInt
    val fromRow: Int = fromChars.filter(x => x.isDigit).mkString("").toInt

    val fromCol: Int = toColNumber(fromChars.head.charValue()) match {
      case Some(c) => c
      case None => {
        return None
      }
    }
    val toCol: Int = toColNumber(toChars.head.charValue()) match {
      case Some(c) => c
      case None =>
        return None
    }
    Some((fromRow, fromCol), (toRow, toCol))
  }

  def moveTile(game: Game, gridFrom: Grid, gridTo: Grid, tile: Tile, newRow: Int, newCol: Int): Try[Game] = {
    undoManager.doStep(new MoveTileCommand(gridFrom, gridTo, tile, newRow, newCol, gameController, game)) match {
      case Success(newGame) => setGameStateAfterMoveTile(newGame)
      case Failure(x) => Failure(x)
    }
  }

  private def setGameStateAfterMoveTile(game: Game): Try[Game] = {

    if (game.countMovedTiles == 0) return Success(game.setGameState(WAITING))

    if (!game.isValid) return Success(game.setGameState(INVALID))

    if (game.getRackOfActivePlayer.tiles.isEmpty) return Success(game.setGameState(WON))

    if (game.isPlayerInFirstRound(game.activePlayer) && !game.doesPlayerReachedMinLayOutPoints)
      return Success(game.setGameState(TO_LESS))

    Success(game.setGameState(VALID))

  }

  override def sortRack(game: Game): Try[Game] = {
    Success(game.updateParticipationOfActivePlayer(
      game.getParticipationOfActivePlayer.copy(rack = game.getRackOfActivePlayer.sortRack())))
  }

}

package de.htwg.se.rummi.main.controller.controllerBaseImpl

import java.util.NoSuchElementException

import com.google.inject.{Guice, Inject, Injector}
import de.htwg.se.rummi.game_service.GameService
import de.htwg.se.rummi.game_service.controller.fileIoComponent.FileIoInterface
import de.htwg.se.rummi.main.RummiModule
import de.htwg.se.rummi.main.controller.ControllerInterface
import de.htwg.se.rummi.main.util.UndoManager
import de.htwg.se.rummi.model.Const._
import de.htwg.se.rummi.model.GameState._
import de.htwg.se.rummi.model.model._
import de.htwg.se.rummi.model.util.GridType
import de.htwg.se.rummi.model.{AlreadyDrawnException, Const, FieldNotValidException, InvalidGameStateException}
import de.htwg.se.rummi.player.controller.{PlayerController, PlayerService}

import scala.util.{Failure, Success, Try}

class Controller @Inject()() extends ControllerInterface {

  private val undoManager = new UndoManager
  val injector: Injector = Guice.createInjector(new RummiModule)
  val fileIo: FileIoInterface = injector.getInstance(classOf[FileIoInterface])

  val gameController : GameService = new GameServiceConnector()
  val playerController: PlayerService = new PlayerController()

  override def getGameById(id: Long): Try[Game] = {
    gameController.getGameById(id)
  }

  def createGame(playerNames: List[String]): Try[Game] = {
    if (playerNames == Nil) Failure(new NoSuchElementException())
    gameController.newGame(playerController.getPlayers(playerNames)) match {
      case Failure(err) => throw err
      case Success(game) => {
        Success(game)
      }
    }
  }

  override def save(game: Game): Try[String] = {
    fileIo.save(game)
  }

  override def load(path: String): Try[Game] = {
    fileIo.load(path)
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

    if (game.gameState != DRAWN && game.countMovedTiles == 0) {
      return Failure(FieldNotValidException("You have to place at least one tile on the field or draw."))
    }

    if (game.gameState == TO_LESS) {
      return Failure(FieldNotValidException(s"For your first move, you must play a set or run with a " +
        s"value of at least ${Const.MINIMUM_POINTS_FIRST_ROUND} points"))
    }

    if (game.gameState != VALID && game.countMovedTiles > 0) {
      return Failure(FieldNotValidException("The Field ist not valid, resolve first."))
    }

    gameController.setActivePlayer(game, gameController.getNextActivePlayer(game)) match {
      case Success(g) =>
        undoManager.reset()
        gameController.setGameState(g, WAITING)
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

  private def getGridFrom(game: Game, tile: Tile): Try[Grid] = {
    var gridFrom: Grid = null
    if (game.field.tiles.values.exists(t => t == tile)) {
      Success(game.field.asInstanceOf[Grid])
    } else if (game.getRackOfActivePlayer.tiles.values.exists(t => t == tile)) {
      Success(game.getRackOfActivePlayer.asInstanceOf[Grid])
    } else {
      Failure(new NoSuchElementException("Tile not found in rack."))
    }
  }

  private def isInField(row: Int): Boolean = {
    row <= GRID_ROWS
  }

  private def convertToRackRow(row: Int): Int = {
    if (isInField(row)) row else row - GRID_ROWS
  }

  def moveTile(game: Game, from: String, to: String): Try[Game] = {
    val (coordFrom, coordTo) = coordsToFields(from, to).getOrElse(
      return Failure(new NoSuchElementException("No such field.")))

    val field = game.field
    val rack = game.getRackOfActivePlayer

    val fromGrid: Grid = if (isInField(coordFrom._1)) field else rack
    val toGrid: Grid = if (isInField(coordTo._1)) field else rack

    val tile = fromGrid.getTileAt(convertToRackRow(coordFrom._1), coordFrom._2) match {
      case Some(e) => e
      case None => return Failure(new NoSuchElementException("There is no tile on field " + from))
    }

    moveTile(game, fromGrid.getType, toGrid.getType, tile, convertToRackRow(coordTo._1), coordTo._2)
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

  def moveTile(game: Game, gridFrom: GridType, gridTo: GridType, tile: Tile, newRow: Int, newCol: Int): Try[Game] = {
    undoManager.doStep(MoveTileCommand(game, gridFrom, gridTo, tile, (newRow, newCol), gameController)) match {
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
    gameController.sortRack(game)
  }

}

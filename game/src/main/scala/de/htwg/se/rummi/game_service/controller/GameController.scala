package de.htwg.se.rummi.game_service.controller

import java.util.NoSuchElementException

import de.htwg.se.rummi.game_service.GameService
import de.htwg.se.rummi.model.GameState.{DRAWN, GameState}
import de.htwg.se.rummi.model.model._
import de.htwg.se.rummi.model.util.{GridType, RACK}
import de.htwg.se.rummi.model.{Const, FieldIsOccupiedException, model}

import scala.util.{Failure, Success, Try}

class GameController() extends GameService {

  var games: List[Game] = Nil

  override def gameIdToGame(id: Long): Try[Game] = {
    games.find(g => g.id == id) match {
      case Some(game) => Success(game)
      case None => Failure(new NoSuchElementException("No such game."))
    }
  }

  override def setGameState(game: Game, gameState: GameState): Try[Game] = {
    returnSuccess(game, game.copy(gameState = gameState))
  }

  override def newGame(players: List[Player]): Try[Game] = {

    var coveredTiles: List[Tile] = List.empty
    var racks: List[Rack] = Nil

    for (i <- Const.LOWEST_NUMBER to Const.HIGHEST_NUMBER) {
      coveredTiles = new Tile(i, RED) :: coveredTiles
      coveredTiles = new Tile(i, RED) :: coveredTiles
      coveredTiles = new Tile(i, BLUE) :: coveredTiles
      coveredTiles = new Tile(i, BLUE) :: coveredTiles
      coveredTiles = new Tile(i, GREEN) :: coveredTiles
      coveredTiles = new Tile(i, GREEN) :: coveredTiles
      coveredTiles = new Tile(i, YELLOW) :: coveredTiles
      coveredTiles = new Tile(i, YELLOW) :: coveredTiles
    }

    coveredTiles = (1 to 2 map (i => new Tile(i, WHITE, true))).toList ::: coveredTiles

    coveredTiles = scala.util.Random.shuffle(coveredTiles)

    players.foreach(_ => {
      // Take 14 tiles and add them to the rack of the player
      var tilesAddToRack = coveredTiles.take(Const.NUMBER_OF_INITIAL_RACK_TILES)

      // Remove the tiles added to the rack from the coveredTiles list
      coveredTiles = coveredTiles.filter(t => !tilesAddToRack.contains(t))

      var map: Map[(Int, Int), Tile] = Map.empty
      var i, j = 1

      while (tilesAddToRack.nonEmpty) {
        map = map + ((i, j) -> tilesAddToRack.head)
        tilesAddToRack = tilesAddToRack.drop(1)
        j += 1
        if (j > Const.RACK_COLS) {
          j = 1
          i += 1
        }
      }
      racks = racks :+ model.Rack(map)
    })

    val game = new Game(players.head,
      Field.empty,
      coveredTiles,
      players.map(p => PlayerParticipation(p, racks(players.indexOf(p)))), games.size
    )

    games = games :+ game
    Success(game)
  }

  override def draw(game: Game): Try[Game] = {
    val newTile = game.coveredTiles.head

    val p = game.playerParticipations.filter(p => p.player == game.activePlayer).head

    val newRack = p.rack.getFreeField match {
      case Some(freeField) => p.rack.copy(tiles = p.rack.tiles + (freeField -> newTile))
      case None => throw new NoSuchElementException("No space in rack left.")
    }

    val newParticipations = game.playerParticipations.updated(game.playerParticipations.indexOf(p), p.copy(rack = newRack.asInstanceOf[Rack]))

    val newGame = game.copy(
      coveredTiles = game.coveredTiles.filter(x => x != newTile),
      playerParticipations = newParticipations,
      gameState = DRAWN)

    returnSuccess(game, newGame)
  }

  override def setActivePlayer(game: Game, player: Player): Try[Game] = {
    val newGame = game.copy(activePlayer = player).copy(movedTiles = Nil)
    returnSuccess(game, newGame)
  }

  def getNextActivePlayer(game: Game): Player = {
    val players: List[Player] = game.playerParticipations.map(p => p.player).sortBy(p => p.name)
    val nextPlayerIndex = players.indexOf(game.activePlayer) + 1
    if (nextPlayerIndex >= players.size) {
      return players.head
    }
    players(nextPlayerIndex)
  }

  override def moveTile(game: Game, from: GridType, to: GridType, tile: Tile, newRow: Int, newCol: Int): Try[Game] = {

    val gridFrom = if (from == RACK) game.getRackOfActivePlayer else game.field
    val gridTo = if (to == RACK) game.getRackOfActivePlayer else game.field

    moveTile(game, gridFrom, gridTo, tile, newRow, newCol)
  }

  private def checkCoordInBounds(row: Int, col: Int, grid: Grid): Boolean = {
    if (row > grid.rows) return false
    if (col > grid.cols) return false
    true
  }

  private def moveTile(game: Game, gridFrom: Grid, gridTo: Grid, tile: Tile, newRow: Int, newCol: Int): Try[Game] = {

    if (checkCoordInBounds(newRow, newCol, gridTo))
      return Failure(new IllegalArgumentException("Coords not in grid bounds."))

    moveTileImpl(gridFrom, gridTo, tile, newRow, newCol) match {
      case Failure(x) => Failure(x)
      case Success(newGrids) => newGrids match {
        case (field: Field, rack: Rack) =>
          val part = game.getParticipationOfActivePlayer.copy(rack = rack)
          val newGame = game.updateParticipationOfActivePlayer(part)
            .copy(field = field)
            .copy(movedTiles = game.movedTiles.filter(x => x != tile))
          returnSuccess(game, newGame)
        case (field: Field, _: Field) =>
          returnSuccess(game, game.copy(field = field))
        case (rack: Rack, field: Field) =>

          val part = game.getParticipationOfActivePlayer.copy(rack = rack)
          val newGame = game.updateParticipationOfActivePlayer(part)
            .copy(field = field)
            .copy(movedTiles = game.movedTiles :+ tile)
          returnSuccess(game, newGame)
        case (rack: Rack, _: Rack) =>
          val part = game.getParticipationOfActivePlayer.copy(rack = rack)
          returnSuccess(game, game.updateParticipationOfActivePlayer(part))
      }
    }
  }

  private def moveTileImpl(gridFrom: Grid, gridTo: Grid, tile: Tile, newRow: Int, newCol: Int): Try[(Grid, Grid)] = {
    if (gridTo.getTileAt(newRow, newCol).isDefined)
      return Failure(FieldIsOccupiedException(s"Can not move tile $tile because target field is occupied."))

    gridFrom.getTilePosition(tile) match {
      case Some(x) =>
        if (gridTo == gridFrom) {
          // tile is moved within the same grid
          val tiles = gridFrom.getTiles - x + ((newRow, newCol) -> tile)
          Success((gridFrom.copyGrid(tiles), gridTo.copyGrid(tiles)))
        } else {
          Success(gridFrom.copyGrid(gridFrom.getTiles - x), gridTo.copyGrid(gridTo.getTiles + ((newRow, newCol) -> tile)))
        }
      case None => Failure(new NoSuchElementException("Tile not found in rack."))
    }
  }

  private def returnSuccess(game: Game, newGame: Game): Try[Game] = {
    games = games.updated(games.indexOf(game), newGame)
    Success(newGame)
  }
}
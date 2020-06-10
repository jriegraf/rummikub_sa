package de.htwg.se.rummi.controller.controllerBaseImpl

import java.util.NoSuchElementException

import de.htwg.se.rummi.Const
import de.htwg.se.rummi.controller.GameState.{DRAWN, GameState}
import de.htwg.se.rummi.model._

import scala.util.{Failure, Success, Try}

class GameController() {

  var games: List[Game] = Nil

  def setGameState(game: Game, gameState: GameState) = {
    returnSuccess(game, game.copy(gameState = gameState))
  }

  def newGame(players: List[Player]): Try[Game] = {

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

    players.foreach(p => {
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
      racks = racks :+ Rack(map)
    })

    val game = new Game(players.head,
      Field.empty,
      coveredTiles,
      players.map(p => PlayerParticipation(p, racks(players.indexOf(p)))), players.size
    )

    games = games :+ game
    println("#Games: " + games.size)
    Success(game)
  }


  def draw(game: Game): Try[Game] = {
    val newTile = game.coveredTiles.head

    val p = game.playerParticipations.filter(p => p.player == game.activePlayer).head

    val newRack = p.rack.getFreeField() match {
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

  def setActivePlayer(game: Game, player: Player): Try[Game] = {
    val newGame = game.copy(activePlayer = player)
    returnSuccess(game, newGame)
  }


  def moveTile(game: Game, gridTo: Grid, tile: Tile, newRow: Int, newCol: Int): Try[Game] = {

    var gridFrom: Grid = null
    if (game.field.tiles.values.exists(t => t == tile)) {
      gridFrom = game.field.asInstanceOf[Grid]
    } else if (game.getRackOfActivePlayer.tiles.values.exists(t => t == tile)) {
      gridFrom = game.getRackOfActivePlayer.asInstanceOf[Grid]
    } else {
      return Failure(new NoSuchElementException("Tile not found in rack."))
    }

    moveTileImpl(gridFrom, gridTo, tile, newRow, newCol) match {
      case Failure(x) => Failure(x)
      case Success(x) => x match {
        case (field: Field, rack: Rack) => {
          val part = game.getParticipationOfActivePlayer.copy(rack = rack)
          val newGame = game.updateParticipationOfActivePlayer(part)
            .copy(field = field)
            .copy(turn = game.turn.copy(movedTiles = game.turn.movedTiles.filter(x => x != tile)))
          returnSuccess(game, newGame)
        }
        case (field: Field, _: Field) => {
          returnSuccess(game, game.copy(field = field))
        }
        case (rack: Rack, field: Field) => {

          val part = game.getParticipationOfActivePlayer.copy(rack = rack)
          val newGame = game.updateParticipationOfActivePlayer(part)
            .copy(field = field)
            .copy(turn = game.turn.copy(movedTiles = game.turn.movedTiles :+ tile))
          returnSuccess(game, newGame)
        }
        case (rack: Rack, _: Rack) => {
          val part = game.getParticipationOfActivePlayer.copy(rack = rack)
          returnSuccess(game, game.updateParticipationOfActivePlayer(part))
        }
      }
    }
  }

  private def moveTileImpl(gridFrom: Grid, gridTo: Grid, tile: Tile, newRow: Int, newCol: Int): Try[(Grid, Grid)] = {
    if (gridTo.getTileAt(newRow, newCol).isDefined) return Failure(new FieldIsOccupiedException)

    gridFrom.getTilePosition(tile) match {
      case Some(x) =>
        if (gridTo == gridFrom) {
          // tile is moved within the same grid
          val tiles = gridFrom.getTiles - x + ((newRow, newCol) -> tile)
          Success((gridFrom.copyGrid(tiles), gridTo.copyGrid(tiles)))
        } else {
          Success(gridFrom.copyGrid(gridFrom.getTiles - (x)), gridTo.copyGrid(gridTo.getTiles + ((newRow, newCol) -> tile)))
        }
      case None => Failure(new NoSuchElementException("Tile not found in rack."))
    }
  }

  private def returnSuccess(game: Game, newGame: Game): Try[Game] = {
    games = games.updated(games.indexOf(game), newGame)
    Success(newGame)
  }
}
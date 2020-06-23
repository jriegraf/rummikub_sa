package de.htwg.se.rummi.game_service

import de.htwg.se.rummi.model.GameState.GameState
import de.htwg.se.rummi.model.model.{Game, Player, Tile}
import de.htwg.se.rummi.model.util.GridType

import scala.util.Try

trait GameService {
  def gameIdToGame(id: Long): Try[Game]

  def setGameState(game: Game, gameState: GameState): Try[Game]

  def newGame(players: List[Player]): Try[Game]

  def draw(game: Game): Try[Game]

  def moveTile(game: Game, from: GridType, to: GridType, tile: Tile, newRow: Int, newCol: Int): Try[Game]

  def setActivePlayer(game: Game, player: Player): Try[Game]
}

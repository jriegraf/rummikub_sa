package de.htwg.se.rummi.game_service

import de.htwg.se.rummi.model.GameState.GameState
import de.htwg.se.rummi.model.model.{Game, Grid, Player, Tile}

import scala.util.Try

trait GameService {

  def setGameState(id: Long, gameState: GameState): Try[Game]

  def newGame(players: List[Player]): Try[Game]

  def draw(id: Long): Try[Game]

  def moveTile(id: Long, gridFrom: Grid, gridTo: Grid, tile: Tile, newRow: Int, newCol: Int): Try[Game]
}

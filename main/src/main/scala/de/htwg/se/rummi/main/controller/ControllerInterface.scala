package de.htwg.se.rummi.main.controller

import de.htwg.se.rummi.model.model.{Game, Tile}
import de.htwg.se.rummi.model.util.GridType

import scala.swing.Publisher
import scala.util.Try

trait ControllerInterface extends Publisher {

  def createGame(playerNames: List[String]): Try[Game]

  def moveTile(game: Game, from: String, to: String): Try[Game]

  def moveTile(game: Game, gridFrom: GridType, gridTo: GridType, tile: Tile, newRow: Int, newCol: Int): Try[Game]

  def draw(game: Game): Try[Game]

  def switchPlayer(game: Game): Try[Game]

  def sortRack(game: Game): Try[Game]

  def save(game: Game): Try[String]

  def redo(game: Game): Try[Game]

  def undo(game: Game): Try[Game]

  def load(path: String): Try[Game]

  def getGameById(id: Long): Try[Game]
}

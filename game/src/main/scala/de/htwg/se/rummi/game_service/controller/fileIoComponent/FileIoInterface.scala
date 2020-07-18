package de.htwg.se.rummi.game_service.controller.fileIoComponent

import de.htwg.se.rummi.model.model.Game

import scala.util.Try

trait FileIoInterface {

  def load(path: String): Try[Game]

  def save(game: Game): Try[String]

}

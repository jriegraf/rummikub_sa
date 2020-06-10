package de.htwg.se.rummi.controller.fileIoComponent

import de.htwg.se.rummi.controller.GameController
import de.htwg.se.rummi.model.Game

trait FileIoInterface {

  def load: GameController
  def save(game: Game) : String

}

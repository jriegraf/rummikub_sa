package de.htwg.se.rummi.game_service.controller.fileIoComponent

import de.htwg.se.rummi.game_service.controller.GameController
import de.htwg.se.rummi.model.model.Game

trait FileIoInterface {

  def load: GameController
  def save(game: Game) : String

}

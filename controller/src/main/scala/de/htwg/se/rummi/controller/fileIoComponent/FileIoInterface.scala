package de.htwg.se.rummi.controller.fileIoComponent

import de.htwg.se.rummi.controller.controllerBaseImpl.GameController

trait FileIoInterface {

  def load: GameController
  def save(game: GameController) : String

}

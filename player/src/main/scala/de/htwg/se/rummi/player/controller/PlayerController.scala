package de.htwg.se.rummi.player.controller

import java.util.NoSuchElementException

import de.htwg.se.rummi.model.model.Player
import de.htwg.se.rummi.player.controller.database.slick.PlayerRepository

import scala.util._

class PlayerController() extends PlayerService {

  private val playerRepository = new PlayerRepository()
  (Player("Jules") :: Player("Pato") :: Nil).foreach(p => playerRepository.create(p))

  override def getPlayers(playerNames: List[String]): Try[List[Player]] = Success(playerNames.map(name => {
    playerRepository.read(name) match {
      case Some(player) => player
      case None => return Failure(new NoSuchElementException(f"No Player with id $name found."))
    }
  }))

  override def getPlayer(name: String): Option[Player] = {
    playerRepository.read(name)
  }
}

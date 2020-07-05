package de.htwg.se.rummi.player.controller

import de.htwg.se.rummi.model.model.Player

trait PlayerService {
  def getPlayer(id: Long): Option[Player]

  def getPlayers(playerNames: List[String]): List[Player]

}

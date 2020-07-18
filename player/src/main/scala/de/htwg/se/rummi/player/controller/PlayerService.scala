package de.htwg.se.rummi.player.controller

import de.htwg.se.rummi.model.model.Player

import scala.util.Try

trait PlayerService {

  def getPlayer(name: String): Option[Player]

  def getPlayers(playerIds: List[String]): Try[List[Player]]
}

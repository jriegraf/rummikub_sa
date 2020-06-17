package de.htwg.se.rummi.player_service.controller

import de.htwg.se.rummi.model.model.Player

class PlayerController() extends PlayerService {
  private var players: List[Player] = Player(0, "Jules") :: Player(1, "Pato") :: Nil

  override def getPlayers(playerNames: List[String]): List[Player] = playerNames.map(name => {
    players.find(p => p.name == name) match {
      case Some(player) => player
      case None => {
        val newPlayer = Player(players.size, name)
        players = players :+ newPlayer
        newPlayer
      }
    }
  });

  override def getPlayer(id: Long): Option[Player] = {
    players.find(p => p.id == id)
  }
}

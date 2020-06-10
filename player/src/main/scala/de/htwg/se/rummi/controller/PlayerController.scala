package de.htwg.se.rummi.controller

import de.htwg.se.rummi.model.Player

class PlayerController() {
  private var players: List[Player] = Player("Jules") :: Player("Pato") :: Nil

  def getPlayers(playerNames: List[String]): List[Player] = playerNames.map(name => {
    players.find(p => p.name == name) match {
      case Some(player) => player
      case None => {
        val newPlayer = Player(name)
        players = players :+ newPlayer
        newPlayer
      }
    }
  });
}

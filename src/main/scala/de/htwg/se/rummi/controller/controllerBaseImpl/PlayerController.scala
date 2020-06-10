package de.htwg.se.rummi.controller.controllerBaseImpl

import de.htwg.se.rummi.model.{Game, Player}

class PlayerController() {
  private var players: List[Player] = Player("Jules") :: Player("Pato") :: Nil

  def getNextActivePlayer(game: Game): Player = {
    val players: List[Player] = game.playerParticipations.map(p => p.player).sortBy(p => p.name)
    val nextPlayerIndex = players.indexOf(game.activePlayer) + 1
    if (nextPlayerIndex >= players.size) {
      return players.head
    }
    players(nextPlayerIndex)
  }

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

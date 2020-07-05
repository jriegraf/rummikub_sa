package de.htwg.se.rummi.player.controller

import de.htwg.se.rummi.model.model.Player
import slick.jdbc.H2Profile.api._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

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
    val players = CaseClassMapping.players
    val future = CaseClassMapping.db.run((for (p <- players if p.id === id) yield p).result.headOption)

    Await.result(future, 3 seconds)
  }
}

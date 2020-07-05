package de.htwg.se.rummi.player.controller.database.mongo

import de.htwg.se.rummi.model.model.Player
import de.htwg.se.rummi.player.controller.database.PlayerCrudRepository

import scala.util.Try

class PlayerRepository extends PlayerCrudRepository {
  override def create(player: Player): Try[Unit] = ???

  override def read(name: String): Option[Player] = ???

  override def delete(player: Player): Try[Unit] = ???

  override def update(player: Player): Try[Unit] = ???
}

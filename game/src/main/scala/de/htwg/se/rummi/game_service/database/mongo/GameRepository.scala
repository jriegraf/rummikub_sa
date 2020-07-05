package de.htwg.se.rummi.game_service.database.mongo

import de.htwg.se.rummi.game_service.database.GameCrudRepository
import de.htwg.se.rummi.model.model.Player

import scala.util.Try

class GameRepository extends GameCrudRepository {
  override def create(player: Player): Try[Unit] = ???

  override def read(name: String): Option[Player] = ???

  override def delete(player: Player): Try[Unit] = ???

  override def update(player: Player): Try[Unit] = ???
}

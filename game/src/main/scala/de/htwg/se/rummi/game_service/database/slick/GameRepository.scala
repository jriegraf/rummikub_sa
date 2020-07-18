package de.htwg.se.rummi.game_service.database.slick

import de.htwg.se.rummi.game_service.database.GameCrudRepository
import de.htwg.se.rummi.model.model.Game
import slick.jdbc.PostgresProfile.api._

import scala.util._

class GameRepository extends GameCrudRepository {
  // val db = Database.forConfig("h2mem1")
  val db = Database.forURL(
    "jdbc:postgresql://localhost:5432/",
    "postgres", "pw",
    null,
    "org.postgresql.Driver")

  override def create(game: Game): Try[Unit] = ???

  override def read(id: Long): Option[Game] = ???

  override def delete(game: Game): Try[Unit] = ???

  override def update(game: Game): Try[Unit] = ???
}

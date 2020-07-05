package de.htwg.se.rummi.game_service.database.slick

import de.htwg.se.rummi.game_service.database.GameCrudRepository
import de.htwg.se.rummi.model.model.{Game, Player}
import slick.jdbc.PostgresProfile.api._
import slick.jdbc.meta.MTable

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.Await
import scala.util._

class GameRepository extends GameCrudRepository{
  // val db = Database.forConfig("h2mem1")
  val db = Database.forURL(
    "jdbc:postgresql://localhost:5432/",
    "postgres", "pw",
    null,
    "org.postgresql.Driver")

  override def create(player: Player): Try[Unit] = ???

  override def read(name: String): Option[Player] = ???

  override def delete(player: Player): Try[Unit] = ???

  override def update(player: Player): Try[Unit] = ???
}

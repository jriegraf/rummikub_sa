package de.htwg.se.rummi.game_service.database

import de.htwg.se.rummi.model.model.Game

import scala.util.Try

trait GameCrudRepository {

  def create(game: Game): Try[Unit]

  def read(id: Long): Option[Game]

  def delete(game: Game): Try[Unit]

  def update(game: Game): Try[Unit]
}

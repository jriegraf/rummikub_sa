package de.htwg.se.rummi.player.controller.database

import de.htwg.se.rummi.model.model.Player

import scala.util.Try

trait PlayerCrudRepository {

  def create(player: Player): Try[Unit]

  def read(name: String): Option[Player]

  def delete(player: Player): Try[Unit]

  def update(player: Player): Try[Unit]
}

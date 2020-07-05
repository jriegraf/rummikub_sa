package de.htwg.se.rummi.player.controller.database.slick

import de.htwg.se.rummi.model.model.Player
import de.htwg.se.rummi.player.controller.database.PlayerCrudRepository
import slick.jdbc.PostgresProfile.api._
import slick.jdbc.meta.MTable

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.Await
import scala.util._

class PlayerRepository extends PlayerCrudRepository {
  // val db = Database.forConfig("h2mem1")
  val db = Database.forURL(
    "jdbc:postgresql://localhost:5432/",
    "postgres", "pw",
    null,
    "org.postgresql.Driver")

  val players = TableQuery[Players]

  try {
    // crate schema if it does not exist
    val tables = List(players)
    val existing = db.run(MTable.getTables)
    val f = existing.flatMap(v => {
      val names = v.map(mt => mt.name.name)
      val createIfNotExist = tables.filter(table =>
        (!names.contains(table.baseTableRow.tableName))).map(_.schema.create)
      db.run(DBIO.sequence(createIfNotExist))
    })
    Await.result(f, 10 seconds)
  }


  class Players(tag: Tag) extends Table[Player](tag, "PLAYERS") {
    def id = column[Long]("PLAYER_ID", O.PrimaryKey, O.AutoInc) // This is the primary key column

    def name = column[String]("PLAYER_NAME", O.Unique)

    // Every table needs a * projection with the same type as the table's type parameter
    def * = (name, id.?) <> ((Player.apply _).tupled, Player.unapply)
  }

  override def create(player: Player): Try[Unit] = {
    try {
      Await.result(db.run(players.insertOrUpdate(player)), 3 seconds)
      Success()
    } catch {
      case err: Throwable => Failure(err)
    }
  }

  override def read(name: String): Option[Player] = {
    val future = db.run(players.filter(_.name === name).result.headOption)
    Await.result(future, 3 seconds)
  }

  override def update(player: Player): Try[Unit] = create(player)

  override def delete(player: Player): Try[Unit] = {
    try {
      Await.result(db.run(players.filter(_.id === player.id).delete), 3 seconds)
      Success()
    } catch {
      case err: Throwable => Failure(err)
    }
  }
}

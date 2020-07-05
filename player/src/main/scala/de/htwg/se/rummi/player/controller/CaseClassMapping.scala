package de.htwg.se.rummi.player.controller

import de.htwg.se.rummi.model.model.Player
import slick.jdbc.H2Profile.api._
import scala.concurrent.duration._
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

object CaseClassMapping {

  val players = TableQuery[Players]
  val db = Database.forConfig("h2mem1")

  try {
    Await.result(db.run(DBIO.seq(
      // create the schema
      players.schema.create,

      // insert two User instances
      players += Player(0, "John Doe"),
      players += Player(1, "Fred Smith"),

      // print the users (select * from USERS)
      players.result.map(println)
    )), 10 seconds)
  }


  class Players(tag: Tag) extends Table[Player](tag, "PLAYERS") {
    def id = column[Long]("PLAYER_ID", O.PrimaryKey) // This is the primary key column

    def name = column[String]("PLAYER_NAME")

    // Every table needs a * projection with the same type as the table's type parameter
    def * = (id, name) <> ((Player.apply _).tupled, Player.unapply)
  }

}
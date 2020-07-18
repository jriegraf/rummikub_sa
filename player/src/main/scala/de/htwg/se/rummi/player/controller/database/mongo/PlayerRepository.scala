package de.htwg.se.rummi.player.controller.database.mongo

import com.mongodb.BasicDBObject
import de.htwg.se.rummi.model.model.Player
import de.htwg.se.rummi.player.controller.database.PlayerCrudRepository
import org.mongodb.scala._
import org.mongodb.scala.model.Filters
import org.mongodb.scala.model.Filters._
import play.api.libs.json.Json

import scala.concurrent.duration._
import scala.concurrent.Await
import scala.util.{Failure, Success, Try}

class PlayerRepository extends PlayerCrudRepository {

  val mongoClient: MongoClient = MongoClient()
  val database: MongoDatabase = mongoClient.getDatabase("rummikub")
  val playerCollection: MongoCollection[Document] = database.getCollection("player");

  override def create(player: Player): Try[Unit] = {
    val id = new util.Random().nextLong()
    val doc = Document(Json.prettyPrint(Json.toJson(player))) + ("_id" -> id.toString)
    println("Create Player: ")
    println(doc.toJson())
    try {
      Await.result(playerCollection.insertOne(doc).toFuture(), 2 seconds)
      Success()
    }catch {
      case e : Throwable => Failure(e)
    }
  }

  override def read(name: String): Option[Player] = {
    val docs = Await.result(playerCollection.find(equal("name", name)).toFuture(), 2 seconds)
    docs.map(doc => doc.toJson())
      .map { x => println(x); x }
      .map(doc => Json.parse(doc).as[Player]).find(p => p.name == name)
  }

  override def delete(player: Player): Try[Unit] = {
    val query = new BasicDBObject("name", player.name)
    playerCollection.deleteOne(query)
    Success()
  }

  override def update(player: Player): Try[Unit] = {
    val document = Document(Json.prettyPrint(Json.toJson(player)))
    playerCollection.replaceOne(Filters.eq("name", player.name), document)
    Success()
  }
}

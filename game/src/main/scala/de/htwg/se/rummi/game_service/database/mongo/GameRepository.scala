package de.htwg.se.rummi.game_service.database.mongo

import com.mongodb.BasicDBObject
import de.htwg.se.rummi.game_service.database.GameCrudRepository
import de.htwg.se.rummi.model.model.Game
import org.mongodb.scala._
import org.mongodb.scala.model.Filters
import org.mongodb.scala.model.Filters.equal
import play.api.libs.json.Json

import scala.concurrent.duration._
import scala.concurrent.Await
import scala.util.{Success, Try}

class GameRepository extends GameCrudRepository {


  val mongoClient: MongoClient = MongoClient()
  val database: MongoDatabase = mongoClient.getDatabase("rummikub")
  val gameCollection: MongoCollection[Document] = database.getCollection("Game");

  override def create(player: Game): Try[Unit] = {
    val id = new util.Random().nextLong()
    val doc = Document(Json.prettyPrint(Json.toJson(player))) + ("_id" -> id.toString)
    println("Create Game: ")
    println(doc.toJson())
    Await.result(gameCollection.insertOne(doc).toFuture(), 2 seconds)
    Success()
  }

  override def read(id: Long): Option[Game] = {
    val docs = Await.result(gameCollection.find(equal("id", id)).toFuture(), 2 seconds)
    docs.map(doc => doc.toJson())
      .map { x => println(x); x }
      .map(doc => Json.parse(doc).as[Game]).find(p => p.id == id)
  }

  override def delete(game: Game): Try[Unit] = {
    val query = new BasicDBObject("id", game.id)
    gameCollection.deleteOne(query)
    Success()
  }

  override def update(game: Game): Try[Unit] = {
    val document = Document(Json.prettyPrint(Json.toJson(game)))
    gameCollection.replaceOne(Filters.eq("id", game.id), document)
    Success()
  }
}

package de.htwg.se.rummi.game_service.controller.controllerBaseImpl

import de.htwg.se.rummi.game_service.controller.fileIoComponent.jsonImpl.JsonFileIo
import de.htwg.se.rummi.main.controller.controllerBaseImpl.Controller
import de.htwg.se.rummi.model.model.{BLUE, GREEN, RED, RummiSet, Tile, WHITE, YELLOW}
import org.scalatest.{Matchers, WordSpec}

import scala.util.{Failure, Success}

class ControllerSpec extends WordSpec with Matchers {


  var playerNames: List[String] = List("patrick", "julian")

  var controller = new Controller()
  controller.createGame(playerNames)

  val g9 = new Tile(9, GREEN)
  val g10 = new Tile(10, GREEN)
  val g8 = new Tile(8, GREEN)
  val g11 = new Tile(11, GREEN)
  val g12 = new Tile(12, GREEN)
  val g13 = new Tile(13, GREEN)

  "When the game starts a new Game " should {
    "be initiated " in {

    }

    "and should tell you who the first active Player is " in {

    }
  }

  "The first play is either to draw a card or to play 30+ valid points " should {
    "return false and publish a status message if there are 29 or less pts played" in {

    }

    "return true if there are 30 or more valid points played " in {
      val list = g11 :: g12 :: g13 :: Nil

    }

    "return the rack of a specific player " in {

    }

    "set the rack of the active player" in {

    }

    "translate coordinates from A1 to tuple(1,1)" in {

    }
  }

  "After a Move is made it should be the next players turn " should {
    "change player " in {

    }
  }

  "Before the switch the controller checks if the playingfield is valid: " should {
    val list = g11 :: g12 :: g13 :: Nil
    val list2 = g8 :: g9 :: g11 :: Nil
    val list3 = g8 :: g9 :: g10 :: Nil
    val playingfieldSet1 = new RummiSet(list)
    val playingfieldSet2 = new RummiSet(list2)
    val playingfieldSet3 = new RummiSet(list3)

    "return false if there are wrong sets " in {



    }

    "return false if there are wrong set" in {

    }

    "return true if multiple sets are correct " in {

    }
  }

  "Players draw tiles" should {
    "take a tile from the stack and adds it to the players rack" in {

    }

    "throw exception if rack is full" in {

    }

    "can sort tiles by color and number" in {
      val g1 = Tile(1, GREEN)
      val g5 = Tile(5, GREEN)
      val b1 = Tile(1, BLUE)
      val y1 = Tile(1, YELLOW)
      val r1 = Tile(1, RED)



    }

    "can sort tiles by color with 5 colors" in {
      val g1 = Tile(1, GREEN)
      val g5 = Tile(5, WHITE)
      val b1 = Tile(1, BLUE)
      val y1 = Tile(1, YELLOW)
      val r1 = Tile(1, RED)



    }
  }

  "Players can move Tiles " should {
    "either from their rack to the field " in {


    }

    "or within the rack " in {

    }

    "from field to rack" in {

    }

    "within the field" in {

    }

    "by their variables" in {

    }
  }

  "On save, controller " should {
    "return the serialized game in json format" in {
      controller.createGame("p1" :: "p2" :: Nil) match {
        case Failure(exception) =>
        case Success(game) => {
          val fileIo = new JsonFileIo();
          val json = fileIo.save(game)
          json shouldNot(be(""))
        }
      }

    }
  }

  "controller undo" should {
    "undo the last tile movement" in {

    }

    "redo the last tile movement" in {

    }
  }
}

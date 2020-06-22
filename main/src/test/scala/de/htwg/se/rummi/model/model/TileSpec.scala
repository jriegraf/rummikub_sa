package de.htwg.se.rummi.model.model

import org.scalatest.{Matchers, WordSpec}
import play.api.libs.json.Json

class TileSpec extends WordSpec with Matchers {

  "A tile is a game piece that has a number and a color or is a jocker." should {

    val tileBlueFour = Tile(4, BLUE, false)

    "convert to json" in {
      val jsonString = Json.prettyPrint(Json.toJson(tileBlueFour))
      jsonString should be ("""{
                              |  "number" : 4,
                              |  "color" : {
                              |    "name" : "BLUE"
                              |  },
                              |  "joker" : false
                              |}""".stripMargin)
    }

    "convert from json" in {
      val json = """{
                   |  "number" : 4,
                   |  "color" : {
                   |    "name" : "BLUE"
                   |  },
                   |  "joker" : false
                   |}""".stripMargin

      val tile = Json.parse(json).as[Tile]
      tile.number  should be (4)
      tile.color should be (BLUE)
      tile.joker should be (false)
    }

    "equals compares by reference, not by value" in {
      tileBlueFour equals tileBlueFour should be(true)
      tileBlueFour equals Tile(4, BLUE, false) should be(false)
    }

    "equals with another type should fail" in {
      tileBlueFour equals "some random string" should be(false)
    }

    "toString converts to a nice output of the number and color" in {
      tileBlueFour.toString should be("(4, BLUE)")
    }

    "if tile is a joker, it should print a white 'J'" in {
      Tile(2342, RED, true).toString should be("(JOKER)")
    }

  }
}

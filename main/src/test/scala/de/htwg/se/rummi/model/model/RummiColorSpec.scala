package de.htwg.se.rummi.model.model

import org.scalatest.{Matchers, WordSpec}


class RummiColorSpec extends WordSpec with Matchers {

  "A RummiColor is a color of a tile, it" should {


    "only print name by calling toString" in {
      WHITE.toString should be("WHITE")
    }

    "compare by name" in {
      val True = 0
      WHITE == RummiColor("WHITE") should be(true)
      WHITE == RummiColor("BLUE") should not be (true)
    }

  }
}

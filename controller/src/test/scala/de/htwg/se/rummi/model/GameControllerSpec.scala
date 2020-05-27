package de.htwg.se.rummi.model

import de.htwg.se.rummi.controller.controllerBaseImpl.GameController
import org.scalatest.{Matchers, WordSpec}

class GameControllerSpec extends WordSpec with Matchers {

  "A game" should {

    val game = GameController("player1" :: "player2" :: Nil)

    "some testing" in {

    }


  }
}

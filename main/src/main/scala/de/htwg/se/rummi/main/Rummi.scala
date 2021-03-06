package de.htwg.se.rummi.main

import com.google.inject.Guice
import de.htwg.se.rummi.main.aview.{RestService, Tui}
import de.htwg.se.rummi.main.controller.ControllerInterface
import de.htwg.se.rummi.model.model.Game

import scala.io.StdIn
import scala.util.{Failure, Success}

object Rummi {

  def main(args: Array[String]): Unit = {

    println(" _____                               _  _            _")
    println("|  __ \\                             (_)| |          | |")
    println("| |__) |_   _  _ __ ___   _ __ ___   _ | | __ _   _ | |__")
    println("|  _  /| | | || '_ ` _ \\ | '_ ` _ \\ | || |/ /| | | || '_ \\")
    println("| | \\ \\| |_| || | | | | || | | | | || ||   < | |_| || |_) |")
    println("|_|  \\_\\\\__,_||_| |_| |_||_| |_| |_||_||_|\\_\\ \\__,_||_.__/")

    println()

    var playerNames: List[String] = Nil

    if (args.size > 0) {
      // Read player names from program arguments
      val numberOfPlayers: Int = args(0).toInt
      if (args.size - 1 != numberOfPlayers) throw new IllegalArgumentException
      playerNames = args.slice(1, args.size).toList
    } else {
      // Ask user to input player names
      print("Number of players? ")
      val numberOfPlayer = StdIn.readLine().toInt

      (1 to numberOfPlayer)
        .foreach(i => {
          print("Player " + i + ": ")
          playerNames = StdIn.readLine() :: playerNames
        })
    }

    val injector = Guice.createInjector(new RummiModule)
    val controller = injector.getInstance(classOf[ControllerInterface])

    val game: Game = controller.createGame(playerNames) match {
      case Failure(exception) =>
        println(s"Game could not be created. Error: ${exception}")
        System.exit(1)
        return
      case Success(value) => value
    }
    val restService = new RestService(controller);
    println("Game id: " + game.id)
    val tui = new Tui(controller, game)


    //val gui = new SwingGui(controller, game)
    //gui.init
    //gui.visible = true


  }
}

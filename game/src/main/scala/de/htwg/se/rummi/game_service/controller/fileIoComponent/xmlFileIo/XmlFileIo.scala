package de.htwg.se.rummi.game_service.controller.fileIoComponent.xmlFileIo

import de.htwg.se.rummi.game_service.controller.GameController
import de.htwg.se.rummi.game_service.controller.fileIoComponent.FileIoInterface
import de.htwg.se.rummi.model.model.Game

import scala.xml.PrettyPrinter

class XmlFileIo extends FileIoInterface {
  override def load: GameController = ???

  override def save(game: Game): String = {
    import java.io._
    val pw = new PrintWriter(new File("grid.xml"))
    val prettyPrinter = new PrettyPrinter(120, 4)
    val xml = prettyPrinter.format(gameToXml(game))
    pw.write(xml)
    pw.close
    xml
  }

  def gameToXml(game: Game) = {
    ???
  }
}

package de.htwg.se.rummi.model.fileIoComponent.xmlFileIo

import de.htwg.se.rummi.controller.controllerBaseImpl.GameController
import de.htwg.se.rummi.model.fileIoComponent.FileIoInterface

import scala.xml.PrettyPrinter

class XmlFileIo extends FileIoInterface{
  override def load: GameController = ???

  override def save(game: GameController): String = {
    import java.io._
    val pw = new PrintWriter(new File("grid.xml"))
    val prettyPrinter = new PrettyPrinter(120, 4)
    val xml = prettyPrinter.format(gameToXml(game))
    pw.write(xml)
    pw.close
    xml
  }

  def gameToXml(game: GameController) = {
    game toXml
  }
}

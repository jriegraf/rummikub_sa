package de.htwg.se.rummi.main

import com.google.inject.AbstractModule
import de.htwg.se.rummi.main.controller.ControllerInterface
import de.htwg.se.rummi.main.controller.controllerBaseImpl.Controller
import de.htwg.se.rummi.game_service.controller.fileIoComponent.FileIoInterface
import de.htwg.se.rummi.game_service.controller.fileIoComponent.xmlFileIo.XmlFileIo
import net.codingwell.scalaguice.ScalaModule

class RummiModule extends AbstractModule with ScalaModule {
  override def configure() = {
    bind[FileIoInterface].to[XmlFileIo]
    bind[ControllerInterface].to[Controller]
  }
}

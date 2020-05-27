package de.htwg.se.rummi.controller

import com.google.inject.AbstractModule
import de.htwg.se.rummi.controller.fileIoComponent.FileIoInterface
import de.htwg.se.rummi.controller.fileIoComponent.xmlFileIo.XmlFileIo
import net.codingwell.scalaguice.ScalaModule

class RummiModule extends AbstractModule with ScalaModule {
  override def configure() = {
    bind[FileIoInterface].to[XmlFileIo]
    bind[ControllerInterface].to[controllerBaseImpl.Controller]
  }
}

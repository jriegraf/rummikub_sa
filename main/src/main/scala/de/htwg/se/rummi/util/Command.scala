package de.htwg.se.rummi.util

import de.htwg.se.rummi.model.Game

import scala.util.Try

trait Command {

  def doStep: Try[Game]

  def undoStep: Try[Game]

  def redoStep: Try[Game]

}
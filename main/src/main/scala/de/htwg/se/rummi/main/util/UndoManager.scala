package de.htwg.se.rummi.main.util

import de.htwg.se.rummi.model.model.Game

import scala.util.{Failure, Try}

class UndoManager {
  private var undoStack: List[Command] = Nil
  private var redoStack: List[Command] = Nil

  def doStep(command: Command): Try[Game] = {
    undoStack = command :: undoStack
    command.doStep
  }

  def undoStep: Try[Game] = {
    undoStack match {
      case Nil => Failure(new Exception("Undo stack is empty."))
      case head :: stack => {
        val ret = head.undoStep
        undoStack = stack
        redoStack = head :: redoStack
        ret
      }
    }
  }

  def redoStep: Try[Game] = {
    redoStack match {
      case Nil => Failure(new Exception("Redo stack is empty."))
      case head :: stack => {
        val ret = head.redoStep
        redoStack = stack
        undoStack = head :: undoStack
        ret
      }
    }
  }

  def reset(): Unit = {
    undoStack = Nil
    redoStack = Nil
  }
}
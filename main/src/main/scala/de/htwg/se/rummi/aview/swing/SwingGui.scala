package de.htwg.se.rummi.aview.swing

import java.awt.Color

import de.htwg.se.rummi.GameState
import de.htwg.se.rummi.controller.controllerBaseImpl._
import de.htwg.se.rummi.controller.ControllerInterface
import de.htwg.se.rummi.model.Game

import scala.swing._
import scala.swing.event.ButtonClicked
import scala.util.{Failure, Success, Try}

class SwingGui(co: ControllerInterface, var game: Game) extends MainFrame {

  preferredSize = new Dimension(1100, 800)
  title = "Rummikub in Scala"

  val finishButton = new Button("Finish") {
    enabled = false
  }

  listenTo(finishButton)
  val getTileButton = new Button("Get Tile")
  listenTo(getTileButton)
  val undoButton = new Button("Undo")
  listenTo(undoButton)
  val redoButton = new Button("Redo")
  listenTo(redoButton)
  val sortButton = new Button("Sort")
  listenTo(sortButton)

  val statusLabel = new Label(GameState.message(game.gameState))
  val playerLabel = new Label("Current Player: " + game.activePlayer.name)

  val field = new SwingGrid(8, 13) {
    border = Swing.EmptyBorder(0, 10, 10, 10)
  }
  val rack = new SwingGrid(4, 13) {
    border = Swing.EmptyBorder(0, 10, 10, 10)
  }

  val newGameMenuItem = new MenuItem("New Game")
  listenTo(newGameMenuItem)
  val saveMenuItem = new MenuItem("Save")
  listenTo(saveMenuItem)
  val quitMenuItem = new MenuItem("Quit")
  listenTo(quitMenuItem)

  menuBar = new MenuBar() {
    contents += new Menu("Menu") {
      contents += newGameMenuItem
      contents += quitMenuItem
      contents += saveMenuItem
    }
  }

  val center = new BoxPanel(Orientation.Vertical) {
    contents += field
    contents += rack
    contents += new BoxPanel(Orientation.Horizontal) {
      contents += finishButton
      contents += getTileButton
      contents += undoButton
      contents += redoButton
      contents += sortButton
    }
  }

  val south: GridPanel = new GridPanel(1, 3) {
    border = Swing.EmptyBorder(10)
    contents += playerLabel
    contents += statusLabel
  }

  contents = new BorderPanel() {
    add(center, BorderPanel.Position.Center)
    add(south, BorderPanel.Position.South)
  }

  rack.fields.foreach(t => listenTo(t))
  field.fields.foreach(t => listenTo(t))


  var selectedField: Option[Field] = Option.empty

  reactions += {
    case ButtonClicked(b) => {

      b match {
        case clickedField: Field =>
          fieldClicked(clickedField)
        case _ => if (b == getTileButton) {
          co.draw(game) match {
            case Success(game) => updateGame(game)
            case Failure(err) => statusLabel.text = err.getMessage
          }
        } else if (b == finishButton) {
          co.switchPlayer(game) match {
            case Success(game) => updateGame(game)
            case Failure(err) => statusLabel.text = err.getMessage
          }
        } else if (b == quitMenuItem) {
          sys.exit(0)
        } else if (b == newGameMenuItem) {
          co.createGame(game.playerParticipations.map(p => p.player.name)) match {
            case Success(game) => updateGame(game)
            case Failure(err) => statusLabel.text = err.getMessage
          }
        } else if (b == saveMenuItem) {
          co.save(game)
        } else if (b == sortButton) {
          co.sortRack(game) match {
            case Success(game) => updateGame(game)
            case Failure(err) => statusLabel.text = err.getMessage
          }
        } else if (b == undoButton) {
          co.undo(game) match {
            case Success(game) => updateGame(game)
            case Failure(err) => statusLabel.text = err.getMessage
          }
        } else if (b == redoButton) {
          co.redo(game) match {
            case Success(game) => updateGame(game)
            case Failure(err) => statusLabel.text = err.getMessage
          }
        }
      }
    }
  }

  def updateGame(game: Game): Unit ={
    this.game = game
    field.displayGrid(game.field)
    rack.displayGrid(game.getRackOfActivePlayer)
    if (game.isValid) {
      finishButton.enabled = true
    } else {
      finishButton.enabled = false
    }
    game.gameState match {
      case GameState.DRAWN => {
        getTileButton.enabled = false
        finishButton.enabled = true
        field.fields.foreach(f => f.enabled = false)

      }
      case GameState.WAITING => {
        getTileButton.enabled = true
        field.fields.foreach(f => f.enabled = true)
      }
      case GameState.INVALID | GameState.TO_LESS => {
        finishButton.enabled = false
        getTileButton.enabled = false
      }
      case GameState.VALID => {
        finishButton.enabled = true
        getTileButton.enabled = false
      }
      case _ =>

    }
    playerLabel.text = "Current Player: " + game.activePlayer.name
  }

  private def fieldClicked(clickedField: Field): Unit = {
    // Click on a empty field and there is no field selected -> Do nothing
    if (clickedField.tileOpt.isEmpty && selectedField.isEmpty) {

    }
    // Click on a empty field an there is a field selected -> move selected to empty field, unselect
    else if (clickedField.tileOpt.isEmpty && selectedField.isDefined) {

      moveTile(clickedField)

      selectedField.get.border = Swing.LineBorder(Color.BLACK, 1)
      selectedField = None
    }
    // Click on a filled field an no field selected -> Select field
    else if (clickedField.tileOpt.isDefined && selectedField.isEmpty) {
      selectedField = Some(clickedField)
      clickedField.border = Swing.LineBorder(Color.BLACK, 4)
    }
    // Click on a filled field an there is a field selected --> Unselect if same selected and clicket is same, else
    //      deselect the currently selected field and select the clicked field
    else if (clickedField.tileOpt.isDefined && selectedField.isDefined) {
      if (clickedField == selectedField.get) {
        selectedField.get.border = Swing.LineBorder(Color.BLACK, 1)
        selectedField = None
      } else {
        selectedField.get.border = Swing.LineBorder(Color.BLACK, 1)
        selectedField = Some(clickedField)
        clickedField.border = Swing.LineBorder(Color.BLACK, 4)
      }
    }
  }


  private def moveTile(clickedField: Field): Unit = {

    var result: Try[Game] = null
    if (rack.containsField(selectedField.get) && field.containsField(clickedField)) {
      result = co.moveTile(game, game.getRackOfActivePlayer, game.field, selectedField.get.tileOpt.get, clickedField.row, clickedField.col)
    }

    if (field.containsField(selectedField.get) && rack.containsField(clickedField)) {
      result = co.moveTile(game, game.field, game.getRackOfActivePlayer, selectedField.get.tileOpt.get, clickedField.row, clickedField.col)
    }

    if (rack.containsField(selectedField.get) && rack.containsField(clickedField)) {
      result = co.moveTile(game, game.getRackOfActivePlayer, game.getRackOfActivePlayer, selectedField.get.tileOpt.get, clickedField.row, clickedField.col)
    }

    if (field.containsField(clickedField) && field.containsField(selectedField.get)) {
      result = co.moveTile(game, game.field, game.field, selectedField.get.tileOpt.get, clickedField.row, clickedField.col)
    }

    result match {
      case Success(game) => updateGame(game)
      case Failure(err) => statusLabel.text = err.getMessage
    }
  }

  def init: Unit = {
    rack.displayGrid(game.getRackOfActivePlayer)
    field.displayGrid(game.field)
  }

}
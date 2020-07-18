package de.htwg.se.rummi.model

import play.api.libs.json.{Reads, Writes}

// Custom validation helpers

object GameState extends Enumeration {
  type GameState = Value
  val WAITING, VALID, INVALID, TO_LESS, DRAWN, WON = Value

  private val map = Map[GameState, String](
    WAITING -> "Please place at least one stone or draw.",
    DRAWN -> "Player has drawn a tile",
    VALID -> "Field is valid",
    INVALID -> "Field is invalid",
    TO_LESS -> "You have to score 30 or more points on your first turn",
    WON -> "You won! °_°",
  )

  def message(gameStatus: GameState) = {
    map(gameStatus)
  }

  implicit val myEnumReads: Reads[GameState.Value] = Reads.enumNameReads(GameState)
  implicit val myEnumWrites: Writes[GameState.Value] = Writes.enumNameWrites

}
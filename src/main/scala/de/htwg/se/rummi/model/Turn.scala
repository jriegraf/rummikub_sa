package de.htwg.se.rummi.model

case class Turn(player : Player, movedTiles : List[Tile])


object Turn {
  def empty(player: Player) : Turn = {
    Turn(player, List.empty)
  }
}
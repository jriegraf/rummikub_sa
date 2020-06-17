package de.htwg.se.rummi.model.model

case class Turn(player: Player, movedTiles: List[Tile])


object Turn {
  def empty(player: Player): Turn = {
    Turn(player, List.empty)
  }


  import play.api.libs.json._

  implicit val writes = Json.writes[Turn]
  implicit val reads = Json.reads[Turn]
}

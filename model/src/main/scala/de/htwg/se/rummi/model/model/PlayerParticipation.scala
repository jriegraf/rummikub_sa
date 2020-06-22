package de.htwg.se.rummi.model.model

case class PlayerParticipation(player: Player, rack: Rack, inFirstRound: Boolean = true, points: Int = 0) {}

object PlayerParticipation {

  import play.api.libs.json._

  implicit val writes: OWrites[PlayerParticipation] = Json.writes[PlayerParticipation]
  implicit val tileReads: Reads[PlayerParticipation] = Json.reads[PlayerParticipation]

}
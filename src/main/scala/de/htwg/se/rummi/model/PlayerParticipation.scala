package de.htwg.se.rummi.model

case class PlayerParticipation(player: Player, rack: Rack, inFirstRound: Boolean = true, points: Int = 0){
  def toXml = {
    <PlayerParticipation>
      <player>{player}</player>
      <rack>{rack}</rack>
      <inFirstRound>{inFirstRound}</inFirstRound>
      <points>{points}</points>
    </PlayerParticipation>
  }
}

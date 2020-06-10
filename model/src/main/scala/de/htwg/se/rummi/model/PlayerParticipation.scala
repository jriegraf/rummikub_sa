package de.htwg.se.rummi.model

case class PlayerParticipation(player: Player, rack: Rack, inFirstRound: Boolean = true, points: Int = 0) {}
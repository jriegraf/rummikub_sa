package de.htwg.se.rummi.model

case class Player(name: String) {

  def toXml = {
    <player>
      <name>
        {name}
      </name>
    </player>
  }
}
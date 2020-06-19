package de.htwg.se.rummi.model.model

import de.htwg.se.rummi.model.Const._
import de.htwg.se.rummi.model.GameState.{GameState, WAITING}

case class Game(activePlayer: Player,
                field: Field,
                coveredTiles: List[Tile],
                playerParticipations: List[PlayerParticipation],
                id: Long,
                movedTiles: List[Tile] = Nil,
                gameState: GameState = WAITING) {


  def isPlayerInFirstRound(player: Player) = {
    playerParticipations.find(p => p.player == player).map(p => p.inFirstRound) match {
      case Some(e) => e
      case None => throw new NoSuchElementException()
    }
  }


  def setGameState(gameState: GameState): Game = {
    this.copy(gameState = gameState)
  }

  def getPlayers(): List[Player] = {
    playerParticipations.map(p => p.player)
  }

  def switchPlayer(): Game = {
    val nextIdx = getPlayers().sortBy(p => p.name).indexOf(activePlayer) + 1
    if (nextIdx >= getPlayers().size) {
      getPlayers().minBy(p => p.name)
    }
    this.copy(activePlayer = getPlayers()(nextIdx))
  }

  def countMovedTiles: Int = {
    movedTiles.size
  }

  def extractSets(): List[RummiSet] = {
    var sets: List[RummiSet] = Nil

    field.tiles.groupBy(x => x._1._1)
      .map(x => x._2)
      .foreach(map => {

        var list = map.map(x => (x._1._2, x._2))
          .toList
          .sortBy(x => x._1)

        while (list.nonEmpty) {
          var tiles: List[Tile] = List.empty
          tiles = list.head._2 :: tiles

          while (list.exists(x => x._1 == list.head._1 + 1)) {
            list = list.drop(1)
            tiles = list.head._2 :: tiles
          }

          sets = RummiSet(tiles.reverse) :: sets
          list = list.drop(1)
        }

      })
    sets
  }

  def getRack(player: Player): Option[Rack] = {
    playerParticipations.find(p => p.player == player).map(p => p.rack)
  }

  def getRackOfActivePlayer: Rack = {
    getRack(activePlayer) match {
      case Some(r) => r
      case None => throw new NoSuchElementException()
    }
  }

  def getParticipationOfActivePlayer: PlayerParticipation = {
    playerParticipations.find(x => x.player == activePlayer) match {
      case Some(r) => r
      case None => throw new NoSuchElementException()
    }
  }

  def updateParticipationOfActivePlayer(playerParticipation: PlayerParticipation): Game = {
    this.copy(playerParticipations = playerParticipations.updated(
      playerParticipations.indexOf(getParticipationOfActivePlayer), playerParticipation))
  }

  def isValid: Boolean = {
    extractSets()
      .filter(s => !s.isValidGroup())
      .forall(s => s.isValidRun())
  }

  /**
   * Did player reached minimum score to get out?
   * All sets which the user builds or appends to do count.
   *
   * @return true if player reached minimum score
   */
  def doesPlayerReachedMinLayOutPoints: Boolean = {
    val sumOfFirstMove = extractSets()
      // only use Sets, which contains tiles moved by this player in this round
      .filter(x => x.tiles.toSet.intersect(movedTiles.toSet).nonEmpty)
      .map(x => x.getPoints)
      .sum

    sumOfFirstMove >= MINIMUM_POINTS_FIRST_ROUND
  }

  override def equals(that: Any): Boolean = {
    that match {
      case t: Game => t.id == this.id
      case _ => false
    }
  }
}
object Game {

  import play.api.libs.json._

  implicit val writes: OWrites[Game] = Json.writes[Game]
}

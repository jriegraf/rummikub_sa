package de.htwg.se.rummi.model.model

import de.htwg.se.rummi.model.Const

case class RummiSet(tiles: List[Tile]) {

  def getPoints: Int = {
    val pivotTile = tiles.find(t => !t.joker) match {
      case Some(t) => t
      case None => new NoSuchElementException
    }

    val pivotIndex = tiles.indexOf(pivotTile)

    val buffer = tiles.map(t => {
      if (t.joker) -1 else t.number
    }).toBuffer

    for (i <- tiles.indices) {
      if (buffer(i) == -1) {
        buffer.update(i, buffer(pivotIndex) - (pivotIndex - i))
      }
    }
    buffer.sum
  }

  def isValidRun: Boolean = {
    if (tiles.size < 3) return false
    if (tiles.groupBy(_.color).size > 1 && tiles.count(x => x.joker) == 0) return false
    val n: List[Tile] = tiles.sortBy(_.number)
    if (tiles.count(x => x.joker) > 0) {
      // TODO: Check if valid with Joker
      val pivotTile = tiles.find(t => !t.joker) match {
        case Some(t) => t
        case None => new NoSuchElementException
      }

      val pivotIndex = tiles.indexOf(pivotTile)

      val buffer = tiles.map(t => {
        if (t.joker) -1
        else t.number
      }).toBuffer

      for (i <- tiles.indices) {
        if (buffer(i) == -1) {
          buffer.update(i, buffer(pivotIndex) - (pivotIndex - i))
        }
      }
      if (buffer.max > Const.highest_number || buffer.min < Const.lowest_number) {
        return false
      }
      for (i <- tiles.indices) {
        if (buffer(i) != tiles(i).number && !tiles(i).joker) {
          return false
        }
      }
      for (i <- 0 to tiles.size - 2) {
        if (buffer(i) + 1 != buffer(i + 1)) {
          return false
        }
      }
    } else {
      for (i <- 0 to tiles.size - 2) {
        if (n(i).number + 1 != n(i + 1).number)
          return false
      }
    }

    true
  }

  def isValidGroup: Boolean = {
    if (tiles.size < 3) return false
    if (tiles.size > 4) return false
    if (tiles.filter(x => !x.joker)
      .groupBy(_.number).size > 1) return false
    if (tiles.filter(x => !x.joker)
      .groupBy(_.color).size != tiles.size - tiles.count(x => x.joker)) return false
    true
  }

  override def toString: String = {
    tiles.toStream.map(t => t.toString).mkString
  }
}

object RummiSet {

  import play.api.libs.json._

  implicit val writes: OWrites[RummiSet] = Json.writes[RummiSet]
  implicit val reads: Reads[RummiSet] = Json.reads[RummiSet]
}
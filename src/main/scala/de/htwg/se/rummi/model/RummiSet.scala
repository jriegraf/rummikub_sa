package de.htwg.se.rummi.model

case class RummiSet(tiles: List[Tile]) {

  val highest_number = 13
  val lowest_number = 1

  def getPoints: Int = {
    val pivotTile = tiles.find(t => !t.joker) match {
      case Some(t) => t
      case None => new NoSuchElementException
    }

    val pivotIndex = tiles.indexOf(pivotTile)

    val buffer = tiles
      .map(t => {
        if (t.joker) -1 else t.number
      })
      .toBuffer

    for (i <- 0 to tiles.size - 1) {
      if (buffer(i) == -1) {
        buffer.update(i, buffer(pivotIndex) - (pivotIndex - i))
      }
    }
    buffer.sum
  }

  def isValidRun(): Boolean = {
    if (tiles.size < 3) return false
    if (tiles.groupBy(_.colour).size > 1 && tiles.count(x => x.joker) == 0) return false
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

      for (i <- 0 to tiles.size - 1) {
        if (buffer(i) == -1) {
          buffer.update(i, buffer(pivotIndex) - (pivotIndex - i))
        }
      }
      if (buffer.max > highest_number || buffer.min < lowest_number) {
        return false
      }
      for (i <- 0 to tiles.size - 1) {
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

  def isValidGroup(): Boolean = {
    if (tiles.size < 3) return false
    if (tiles.size > 4) return false
    val isJoker = tiles.count(x => x.joker) > 0
    if (!isJoker) {
      if (tiles.groupBy(_.number).size > 1) return false
      if (tiles.groupBy(_.colour).size != tiles.size) return false
      return true
    } else {
      //joker involved
      return true
    }
    true
  }

  override def toString: String = {
    tiles.toStream.map(t => t.toString).mkString
  }
}
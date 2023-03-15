package realjenius.ketris

enum class Rotation { None, Clockwise, Counterclockwise }

/** The set of shape templates */
enum class TetrominoShape(val originX: Int, val originY: Int, val shape: Shape) {
  I(5, 2, Shape("""
    .#.
    .#.
    .#.
    .#.
    """)),
  S(4, 3, Shape("""
    .#.
    .##
    ..#
    """)),
  Z(4, 3, Shape("""
    .#.
    ##.
    #..
  """)),
  O(5, 3, Shape("""
    ...
    .##
    .##
  """)),
  T(4, 3, Shape("""
    ...
    ###
    .#.
  """)),
  L(4, 3, Shape("""
    ##.
    .#.
    .#.
  """)),
  J(4, 3, Shape("""
    .##
    .#.
    .#.
  """));

  companion object {
    val all = values().toList()
  }
}

/** An immutable rotatable coordinate set */
data class Shape(val coordinates: List<Pair<Int,Int>>) {
  constructor(shapeSpec: String) : this(parse(shapeSpec))

  val minX = coordinates.minOf { it.first }
  val minY = coordinates.minOf { it.second }
  val maxX = coordinates.maxOf { it.first }
  val maxY = coordinates.maxOf { it.second }

  fun rotate(rotation: Rotation) = Shape(
    coordinates.map {
      if (rotation == Rotation.Counterclockwise) it.copy(first = it.second, -it.first)
      else it.copy(first = -it.second, second = it.first)
    }
  )

  companion object {
    private fun parse(spec: String) : List<Pair<Int,Int>> {
      return spec
        .lines()
        .filter { it.isNotBlank() }
        .mapIndexed { yIdx, line ->
          line.trim().mapIndexed { xIdx, char ->
            if (char == '#') xIdx-1 to yIdx-1 else null
          }
        }.flatten()
        .filterNotNull()
    }
  }
}

/** An active shape on the board. */
data class Tetromino(val template: TetrominoShape, val shape: Shape, val x: Int, val y: Int) {
  constructor(starter: TetrominoShape) : this(starter, starter.shape, starter.originX, starter.originY)

  val maxX = shape.maxX + x
  val minX = shape.minX + x
  val maxY = shape.maxY + y
  val minY = shape.minY + y

  fun rotate(rotation: Rotation) =
    if (rotation == Rotation.None || template == TetrominoShape.O) this
    else this.copy(shape = shape.rotate(rotation))

  fun move(x: Int, y: Int) = if (x == 0 && y == 0) this else this.copy(x = this.x + x, y = this.y + y)

  fun contains(x: Int, y: Int) = shape.coordinates.any { (it.first + this.x) == x && (it.second + this.y) == y }

  inline fun onMatchingCells(board: Board, action: (Board.Cell) -> Unit) {
    shape.coordinates.forEach {
      action(board.cellAt(it.first + x, it.second + y))
    }
  }
}
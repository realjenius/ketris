package realjenius.ketris

/**
 * The board state for Ketris
 */
class Board(private val game: Game) {
  private var holdPiece: TetrominoShape = game.piece()
  private lateinit var activePiece: Tetromino
  private lateinit var ghostPiece: Tetromino

  val next: TetrominoShape
    get() = holdPiece

  val grid: List<List<Cell>> = (0 until HEIGHT).map { y ->
    (0 until WIDTH).map { x ->
      Cell(x = x, y = y, placed = null, pieceOverlap = null, ghostOverlap = null)
    }
  }

  init {
    spawnPiece()
    calculatePieceOverlays()
  }

  private fun spawnPiece() {
    activePiece = Tetromino(holdPiece)
    holdPiece = game.piece()
    computeGhost()
    game.clock.newPiece()
  }

  inline fun allCells(callback: (cell: Cell) -> Unit) {
    grid.forEach { row ->
      row.forEach {
        callback(it)
      }
    }
  }

  fun cellAt(x: Int, y: Int) = grid[y][x]

  fun move(input: InputState): Boolean {
    var gravityDropCount = game.clock.checkForGravity()

    val priorPiece = activePiece
    activePiece = activePiece.rotate(input.rotate).takeUnless { collides(it) } ?: activePiece
    activePiece = activePiece.move(input.xDelta, 0).takeUnless { collides(it) } ?: activePiece

    var bottomedOut = false
    while ((gravityDropCount > 0 || input.drop) && !bottomedOut) {
      val newPiece = activePiece.move(0, 1)
      if (collides(newPiece)) {
        bottomedOut = true
      } else {
        bottomedOut = false
        gravityDropCount--
        activePiece = newPiece
      }
    }

    if (bottomedOut) {
      placePiece()
      clearLines()
      if (activePiece.minY < INVISIBLE_ROWS) return false
      spawnPiece()
    } else if (priorPiece != activePiece) {
      computeGhost()
    }
    calculatePieceOverlays()
    return true
  }

  private fun placePiece() {
    activePiece.onMatchingCells(this) {
      it.placed = activePiece.template
    }
  }

  private fun clearLines() {
    var lineAdd = 0
    grid.forEachIndexed { idx, row ->
      if (row.all { it.placed != null }) {
        lineAdd++
        row.forEach { it.placed = null }
        clearLine(idx)
      }
    }
    game.addLines(lineAdd)
  }

  private fun clearLine(line: Int) {
    for (c in 0 until WIDTH) {
      for (r in line downTo 1) {
        grid[r][c].placed = grid[r - 1][c].placed
      }
    }
  }

  private fun collides(piece: Tetromino) = when {
    piece.minX < 0 || piece.maxX >= WIDTH -> true
    piece.maxY >= HEIGHT -> true
    else -> grid.any { it.any { cell -> cell.placed != null && piece.contains(cell.x, cell.y) } }
  }

  // This is a super wasteful algorithm, but it works and is easy to reason about, so meh for now.
  private fun computeGhost() {
    ghostPiece = activePiece.copy(shape = activePiece.shape.copy())
    while (!collides(ghostPiece) && ghostPiece.minX < HEIGHT - 1) {
      ghostPiece = ghostPiece.move(0, 1)
    }
    if (ghostPiece.minX < HEIGHT - 1) ghostPiece = ghostPiece.move(0, -1)
  }

  private fun calculatePieceOverlays() {
    allCells { cell ->
      cell.pieceOverlap = activePiece.takeIf { it.contains(cell.x, cell.y) }
      cell.ghostOverlap = ghostPiece.takeIf { it.contains(cell.x, cell.y) }
    }
  }

  data class Cell(val x: Int, val y: Int, var placed: TetrominoShape?, var pieceOverlap: Tetromino?, var ghostOverlap: Tetromino?) {
    fun shape() = pieceOverlap?.template
      ?: ghostOverlap?.template
      ?: placed

    fun isGhosted() = pieceOverlap == null && ghostOverlap != null
  }

  companion object {
    const val WIDTH = 10
    const val HEIGHT = 24
    const val INVISIBLE_ROWS = 4
  }
}
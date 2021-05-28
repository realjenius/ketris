package realjenius.ketris

import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.graphics.TextGraphics
import com.googlecode.lanterna.screen.TerminalScreen

class Graphics (private val screen: TerminalScreen, private val game: Game) {
  private val graphics: TextGraphics = screen.newTextGraphics()
  fun repaint() {
    screen.apply {
      clear()
      graphics.apply {
        if (game.isPausedOrRunning()) drawBoard() else drawStartText()
        drawBorder()
        drawDivider()
        drawStats()
        drawHoldBox()
      }
      refresh()
    }
  }

  private fun TextGraphics.drawBorder() {
    foregroundColor = BORDER_COLOR
    (0 until BORDER_HEIGHT).forEach {
      setCharacter(0, it, BORDER_CHAR)
      setCharacter(BORDER_WIDTH - 1, it, BORDER_CHAR)
    }
    (0 until BORDER_WIDTH).forEach {
      setCharacter(it, 0, BORDER_CHAR)
      setCharacter(it, BORDER_HEIGHT - 1, BORDER_CHAR)
    }
  }

  private fun TextGraphics.drawBoard() {
    game.board.allCells {
      if (it.y >= Board.INVISIBLE_ROWS) {
        val x = it.x + BOARD_START
        val y = it.y + BOARD_START - Board.INVISIBLE_ROWS
        val shape = it.shape()
        val (color: TextColor, char: Char) = when {
          shape != null && it.isTarget() -> GHOST_COLOR to GHOST_CHAR
          shape != null -> PIECE_COLORS[shape]!! to PIECE_CHAR
          else -> BLANK_CELL_COLOR to BLANK_CELL_CHAR
        }

        foregroundColor = color
        setCharacter(x, y, char)
      }
    }
  }

  private fun TextGraphics.drawStartText() {
    if (game.isNotStarted()) {
      foregroundColor = PLAY_COLOR
      putString(BOARD_START + 3, BOARD_START + 9, "PLAY")
      putString(BOARD_START + 2, BOARD_START + 10, "KETRIS")
    } else {
      foregroundColor = GAMEOVER_COLOR
      putString(BOARD_START + 3, BOARD_START + 9, "GAME")
      putString(BOARD_START + 3, BOARD_START + 10, "OVER")
    }
  }

  private fun TextGraphics.drawDivider() {
    foregroundColor = DIVIDER_COLOR
    (0 until BORDER_HEIGHT).forEach {
      setCharacter(DIVIDER, it, DIVIDER_CHAR)
    }
  }

  private fun TextGraphics.drawStats() {
    foregroundColor = STATS_COLOR
    putString(16, 3, "Level: ${game.level}")
    putString(16, 4, "Lines: ${game.lines}")
    putString(16, 5, "Score: ${game.score}")
    if (!game.isRunning()) {
      foregroundColor = PRESS_SPACE_COLOR
      putString(16, 6, "Space to Play")
    }
  }

  private fun TextGraphics.drawHoldBox() {
    foregroundColor = BORDER_COLOR
    drawRectangle(TerminalPosition(18, 8), TerminalSize(7, 7), '*')
    if (game.isPausedOrRunning()) {
      game.board.next.shape.apply {
        // TODO - something wrong with this mess.
        val startX = 21 - (width / 2)
        val startY = 11 - (height / 2)
        coordinates.forEach {
          foregroundColor = PIECE_COLORS[game.board.next]!!
          setCharacter(startX + it.first, startY + it.second, PIECE_CHAR)
        }
      }
    }
  }

  companion object {
    private const val BORDER_CHAR = '\u2588'
    private const val DIVIDER_CHAR = '\u258f'
    private const val PIECE_CHAR = '\u2587'
    private const val GHOST_CHAR = '\u2591'
    private const val BLANK_CELL_CHAR = ' '

    private const val VISIBLE_HEIGHT = Board.HEIGHT - Board.INVISIBLE_ROWS
    private const val BORDER_HEIGHT = VISIBLE_HEIGHT + 2
    private const val BORDER_WIDTH = Board.WIDTH + 2
    private const val DIVIDER = BORDER_WIDTH
    private const val BOARD_START = 1

    private val BORDER_COLOR = TextColor.RGB(49, 51, 53)
    private val STATS_COLOR = TextColor.ANSI.WHITE
    private val DIVIDER_COLOR = TextColor.ANSI.WHITE
    private val BLANK_CELL_COLOR = TextColor.ANSI.BLACK
    private val GHOST_COLOR = BORDER_COLOR
    private val PLAY_COLOR = TextColor.ANSI.GREEN
    private val GAMEOVER_COLOR = TextColor.ANSI.RED
    private val PRESS_SPACE_COLOR = TextColor.ANSI.YELLOW

    private val PIECE_COLORS = mapOf(
      TetrominoShape.I to TextColor.ANSI.CYAN,
      TetrominoShape.S to TextColor.ANSI.GREEN,
      TetrominoShape.Z to TextColor.ANSI.RED,
      TetrominoShape.O to TextColor.ANSI.YELLOW,
      TetrominoShape.T to TextColor.ANSI.MAGENTA,
      TetrominoShape.L to TextColor.ANSI.YELLOW,
      TetrominoShape.J to TextColor.ANSI.BLUE
    )
  }
}
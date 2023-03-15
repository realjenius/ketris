package realjenius.ketris

import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.graphics.TextGraphics
import com.googlecode.lanterna.screen.Screen
import com.googlecode.lanterna.screen.TerminalScreen

/**
 * A Lanterna-based renderer for the game state
 */
class Graphics (private val screen: Screen, private val game: Game) {
  private val graphics: TextGraphics = screen.newTextGraphics()
  private var size: TerminalSize = screen.terminalSize
  fun repaint() {
    screen.apply {
      doResizeIfNecessary()?.let { size = it }
      clear()
      graphics.apply {
        if (game.isPausedOrRunning()) drawBoard() else drawStartText()
        drawBorder()
        drawDivider()
        drawHoldBox()
        drawStats()
      }
      // Delta never seems to be even remotely a performance problem, and avoids most screen-tearing issues.
      refresh(Screen.RefreshType.DELTA)
    }
  }

  private fun TextGraphics.drawBorder() {
    foregroundColor = BORDER_COLOR
    (0 until BORDER_HEIGHT).forEach {
      setCharacter(BORDER_START, it, BORDER_CHAR)
      setCharacter(BORDER_START + BORDER_WIDTH - 1, it, BORDER_CHAR)
    }
    (0 until BORDER_WIDTH).forEach {
      setCharacter(BORDER_START + it, 0, BORDER_CHAR)
      setCharacter(BORDER_START + it, BORDER_HEIGHT - 1, BORDER_CHAR)
    }
  }

  private fun TextGraphics.drawBoard() {
    game.board.allCells {
      if (it.y >= Board.INVISIBLE_ROWS) {
        val x = it.x + BOARD_START
        val y = it.y + 1 - Board.INVISIBLE_ROWS
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
      putString(BOARD_START + 3, 9, "PLAY")
      putString(BOARD_START + 2, 10, "KETRIS")
    } else {
      foregroundColor = GAMEOVER_COLOR
      putString(BOARD_START + 3, 9, "GAME")
      putString(BOARD_START + 3, 10, "OVER")
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
    putString(INFO_START, 3, "Level: ${game.level}")
    putString(INFO_START, 4, "Lines: ${game.lines}")
    putString(INFO_START, 5, "Score: ${game.score}")
    if (!game.isRunning()) {
      foregroundColor = PRESS_SPACE_COLOR
      putString(INFO_START, 6, "Space to Play")
    }
  }

  private fun TextGraphics.drawHoldBox() {
    foregroundColor = BORDER_COLOR
    drawRectangle(TerminalPosition(NEXT_START, 8), TerminalSize(7, 7), '*')
    if (game.isPausedOrRunning()) {
      game.board.next.shape.apply {
        // TODO - something wrong with this mess.
        val startX = NEXT_START + 3
        val startY = 11
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
    private const val BORDER_START = 20
    private const val BOARD_START = BORDER_START + 1
    private const val DIVIDER = BORDER_START + BORDER_WIDTH
    private const val INFO_START = DIVIDER + 2
    private const val NEXT_START = DIVIDER + 4

    private val BORDER_COLOR = TextColor.RGB(49, 51, 53)
    private val STATS_COLOR = TextColor.ANSI.WHITE
    private val DIVIDER_COLOR = TextColor.ANSI.WHITE
    private val BLANK_CELL_COLOR = TextColor.ANSI.BLACK
    private val GHOST_COLOR = TextColor.RGB(69, 71, 73)
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
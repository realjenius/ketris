package realjenius.ketris

import com.googlecode.lanterna.screen.TerminalScreen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory

fun main() {
  TerminalScreen(DefaultTerminalFactory().setPreferTerminalEmulator(false).createTerminal()).apply {
    use {
      cursorPosition = null
      startScreen()
      val clock = Clock()
      val game = Game(clock)
      val input = Input(this, game)
      val graphics = Graphics(this, game)
      var inputState = InputState.EMPTY

      clock.loopUntil({ inputState.eof }) {
        inputState = input.process()
        game.handle(inputState)
        graphics.repaint()
      }
    }
  }
}
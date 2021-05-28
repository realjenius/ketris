package realjenius.ketris

import com.googlecode.lanterna.screen.TerminalScreen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import com.googlecode.lanterna.terminal.ansi.UnixTerminal

fun main() = Ketris.start()


object Ketris {
    private lateinit var screen: TerminalScreen
    private lateinit var clock: Clock
    private lateinit var input: Input
    private lateinit var game: Game
    private lateinit var graphics: Graphics

    fun start() {
        val term = if (System.getenv("EMULATE").isNullOrBlank()) UnixTerminal() else DefaultTerminalFactory().createTerminal()
        screen = TerminalScreen(term)
        screen.use {
            screen.startScreen()
            clock = Clock()
            game = Game(clock)
            input = Input(screen, game)
            graphics = Graphics(screen, game)

            var inputState = InputState.EMPTY

            clock.loopUntil({ inputState.eof }) {
                inputState = input.process()
                game.handle(inputState)
                graphics.repaint()
            }
        }
    }
}



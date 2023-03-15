package realjenius.ketris

import com.googlecode.lanterna.input.KeyType
import com.googlecode.lanterna.screen.Screen
import com.googlecode.lanterna.screen.TerminalScreen

/** Represents the changes to trigger in the game based on the last read input from the player */
data class InputState(
  val rotate: Rotation = Rotation.None,
  val xDelta: Int = 0,
  val drop: Boolean = false,
  val eof: Boolean = false,
  val togglePause: Boolean = false
) {
  companion object {
    val EMPTY = InputState()
  }
}

class Input(private val screen: Screen, private val game: Game) {

  fun process() : InputState {
    val key = screen.pollInput()

    return when {
      key == null -> InputState.EMPTY
      key.keyType == KeyType.Escape || (key.character == ' ' && !game.isRunning()) -> InputState(togglePause = true)
      key.keyType == KeyType.ArrowUp -> InputState(rotate = Rotation.Clockwise)
      key.keyType == KeyType.ArrowDown -> InputState(rotate = Rotation.Counterclockwise)
      key.character == ' ' -> InputState(drop = true)
      key.keyType == KeyType.ArrowLeft -> InputState(xDelta = -1)
      key.keyType == KeyType.ArrowRight -> InputState(xDelta = 1)
      key.keyType == KeyType.EOF -> InputState(eof = true)

      else -> InputState.EMPTY
    }
  }

}

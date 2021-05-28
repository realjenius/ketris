package realjenius.ketris

class Game(val clock: Clock) {

  var level: Int = 1
  var score: Long = 0
  var lines: Int = 0
  var state: State = State.NotStarted
  lateinit var board: Board
  private val pieceBag = generateSequence { TetrominoShape.all.shuffled() }.flatten().iterator()

  fun start() {
    state = State.Running
    clock.adjustGravity(1)
    board = Board(this)
  }

  fun pause() {
    state = State.Paused
  }

  fun isNotStarted() = state == State.NotStarted

  fun isPausedOrRunning() = state == State.Paused || state == State.Running

  fun isRunning() = state == State.Running

  fun resume() {
    state = State.Running
  }

  fun restart() {
    // probably record high score etc.
    start()
  }

  fun gameOver() {
    state = State.GameOver
  }

  fun handle(input: InputState) {
    if (input.togglePause) state.pauseAction(this)
    else if (isRunning()) {
      if (!board.move(input)) gameOver()
    }
  }

  fun piece() = pieceBag.next()

  fun addLines(lineAdd: Int) {
    if (lineAdd == 0) return
    lines += lineAdd
    score += when (lineAdd) {
      1 -> 100
      2 -> 300
      3 -> 500
      else -> 800
    } * level

    if (lines % 10 == 0) {
      level++
      clock.adjustGravity(level)
    }
  }

  enum class State(val pauseAction: (Game) -> Unit) {
    NotStarted(Game::start),
    Running(Game::pause),
    Paused(Game::resume),
    GameOver(Game::restart)
  }
}
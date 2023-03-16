package realjenius.ketris

class Game(val clock: Clock) {
  var level: Int = 1
  var score: Long = 0
  var lines: Int = 0
  var state: State = State.NotStarted
  lateinit var board: Board
  private val pieceBag = generateSequence { TetrominoShape.all.shuffled() }.flatten().iterator()

  fun start() {
    level = 1
    score = 0
    lines = 0
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
    if (input.togglePause) {
      state.pauseAction(this)
    } else if (isRunning()) {
      if (!board.move(input)) gameOver()
    }
  }

  fun piece() = pieceBag.next()

  fun addLines(lineAdd: Int) {
    if (lineAdd == 0) return
    lines += lineAdd
    score += (SCORES_BY_LINE_ADD[lineAdd.coerceAtMost(4) - 1] * level)

    val newLevel = (lines / LINES_PER_LEVEL) + 1
    if (level != newLevel) {
      level = newLevel
      clock.adjustGravity(level)
    }
  }

  enum class State(val pauseAction: (Game) -> Unit) {
    NotStarted(Game::start),
    Running(Game::pause),
    Paused(Game::resume),
    GameOver(Game::restart)
  }

  companion object {
    private val SCORES_BY_LINE_ADD = listOf(100, 300, 500, 800)
    private const val LINES_PER_LEVEL = 10
  }
}
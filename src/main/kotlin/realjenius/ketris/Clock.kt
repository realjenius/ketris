package realjenius.ketris

import java.time.Duration


/**
 * The core clock logic for a gravity-based tetris-like game.
 */
class Clock {

  private var framesPieceActive: Int = 0
  private var timeSimulated: Double = 0.0
  private var gravity: Int = 0

  fun loopUntil(predicate: () -> Boolean, action: () -> Unit) {
    while (!predicate()) {
      val loopStart = System.nanoTime()
      action()

      val duration = LOOP_TIME.minus(Duration.ofNanos(System.nanoTime() - loopStart)).toMillis()
      if (duration > 0) Thread.sleep(duration)
    }
  }

  fun newPiece() {
    framesPieceActive = 0
    timeSimulated = 0.0
  }

  fun adjustGravity(gravity: Int) {
    this.gravity = (gravity-1).coerceAtMost(CELLS_PER_FRAME_PER_LEVEL.size-1)
  }

  fun checkForGravity(frames: Int = 1) : Int {
    framesPieceActive += frames
    var drop = 0
    while (framesPieceActive > timeSimulated) {
      drop++
      timeSimulated += (1 / CELLS_PER_FRAME_PER_LEVEL[gravity])
    }
    return drop
  }

  companion object {
    private val LOOP_TIME = Duration.ofMillis(1000L / 60L)

    val CELLS_PER_FRAME_PER_LEVEL = listOf(
      0.01667,
      0.021017,
      0.026977,
      0.035256,
      0.04693,
      0.06361,
      0.0879,
      0.1236,
      0.1775,
      0.2598,
      0.388,
      0.59,
      0.92,
      1.46,
      2.36
    )
  }
}
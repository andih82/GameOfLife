package org.example
import kotlinx.coroutines.*
import org.example.Options.DELAY_MS
import org.example.Options.SIZE
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener
import kotlin.random.Random

class Cell(val x : Int, val y :Int, val universe: Universe)  {

    var alive = false
    var nextGenAlive = false
    var state = CellState.IDLE
    var actionListeners = mutableListOf<ChangeListener>()

    suspend fun evolve(neighbours : Int) : Boolean {
        changeState(CellState.EVOLVING)
        delay(Random.nextLong(DELAY_MS))
            nextGenAlive = when {
                alive && neighbours < 2 -> false
                alive && neighbours > 3 -> false
                !alive && neighbours == 3 -> true
                else -> alive
            }
        changeState(CellState.EVOLVED)
        nextGenAlive
        return nextGenAlive != alive
    }

    suspend fun countNeighbours(): Int {
        changeState(CellState.COUNTING)
        delay(Random.nextLong(DELAY_MS))
        var count = 0
        for (i in -1..1) {
            for (j in -1..1) {
                if (i == 0 && j == 0) {
                    continue
                }
                val x1 = x + i
                val y1 = y + j
                if (x1 >= 0 && x1 < SIZE && y1 >= 0 && y1 < SIZE && universe.grid[x1][y1].alive) {
                    count++
                }
            }
        }
        changeState(CellState.COUNTED)
        delay(Random.nextLong(DELAY_MS))
        return count
    }

    suspend fun update() {
        changeState(CellState.UPDATING)
        delay(Random.nextLong(DELAY_MS))
        alive = nextGenAlive
        changeState(CellState.UPDATED)
        delay(Random.nextLong(DELAY_MS))
    }

    fun changeState(newState: CellState) {
        state = newState
        actionListeners.forEach { it.stateChanged(
            object : ChangeEvent(this) {})
        }
    }

}

enum class CellState {
    COUNTING, COUNTED, EVOLVING, EVOLVED, UPDATING, UPDATED, IDLE
}
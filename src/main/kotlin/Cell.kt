package org.example
import kotlinx.coroutines.*
import org.example.Options.DELAY_MS
import javax.swing.event.ChangeListener
import kotlin.random.Random

class Cell(val x : Int, val y :Int)  {

    var alive = false
    var nextGenAlive = false
    var state = CellState.IDLE
    var actionListeners = mutableListOf<ChangeListener>()

    suspend fun evolve(neighbours: Int) : Boolean = coroutineScope {
        println("evolve $x $y")
        changeState(CellState.EVOLVING)
            nextGenAlive = when {
                alive && neighbours < 2 -> false
                alive && neighbours > 3 -> false
                !alive && neighbours == 3 -> true
                else -> alive
            }
        delay(Random.nextLong(DELAY_MS))
        println("evolve $x $y done")
        changeState(CellState.EVOLVED)
        nextGenAlive
    }

    fun changeState(newState: CellState) {
        state = newState
        actionListeners.forEach { it.stateChanged(
            object : javax.swing.event.ChangeEvent(this) {})
        }
    }

}

enum class CellState {
    COUNTING, COUNTED, EVOLVING, EVOLVED, IDLE
}
package org.example

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.example.Options.DELAY_MS
import org.example.Options.SHOW_CELL_STATE
import org.example.Options.SIZE
import java.util.concurrent.atomic.AtomicInteger
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener

class Cell(val x: Int, val y: Int, val universe: Universe) {

    val neighbours =
        if ((x == 0 || x == SIZE - 1) && (y == 0 || y == SIZE - 1)) 3 else if (x == 0 || x == SIZE - 1 || y == 0 || y == SIZE - 1) 5 else 8
    var counted = AtomicInteger(0)
    var alive = false
    var nextGenAlive = false
    val state = MutableStateFlow<CellState>(CellState.DEAD)

    var changeListener: ChangeListener? = null

    var age = 0

    suspend fun lifecycle() {
        while (true) {
            if (universe.age.get() > age) {
                when (state.value) {
                    CellState.ALIVE, CellState.DEAD -> {
                        evolve()
                    }

                    CellState.EVOLVING -> {
                        update()
                    }

                    CellState.UPDATING -> {
                        update()
                    }
                }
            }
//            delay(Random.nextLong(DELAY_MS))
            delay(DELAY_MS)
        }
    }

    fun evolve() {
        changeVisualState(CellState.EVOLVING)
        val neighbours = countNeighbours()
        nextGenAlive = when {
            alive && neighbours < 2 -> false
            alive && neighbours > 3 -> false
            !alive && neighbours == 3 -> true
            else -> alive
        }
    }

    fun countNeighbours(): Int {
        var count = 0
        for (i in -1..1) {
            for (j in -1..1) {
                if (i == 0 && j == 0) continue
                val x1 = x + i
                val y1 = y + j

                if (x1 >= 0 && x1 < SIZE && y1 >= 0 && y1 < SIZE) {
                    universe.grid[x1][y1].counted.incrementAndGet()
                    if (universe.grid[x1][y1].alive) {
                        count++
                    }
                }
            }
        }
        return count
    }

    fun update() {
        changeVisualState(CellState.UPDATING)
        if (counted.get() == neighbours) {
            counted.set(0)
                alive = nextGenAlive
                changeVisualState(if (alive) CellState.ALIVE else CellState.DEAD)

            universe.evovledCells.incrementAndGet()
            age++
        }
    }

    fun changeVisualState(newState: CellState) {
        state.value = newState
        if(SHOW_CELL_STATE) {
            changeListener?.stateChanged(object : ChangeEvent(this) {})
        }else if (newState == CellState.ALIVE || newState == CellState.DEAD){
            changeListener?.stateChanged(object : ChangeEvent(this) {})
        }
    }

}

enum class CellState {
    EVOLVING, UPDATING, ALIVE, DEAD
}
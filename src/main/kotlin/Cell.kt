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

    var counted = AtomicInteger(0)
    var alive = false
    var nextGenAlive = false
    val state = MutableStateFlow<CellState>(CellState.DEAD)
    val neighboursIndices = createNeighboursIndices()
    var changeListener: ChangeListener? = null
    var age = 0


    suspend fun lifecycle() {

        while (true) {
            if (universe.age.get() > age) {
                when (state.value) {
                    CellState.ALIVE, CellState.DEAD -> {
                        evolve()
                    }
                    CellState.EVOLVING, CellState.UPDATING -> {
                        update()
                    }
                }
            }
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

    fun createNeighboursIndices(): List<Pair<Int, Int>> {
        val neighbours = mutableListOf<Pair<Int, Int>>()
        for (i in -1..1) {
            for (j in -1..1) {
                if (i == 0 && j == 0) continue
                val x1 = x + i
                val y1 = y + j

                if (x1 >= 0 && x1 < SIZE && y1 >= 0 && y1 < SIZE) {
                    neighbours.add(x1 to y1)
                }
            }
        }
        return neighbours
    }

    fun countNeighbours(): Int {
        var count = 0
        for ((x,y) in neighboursIndices) {
            if (universe.grid[x][y].alive) {
                count++
            }
            universe.grid[x][y].counted.incrementAndGet()
        }
        return count
    }

    fun update() {
        changeVisualState(CellState.UPDATING)
        if (counted.get() < neighboursIndices.size) return
        counted.set(0)
        alive = nextGenAlive
        changeVisualState(if (alive) CellState.ALIVE else CellState.DEAD)
        universe.evovledCells.incrementAndGet()
        age++
    }

    fun changeVisualState(newState: CellState) {
        if (state.value == newState) return
        state.value = newState
        if(SHOW_CELL_STATE) {
            changeListener?.stateChanged(
                ChangeEvent(this)
            )
        }else if (newState == CellState.ALIVE || newState == CellState.DEAD){
            changeListener?.stateChanged(
                ChangeEvent(this)
            )
        }

    }

}



enum class CellState {
    EVOLVING, UPDATING, ALIVE, DEAD
}
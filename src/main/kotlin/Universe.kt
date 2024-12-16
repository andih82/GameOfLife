package org.example

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.Options.SIZE
import java.util.concurrent.atomic.AtomicInteger

class Universe {

    companion object {


        fun defaultSart(): Universe {
            return if (SIZE >= 50) Universe().apply {
                grid[22][22].alive = true
                grid[22][23].alive = true
                grid[22][24].alive = true
                grid[23][22].alive = true
                grid[23][24].alive = true
                grid[24][22].alive = true
                grid[24][24].alive = true

                grid[28][22].alive = true
                grid[28][23].alive = true
                grid[28][24].alive = true
                grid[27][22].alive = true
                grid[27][24].alive = true
                grid[26][22].alive = true
                grid[26][24].alive = true

                grid[22][22].state.value = CellState.ALIVE
                grid[22][23].state.value = CellState.ALIVE
                grid[22][24].state.value = CellState.ALIVE
                grid[23][22].state.value = CellState.ALIVE
                grid[23][24].state.value = CellState.ALIVE
                grid[24][22].state.value = CellState.ALIVE
                grid[24][24].state.value = CellState.ALIVE

                grid[28][22].state.value = CellState.ALIVE
                grid[28][23].state.value = CellState.ALIVE
                grid[28][24].state.value = CellState.ALIVE
                grid[27][22].state.value = CellState.ALIVE
                grid[27][24].state.value = CellState.ALIVE
                grid[26][22].state.value = CellState.ALIVE
                grid[26][24].state.value = CellState.ALIVE
            } else if (SIZE >= 10) Universe().apply {
                grid[3][3].alive = true
                grid[3][4].alive = true
                grid[3][5].alive = true
                grid[4][3].alive = true
                grid[4][4].alive = true
                grid[4][5].alive = true
                grid[5][3].alive = true
                grid[5][4].alive = true
                grid[5][5].alive = true
                grid[3][3].state.value = CellState.ALIVE
                grid[3][4].state.value = CellState.ALIVE
                grid[3][5].state.value = CellState.ALIVE
                grid[4][3].state.value = CellState.ALIVE
                grid[4][4].state.value = CellState.ALIVE
                grid[4][5].state.value = CellState.ALIVE
                grid[5][3].state.value = CellState.ALIVE
                grid[5][4].state.value = CellState.ALIVE
                grid[5][5].state.value = CellState.ALIVE
            }
            else Universe()
        }
    }

    var age = AtomicInteger(0)
    var desiredAge = AtomicInteger(0)
    var evovledCells = AtomicInteger(SIZE * SIZE)
    var grid: Array<Array<Cell>> = Array(SIZE) { i -> Array(SIZE) { j -> Cell(i, j, this) } }

    var evolutionJob: Job? = null

    val isRunning: Boolean
        get() = evovledCells.get() < SIZE * SIZE

    fun stop() {
        evolutionJob?.cancel()
    }

    fun start() {
        repeat(1000){
            step()
        }
    }
    fun step() {
        if(evolutionJob == null || evolutionJob?.isActive == false){
            evolutionJob = evolve()
            println("started $evolutionJob")
        }
        desiredAge.incrementAndGet()
    }

    fun evolve() = CoroutineScope(Dispatchers.Default).launch {

        launch {
            while (true) {
                while (isRunning) {
                    println("${evovledCells.get()} cells evolved in this generation")
                    delay(50L)
                }
                println("All cells evolved in generation $age")
                if (desiredAge.get() > age.get()) {
                    evovledCells.set(0)
                    age.incrementAndGet()
                } else {
                    println("completed $evolutionJob")
                    evolutionJob?.cancel()
                }
                delay(1000)
            }
        }
        grid.flatten().shuffled().forEach { cell ->
            launch(Dispatchers.IO) {
                cell.lifecycle()
            }
        }

    }

}
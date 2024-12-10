package org.example

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.example.Options.PARALLEL
import org.example.Options.SIZE
import java.util.concurrent.CancellationException
import javax.swing.event.ChangeListener
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.random.Random

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
            }
            else Universe()
        }
    }

    var grid: Array<Array<Cell>> = Array(SIZE) { i -> Array(SIZE) { j -> Cell(i, j, this) } }
    var stop = false

    var evolutionJob: Job? = null

    suspend fun evolve(): Boolean {
        var changed = false
            if (PARALLEL) {
                runBlocking {
                    evolutionJob = launch(){
                        println("root $coroutineContext")
                        async() {
                            grid.flatten().forEach { cell ->
                                val neighbours = async() {
                                    cell.countNeighbours()
                                }
                                launch() {
                                    if (cell.evolve(neighbours.await()))
                                        changed = true
                                }

                            }
                        }.await()

                        grid.flatten().forEach { cell ->
                            launch() {
                                cell.update()
                                cell.changeState(CellState.IDLE)
                            }
                        }

                    }
                }
            } else {
                runBlocking {
                    evolutionJob = launch() {
                        grid.flatten().forEach { cell ->
                                val neighbours = cell.countNeighbours()
                                val evolved = cell.evolve(neighbours)
                                if (!changed && evolved) {
                                    changed = true
                                }

                        }
                        grid.flatten().forEach { cell ->
                                cell.update()
                                cell.changeState(CellState.IDLE)
                        }
                    }
                }
            }
        return changed
    }

    fun addActionsListener(listener: ChangeListener) {
        grid.forEach { it.forEach { it.actionListeners.add(listener) } }
    }
}

fun Array<Array<Cell>>.print() {
    repeat(2) { println("========================================================================================") }
    for (element in this) {
        for (j in element.indices) {
            print(if (element[j].alive) "X" else ".")
        }
        println()
    }
}
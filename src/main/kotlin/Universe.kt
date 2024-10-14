package org.example

import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.example.Options.DELAY_MS
import org.example.Options.PARALLEL
import org.example.Options.SIZE
import javax.swing.event.ChangeListener
import kotlin.random.Random

class Universe {

    companion object {


        fun defaultSart(): Universe {
            return if(SIZE >= 50) Universe().apply {
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
            } else  Universe()
        }
    }

    var grid: Array<Array<Cell>> = Array(SIZE) { i -> Array(SIZE) { j -> Cell(i, j) } }

    fun evolve() : Boolean {
        val nextGen = Array(SIZE) { i -> Array(SIZE) { j -> Cell(i, j) } }
        var result = false

        runBlocking {
            if (PARALLEL) {
                for (element in grid) {
                    for (j in element.indices) {
                        val neighbours = async {
                            grid.countNeighbours(element[j].x, element[j].y)
                        }
                        launch {
                            val alive = element[j].evolve(neighbours.await())
                            nextGen[element[j].x][element[j].y].alive = alive
                            if (!result && alive != element[j].alive) {
                                result = true
                            }

                        }

                    }
                }
            } else {
                for (element in grid) {
                    for (j in element.indices) {
                        val neighbours = grid.countNeighbours(element[j].x, element[j].y)
                        val alive = element[j].evolve(neighbours)
                        nextGen[element[j].x][element[j].y].alive = alive
                        if (!result && alive != element[j].alive) {
                            result = true
                        }
                    }
                }
            }
        }
        grid = nextGen
        return result
    }

    fun addActionsListener(listener: ChangeListener) {
        grid.forEach { it.forEach { it.actionListeners.add(listener) } }
    }
}

suspend fun Array<Array<Cell>>.countNeighbours(x: Int, y: Int): Int {
    println("countNeighbours $x $y")
    this[x][y].changeState(CellState.COUNTING)
    delay(Random.nextLong(DELAY_MS))
    var count = 0
    for (i in -1..1) {
        for (j in -1..1) {
            if (i == 0 && j == 0) {
                continue
            }
            val x1 = x + i
            val y1 = y + j
            if (x1 >= 0 && x1 < SIZE && y1 >= 0 && y1 < SIZE && this[x1][y1].alive) {
                count++
            }
        }
    }
    this[x][y].changeState(CellState.COUNTED)
    println("countNeighbours $x $y done")
    return count
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
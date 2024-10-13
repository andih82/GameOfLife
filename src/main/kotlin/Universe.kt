package org.example

import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

class Universe {

    companion object {
        const val SIZE = 50;

        fun defaultSart(): Universe {
            return Universe().apply {
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
            }
        }
    }

    var grid: Array<Array<Cell>> = Array(SIZE) { i -> Array(SIZE) { j -> Cell(i, j) } }

    fun evolve() {
        val nextGen = Array(SIZE) { i -> Array(SIZE) { j -> Cell(i, j) } }
        runBlocking {
            for (element in grid) {
                for (j in element.indices) {
                    val neighbours = async {
                            grid.countNeighbours(element[j].x, element[j].y)
                        }
                    launch {
                        nextGen[element[j].x][element[j].y].alive =
                        element[j].evolve(neighbours.await())
                    }
                }
            }
        }
        grid = nextGen
    }
}

suspend fun Array<Array<Cell>>.countNeighbours(x: Int, y: Int): Int {
    println("countNeighbours $x $y")
    var count = 0
    for (i in -1..1) {
        for (j in -1..1) {
            if (i == 0 && j == 0) {
                continue
            }
            val x1 = x + i
            val y1 = y + j
            if (x1 >= 0 && x1 < Universe.SIZE && y1 >= 0 && y1 < Universe.SIZE && this[x1][y1].alive) {
                count++
            }
        }
    }
    delay(Random.nextLong(50))
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
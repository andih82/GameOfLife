package org.example
import kotlinx.coroutines.*
import kotlin.random.Random

class Cell(val x : Int, val y :Int)  {

    var alive = false
    var nextGenAlive = false

    suspend fun evolve(neighbours: Int) : Boolean = coroutineScope {
        println("evolve $x $y")
            nextGenAlive = when {
                alive && neighbours < 2 -> false
                alive && neighbours > 3 -> false
                !alive && neighbours == 3 -> true
                else -> alive
            }
        delay(Random.nextLong(50))
        println("evolve $x $y done")
        nextGenAlive
    }


}
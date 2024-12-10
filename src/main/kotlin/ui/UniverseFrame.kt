package org.example.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.withContext
import org.example.CellState
import org.example.Options.CELL_SIZE
import org.example.Options.SHOW_CELL_STATE
import org.example.Options.SIZE
import org.example.Universe
import java.awt.Canvas
import java.awt.Color
import java.awt.Graphics
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener

class UniverseFrame(var universe: Universe) : Canvas(), ChangeListener {

    var running = false

    init {
        setSize(SIZE * CELL_SIZE, SIZE * CELL_SIZE)
        preferredSize = size
        universe.let { it.grid.forEach { row -> row.forEach { cell -> cell.actionListeners.add(this) } } }
        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent?) {
                e?.let {
                    val x = it.x / CELL_SIZE
                    val y = it.y / CELL_SIZE
                    universe.grid[x][y].alive = !universe.grid[x][y].alive
                    repaintCell(x, y)
                }
            }
        })
    }

    fun drawUniverse() {
        for (i in 0 until SIZE) {
            for (j in 0 until SIZE) {
                repaintCell(i, j)
            }
        }
    }

    fun repaintCell(x: Int, y: Int, state: CellState = CellState.IDLE) {
        graphics.clearCell(x, y)
        when (state) {
            CellState.IDLE -> {
                if (universe.grid[x][y].alive)
                    graphics.fillCell(x, y, Color.BLACK)
            }

            CellState.EVOLVING -> graphics.drawCell(x, y, Color.BLUE)
            CellState.EVOLVED -> graphics.fillCell(x, y, Color.BLUE)
            CellState.COUNTING -> graphics.drawCell(x, y, Color.RED)
            CellState.COUNTED -> graphics.fillCell(x, y, Color.RED)
            CellState.UPDATING -> graphics.drawCell(x, y, Color.GREEN)
            CellState.UPDATED -> graphics.fillCell(x, y, Color.GREEN)
        }
    }

    override fun paint(g: Graphics?) {
        super.paint(g)
        drawUniverse()
    }

    fun actionPerformed(e: ActionEvent?) {
        CoroutineScope(Dispatchers.Default).launch {
            when (e?.actionCommand) {
                "Reset" -> reset()
                "Step" -> step()
                "Stop" -> stop()
                "Play" -> play()
                "Clear" -> clear()
                else -> println("Unknown command")
            }
        }
    }

    suspend fun reset() {
        stop()
        universe = Universe.defaultSart().apply { addActionsListener(this@UniverseFrame) }
        drawUniverse()
    }

    suspend fun clear() {
        stop()
        universe = Universe().apply { addActionsListener(this@UniverseFrame) }
        drawUniverse()
    }

    suspend fun step() {
        if (universe.evolutionJob?.isActive != true) {
            universe.evolve()
        }
    }

    suspend fun play() {
        if (universe.evolutionJob?.isActive != true) {
            while (universe.evolve()) {
                drawUniverse()
                delay(100L)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun stop() {
        universe.evolutionJob?.let {
                println("stopping $it")
                it.parent?.cancelAndJoin()
        }
    }

    override fun stateChanged(e: ChangeEvent?) {
        if (SHOW_CELL_STATE) {
            e?.source?.let {
                val cell = it as org.example.Cell
                repaintCell(cell.x, cell.y, cell.state)
            }
        }
    }
}



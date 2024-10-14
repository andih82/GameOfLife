package org.example.ui

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import org.example.CellState
import org.example.Options.CELL_SIZE
import org.example.Options.SHOW_CELLSTATE
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

class UniverseFrame(var universe: Universe) : Canvas(), ActionListener, ChangeListener {

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

    fun drawUniverse(g: Graphics) {
        for (i in 0 until SIZE) {
            for (j in 0 until SIZE) {
                if (universe.grid[i][j].alive) {
                    g.fillCell(i, j)
                } else {
                    g.clearCell(i, j)
                }
            }
        }
    }

    fun repaintCell(x: Int, y: Int, state: CellState = CellState.IDLE) {
        when (state) {
            CellState.IDLE -> {
                if (universe.grid[x][y].alive)
                    graphics.fillCell(x, y)
                else
                    graphics.clearCell(x, y)

            }

            CellState.EVOLVED -> {
                if (universe.grid[x][y].nextGenAlive)
                    graphics.fillCell(x, y)
                else
                    graphics.clearCell(x, y)

            }

            CellState.COUNTING -> graphics.drawCell(x, y, Color.RED)
            CellState.COUNTED -> graphics.fillCell(x, y, Color.RED)
            CellState.EVOLVING -> graphics.fillCell(x, y, Color.BLUE)
        }
        repaint(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE)
    }

    override fun paint(g: Graphics?) {
        super.paint(g)
        drawUniverse(g!!)
    }

    override fun repaint() {
        drawUniverse(graphics)
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun actionPerformed(e: ActionEvent?) {
        GlobalScope.launch(Dispatchers.Swing) {
            when (e?.actionCommand) {
                "Reset" -> reset()
                "Step" -> step()
                "Play" -> play()
                "Stop" -> stop()
                "Clear" -> clear()

                else -> println("Unknown command")

            }
        }
    }

    fun reset() {
        universe = Universe.defaultSart().apply { addActionsListener(this@UniverseFrame) }
        repaint()
    }

    fun clear() {
        universe = Universe().apply { addActionsListener(this@UniverseFrame) }
        repaint()
    }

    fun step() {
        universe.evolve().also { universe.addActionsListener(this@UniverseFrame) }
        repaint()
    }

    suspend fun play() {
        running = true
        while (running && universe.evolve()) {
            universe.addActionsListener(this@UniverseFrame)
            delay(10L)
            repaint()
        }
    }

    fun stop() {
        running = false
    }

    override fun stateChanged(e: ChangeEvent?) {
        if (SHOW_CELLSTATE) {
            e?.source?.let {
                val cell = it as org.example.Cell
                repaintCell(cell.x, cell.y, cell.state)
            }
        }
    }
}



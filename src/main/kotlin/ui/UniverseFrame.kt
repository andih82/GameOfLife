package org.example.ui

import org.example.CellState
import org.example.Options.CELL_SIZE
import org.example.Options.SIZE
import org.example.Universe
import java.awt.Canvas
import java.awt.Color
import java.awt.Graphics
import java.awt.event.ActionEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener

class UniverseFrame(var universe: Universe) : Canvas(), ChangeListener {

    init {
        setSize(SIZE * CELL_SIZE, SIZE * CELL_SIZE)
        preferredSize = size
        universe.let { it.grid.flatten().forEach { cell -> cell.changeListener = this } }
        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent?) {
                e?.let {
                    val x = it.x / CELL_SIZE
                    val y = it.y / CELL_SIZE
                    universe.grid[x][y].alive = !universe.grid[x][y].alive
                    universe.grid[x][y].state.value = if (universe.grid[x][y].alive) CellState.ALIVE else CellState.DEAD
                    repaintCell(x, y, universe.grid[x][y].state.value)
                }
            }
        })
    }

    fun drawUniverse() {
        for (i in 0 until SIZE) {
            for (j in 0 until SIZE) {
                if (universe.grid[i][j].alive) {
                    repaintCell(i, j, CellState.ALIVE)
                }
            }
        }
    }

    fun repaintCell(x: Int, y: Int, state: CellState = CellState.DEAD) {
        when (state) {
            CellState.ALIVE -> graphics.fillCell(x, y, Color.BLACK)
            CellState.DEAD -> graphics.clearCell(x, y)
            CellState.EVOLVING -> graphics.fillCell(x, y, Color.BLUE)
            CellState.UPDATING -> graphics.fillCell(x, y, Color.RED)
        }
    }

    override fun paint(g: Graphics?) {
        super.paint(g)
        drawUniverse()
    }

    fun actionPerformed(e: ActionEvent?) {
        when (e?.actionCommand) {
            "Reset" -> reset()
            "Step" -> step()
            "Stop" -> stop()
            "Play" -> play()
            "Clear" -> clear()
            else -> println("Unknown command")
        }
    }

    fun reset() {
        stop()
        universe = Universe.defaultSart().apply { grid.flatten().forEach { it.changeListener = this@UniverseFrame } }
        paint(graphics)
    }

    fun clear() {
        stop()
        universe = Universe().apply { grid.flatten().forEach { it.changeListener = this@UniverseFrame } }
        paint(graphics)
    }

    fun step() {
        universe.step()
    }

    fun play() {
        universe.start()
    }

    fun stop() {
        universe.stop()
    }

    override fun stateChanged(e: ChangeEvent?) {
        e?.source?.let {
            val cell = it as org.example.Cell
            repaintCell(cell.x, cell.y, cell.state.value)
        }

    }
}



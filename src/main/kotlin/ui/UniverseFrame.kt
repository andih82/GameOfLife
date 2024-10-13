package org.example.ui

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import org.example.CellState
import org.example.Universe
import java.awt.Canvas
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener

class UniverseFrame(var universe : Universe ) : Canvas(), ActionListener, ChangeListener {

    var running = false

    init {
        setSize(Universe.SIZE * 10, Universe.SIZE * 10)
        preferredSize = size
        universe.let { it.grid.forEach { row -> row.forEach { cell -> cell.actionListeners.add(this) } } }
        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent?) {
                e?.let {
                    val x = it.x / 10
                    val y = it.y / 10
                    universe.grid[x][y].alive = !universe.grid[x][y].alive
                    repaintCell(x, y)
                }
            }
        })
    }

    fun drawUniverse(g: Graphics) {
        for (i in 0 until Universe.SIZE) {
            for (j in 0 until Universe.SIZE) {
                if (universe.grid[i][j].alive) {
                    g.fillRect(i * 10, j * 10, 10, 10)
                } else {
                    g.clearRect(i * 10, j * 10, 10, 10)
                }
            }
        }
    }

    fun repaintCell(x: Int, y: Int, state: CellState = CellState.IDLE) {
        graphics.clearRect(x * 10, y * 10, 10, 10)

        when (state) {
            CellState.IDLE -> {
                if (universe.grid[x][y].alive) {
                    graphics.fillRect(x * 10, y * 10, 10, 10)
                }
            }
            CellState.EVOLVED -> {
                if (universe.grid[x][y].nextGenAlive) {
                    graphics.fillRect(x * 10, y * 10, 10, 10)
                }
            }
            CellState.COUNTING -> {
                val g2 = graphics as Graphics2D
                g2.color = Color.RED
                g2.drawRect(x * 10, y * 10, 10, 10)
            }

            CellState.COUNTED -> {
                val g2 = graphics as Graphics2D
                g2.color = Color.RED
                g2.fillRect(x * 10, y * 10, 10, 10)
            }

            CellState.EVOLVING -> {
                val g2 = graphics as Graphics2D
                g2.color = Color.BLUE
                g2.fillRect(x * 10, y * 10, 10, 10)
            }
        }
        repaint(x * 10, y * 10, 10, 10)
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
        universe = Universe.defaultSart()
        universe.grid.forEach { row -> row.forEach { cell -> cell.actionListeners.add(this) } }
        repaint()
    }

    fun clear() {
        universe = Universe()
        universe.grid.forEach { row -> row.forEach { cell -> cell.actionListeners.add(this) } }
        repaint()
    }

    fun step() {
        universe.evolve()
        universe.grid.forEach { row -> row.forEach { cell -> cell.actionListeners.add(this) } }
        repaint()
    }

    suspend fun play() {
        running = true
        while (running && universe.evolve()) {
            universe.grid.forEach { row -> row.forEach { cell -> cell.actionListeners.add(this) } }
            delay(10L)
        }
    }

    fun stop() {
        running = false
    }

    override fun stateChanged(e: ChangeEvent?) {
        if(Universe.SHOW_CELLSTATE) {
            e?.source?.let {
                val cell = it as org.example.Cell
                repaintCell(cell.x, cell.y, cell.state)
            }
        }
    }
}
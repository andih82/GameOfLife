package org.example.ui

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import org.example.Universe
import java.awt.Canvas
import java.awt.Graphics
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

class UniverseFrame(var universe : Universe ) : Canvas(), ActionListener {

    var running = false

    init {
        setSize(Universe.SIZE * 10,Universe.SIZE * 10)
        preferredSize = size

        addMouseListener(object : MouseAdapter(){
            override fun mouseClicked(e: MouseEvent?) {
                e?.let {
                    val x = it.x / 10
                    val y = it.y / 10
                    universe.grid[x][y].alive = !universe.grid[x][y].alive
                    repaintCell(x, y)
                }
            }
        })
    }

    fun drawUniverse(g : Graphics){
        for (i in 0 until Universe.SIZE) {
            for (j in 0 until Universe.SIZE) {
                if (universe.grid[i][j].alive) {
                    g.fillRect(i * 10, j * 10, 10, 10)
                }
                else {
                    g.clearRect(i * 10, j * 10, 10, 10)
                }
            }
        }
    }

    fun repaintCell(x: Int, y: Int){
        graphics.clearRect(x * 10, y * 10, 10, 10)
        if (universe.grid[x][y].alive) {
            graphics.fillRect(x * 10, y * 10, 10, 10)
        }
        repaint( x * 10, y * 10, 10, 10   )
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
                    "Clear" -> {
                        universe = Universe()
                        repaint()
                    }
                    else -> println("Unknown command")

            }
        }
    }

    fun reset(){
        universe = Universe.defaultSart()
        repaint()
    }

    suspend fun step(){
        universe.evolve()
        repaint()
    }

    suspend fun play(){
        running = true
        while (running){
            universe.evolve()
            repaint()
            delay(10)
        }
    }

    fun stop(){
        running = false
    }
}
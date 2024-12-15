package org.example.ui

import org.example.Universe
import java.awt.BorderLayout
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JToolBar

class MainFrame: JFrame() {

    var universe: Universe =  Universe.defaultSart()

    val universeFrame = UniverseFrame(universe)

    init {
        title = "Game of Life"
        defaultCloseOperation = EXIT_ON_CLOSE
        isVisible = true

        contentPane = JPanel(BorderLayout()).apply {
            add("North", JToolBar().apply {
                add(JButton("Reset").apply {
                    addActionListener{
                    universeFrame.actionPerformed(object : java.awt.event.ActionEvent(this, 0, "Reset"){})
                        }
                })
                add(JButton("Step").apply {
                    addActionListener {
                            universeFrame.actionPerformed(object : java.awt.event.ActionEvent(this, 0, "Step"){})
                    }
                })
                add(JButton("Play").apply {
                    addActionListener {
                        universeFrame.actionPerformed(object : java.awt.event.ActionEvent(this, 0, "Play"){})
                    }
                })
                add(JButton("Stop").apply {
                    addActionListener {
                        universeFrame.actionPerformed(object : java.awt.event.ActionEvent(this, 0, "Stop"){})
                    }
                })
                add(JButton("Clear").apply {
                    addActionListener {
                        universeFrame.actionPerformed(object : java.awt.event.ActionEvent(this, 0, "Clear"){})
                    }
                })
                add(JButton("Random").apply {
                    addActionListener {
                        universeFrame.actionPerformed(object : java.awt.event.ActionEvent(this, 0, "Random"){})
                    }
                })
                isOpaque = true
            })
            add("Center", universeFrame)
        }
        pack()
    }

}
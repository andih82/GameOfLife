package org.example.ui

import org.example.Options.CELL_SIZE
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D

fun Graphics.clearCell(x: Int, y: Int) {
    this.clearRect(
        x * CELL_SIZE,
        y * CELL_SIZE,
        CELL_SIZE,
        CELL_SIZE
    )
}

fun Graphics.fillCell(x: Int, y: Int, color: Color = Color.BLACK) {
    if (color == Color.BLACK) {
        this.fillRect(
            x * CELL_SIZE,
            y * CELL_SIZE,
            CELL_SIZE,
            CELL_SIZE
        )
    } else {
        val g2 = this as Graphics2D
        g2.color = color
        g2.fillRect(
            x * CELL_SIZE,
            y * CELL_SIZE,
            CELL_SIZE,
            CELL_SIZE
        )
    }
}

fun Graphics.drawCell(x: Int, y: Int, color: Color = Color.BLACK) {
    if (color == Color.BLACK) {
        this.drawRect(
            x * CELL_SIZE,
            y * CELL_SIZE,
            CELL_SIZE,
            CELL_SIZE
        )
    } else {
        val g2 = this as Graphics2D
        g2.color = Color.RED
        g2.drawRect(
            x * CELL_SIZE,
            y * CELL_SIZE,
            CELL_SIZE,
            CELL_SIZE
        )
    }
}
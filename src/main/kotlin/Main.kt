package org.example

import org.example.ui.MainFrame
import kotlinx.coroutines.*
import kotlinx.coroutines.swing.Swing

fun main() {
    CoroutineScope(Dispatchers.Swing).launch {
            MainFrame()
        }
}
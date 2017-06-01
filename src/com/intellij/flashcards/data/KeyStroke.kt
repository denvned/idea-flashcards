package com.intellij.flashcards.data

data class KeyStroke @JvmOverloads constructor(
        var keyChar: Int = 0,
        var keyCode: Int = 0,
        var modifiers: Int = 0,
        var onKeyRelease: Boolean = false)

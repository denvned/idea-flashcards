package org.intellij.flashcards.data

data class Shortcut @JvmOverloads constructor(
        var firstKeyStroke: KeyStroke? = null,
        var secondKeyStroke: KeyStroke? = null)

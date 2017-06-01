package com.intellij.flashcards

import java.awt.Color
import java.awt.event.KeyEvent

enum class RecallGrade(
        val text: String,
        val recallProbability: Double,
        val color: Color,
        val mnemonic: Int) {

    FAIL("Fail", 0.2, Color.RED, KeyEvent.VK_F),
    HARD("Hard", 0.5, Color.ORANGE, KeyEvent.VK_H),
    GOOD("Good", 0.8, Color.YELLOW, KeyEvent.VK_G),
    EASY("Easy", 0.9, Color.GREEN, KeyEvent.VK_A)
}

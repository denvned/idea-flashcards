package com.intellij.flashcards

import java.awt.Color

enum class RecallGrade(
        val text: String,
        val recallProbability: Double,
        val color: Color) {

    FAIL("Fail", 0.2, Color.RED),
    HARD("Hard", 0.5, Color.ORANGE),
    GOOD("Good", 0.8, Color.YELLOW),
    EASY("Easy", 0.9, Color.GREEN)
}

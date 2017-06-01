package com.intellij.flashcards

import java.awt.Color

enum class RecallGrade(val text: String, val color: Color) {
    FAIL("Fail", Color.RED),
    HARD("Hard", Color.ORANGE),
    GOOD("Good", Color.YELLOW),
    EASY("Easy", Color.GREEN)
}
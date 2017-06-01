package com.intellij.flashcards.data

data class Database(
        var learnQueue: MutableList<Flashcard> = mutableListOf(),
        var reviewResults: MutableList<ReviewResult> = mutableListOf())

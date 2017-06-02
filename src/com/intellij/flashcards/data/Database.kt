package com.intellij.flashcards.data

data class Database(
        var learnQueue: MutableList<String> = mutableListOf(),
        var reviewResults: MutableMap<Flashcard, MutableList<ReviewResult>> = hashMapOf())

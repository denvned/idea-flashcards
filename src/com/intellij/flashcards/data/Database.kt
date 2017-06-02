package com.intellij.flashcards.data

import com.intellij.util.xmlb.annotations.AbstractCollection

data class Database(
        var learnQueue: MutableList<String> = mutableListOf(),
        var reviewResults: MutableMap<Flashcard, MutableList<ReviewResult>> = hashMapOf(),
        @get:AbstractCollection(sortOrderedSet = false) var ignoredActions: MutableSet<String> = mutableSetOf<String>())

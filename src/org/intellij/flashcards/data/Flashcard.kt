package org.intellij.flashcards.data

import com.intellij.util.xmlb.annotations.AbstractCollection

data class Flashcard @JvmOverloads constructor(
        var actionId: String? = null,
        @get:AbstractCollection(sortOrderedSet = false) var shortcuts: MutableSet<Shortcut> = mutableSetOf())

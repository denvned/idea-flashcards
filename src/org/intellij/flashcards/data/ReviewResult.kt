package org.intellij.flashcards.data

data class ReviewResult @JvmOverloads constructor(
        var date: Long = 0L,
        var recallGrade: RecallGrade? = null,
        var nextReviewDate: Long = 0L)

package com.intellij.flashcards

import com.intellij.flashcards.data.Database
import com.intellij.flashcards.data.Flashcard
import com.intellij.flashcards.data.RecallGrade
import com.intellij.flashcards.data.ReviewResult
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.diagnostic.logger
import java.util.*
import kotlin.collections.ArrayList

@State(name = "Flashcards", storages = arrayOf(Storage("flashcards.xml")), reloadable = false)
class Flashcards : PersistentStateComponent<Database> {
    override fun loadState(state: Database) {
        learnQueue -= state.learnQueue
        learnQueue += state.learnQueue

        state.reviewResults.forEach { reviewResult ->
            reviewResult.card?.let {
                reviewResults.getOrPut(it) { mutableListOf() } += reviewResult
            }
        }

        reviewQueue += reviewResults.keys
        learnQueue -= reviewResults.keys
    }

    override fun getState() = Database(learnQueue.toMutableList(), reviewResults.values.flatten().toMutableList())

    private val reviewResults: MutableMap<Flashcard, MutableList<ReviewResult>> = HashMap()
    private val learnQueue = ActionManager.getInstance().run {
        getActionIds("").map { getAction(it).toCard() }.filterTo(linkedSetOf()) { it.shortcuts.isNotEmpty() }
    }
    private val reviewQueue = TreeSet<Flashcard>(compareBy {
        it.lastReview?.nextReviewDate ?: 0L
    })

    tailrec fun getNextCardToReview(): Flashcard? {
        val card = reviewQueue.firstOrNull()?.takeIf { it.shouldReview } ?: learnQueue.firstOrNull() ?: return null
        val actualCard = card.actualCard
        val isObsolete = card != actualCard
        if (isObsolete) {
            learnQueue -= card
            reviewQueue -= card
            if (actualCard != null && actualCard !in reviewQueue) {
                learnQueue += actualCard
            }
            return getNextCardToReview()
        }
        return card
    }

    private val Flashcard.lastReview get() = reviewResults[this]?.lastOrNull()
    private val Flashcard.lastReviewDate get() = lastReview?.date
    private val Flashcard.nextReviewDate get() = lastReview?.nextReviewDate
    private val Flashcard.shouldReview get() = nextReviewDate?.compareTo(Date().time) ?: 0 <= 0

    fun calculateNextReviewDate(card: Flashcard, recallGrade: RecallGrade): Long {
        val now = Date().time
        val intervalSinceLastReview = card.lastReviewDate?.let { now - it } ?: INITIAL_INTERVAL
        return now + calculateNextInterval(intervalSinceLastReview, recallGrade)
    }

    private fun calculateNextInterval(interval: Long, recallGrade: RecallGrade): Long {
        val retention = Math.log1p(interval.toDouble() / MILLIS_PER_DAY) / Math.log(1 / recallGrade.recallProbability)
        val days = 1 / Math.pow(TARGET_PROBABILITY, retention) - 1
        val millis = days * MILLIS_PER_DAY
        return millis.toLong()
    }

    fun addReviewResult(card: Flashcard, recallGrade: RecallGrade, nextReviewDate: Long) {
        learnQueue -= card
        reviewQueue -= card
        reviewResults.getOrPut(card) { ArrayList() } += ReviewResult(card, Date().time, recallGrade, nextReviewDate)
        reviewQueue += card
    }

    fun getCurrentLearnProgress(): String {
        return "42%"
    }

    companion object {
        private val LOG = logger<Flashcards>()
        private const val TARGET_PROBABILITY = 0.7
        private const val MILLIS_PER_DAY = 24 * 60 * 60 * 1000L
        private const val INITIAL_INTERVAL = MILLIS_PER_DAY / 12
    }
}

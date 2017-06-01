package com.intellij.flashcards

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.KeyboardShortcut
import com.intellij.openapi.components.ApplicationComponent
import com.intellij.openapi.diagnostic.Logger
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

class FlashcardsComponent : ApplicationComponent {
    private val LOG = Logger.getInstance(FlashcardsComponent::class.java)

    private val actions = ArrayList<AnAction>()
    private val reviewResults = HashMap<Flashcard, MutableList<ReviewResult>>()
    private val learnQueue = mutableSetOf<Flashcard>()
    private val reviewQueue = TreeSet<Flashcard>(compareBy {
        it.lastReview?.nextReviewDate ?: LocalDateTime.MIN
    })

    override fun getComponentName() = FlashcardsComponent::class.simpleName!!

    override fun initComponent() {
        initActions()
    }

    private fun initActions() {
        actions += ActionManager.getInstance().run {
            getActionIds("").map { getAction(it) }.filter {
                it.shortcutSet.shortcuts.any { it is KeyboardShortcut }
            }
        }

        learnQueue += actions.map { it.toCard() }
    }

    private val INITIAL_INTERVAL = Duration.ofHours(2)
    private val TARGET_PROBABILITY = 0.7
    private val MILLIS_PER_DAY = 24 * 60 * 60 * 1000.0

    class ReviewResult(
            val card: Flashcard,
            val date: LocalDateTime,
            val recallGrade: RecallGrade,
            val nextReviewDate: LocalDateTime)

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

    private fun AnAction.toCard(): Flashcard {
        val shortcuts = shortcutSet.shortcuts.filterIsInstance<KeyboardShortcut>()
        return Flashcard(
                ActionManager.getInstance().getId(this),
                shortcuts.map { it.firstKeyStroke to it.secondKeyStroke }.toSet())
    }

    private val Flashcard.actualCard get() = action?.toCard()
    private val Flashcard.lastReview get() = reviewResults[this]?.lastOrNull()
    private val Flashcard.lastReviewDate get() = lastReview?.date
    private val Flashcard.nextReviewDate get() = lastReview?.nextReviewDate
    private val Flashcard.shouldReview get() = nextReviewDate?.compareTo(LocalDateTime.now()) ?: 0 <= 0

    fun calculateNextReviewDate(card: Flashcard, recallGrade: RecallGrade): LocalDateTime {
        val now = LocalDateTime.now()
        val intervalSinceLastReview = card.lastReviewDate?.let { Duration.between(it, now) } ?: INITIAL_INTERVAL
        return now + calculateNextInterval(intervalSinceLastReview, recallGrade)
    }

    private fun calculateNextInterval(interval: Duration, recallGrade: RecallGrade): Duration {
        val retention = Math.log1p(interval.toMillis() / MILLIS_PER_DAY) / Math.log(1 / recallGrade.recallProbability)
        val days = 1 / Math.pow(TARGET_PROBABILITY, retention) - 1
        val millis = days * MILLIS_PER_DAY
        return Duration.ofMillis(millis.toLong())
    }

    fun addReviewResult(card: Flashcard, recallGrade: RecallGrade, nextReviewDate: LocalDateTime) {
        learnQueue -= card
        reviewQueue -= card
        val actionReviewResults = reviewResults.getOrPut(card) { ArrayList() }
        actionReviewResults += ReviewResult(card, LocalDateTime.now(), recallGrade, nextReviewDate)
        reviewQueue += card
    }

    override fun disposeComponent() {
    }
}

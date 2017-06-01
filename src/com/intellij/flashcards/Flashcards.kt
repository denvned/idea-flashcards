package com.intellij.flashcards

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.KeyboardShortcut
import com.intellij.openapi.components.ApplicationComponent
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

class Flashcards : ApplicationComponent {
    private val actions = ArrayList<AnAction>()
    private val reviewResults = HashMap<String, MutableList<ReviewResult>>()
    private val reviewQueue = TreeSet<String>(compareBy {
        reviewResults[it]?.last()?.nextReviewDate ?: LocalDateTime.MIN
    })

    override fun getComponentName() = "Flashcards"

    override fun initComponent() {
        initActions()
    }

    private fun initActions() {
        actions += ActionManager.getInstance().run {
            getActionIds("").map { getAction(it) }.filter {
                it.shortcutSet.shortcuts.any { it is KeyboardShortcut }
            }
        }

        reviewQueue += actions.map { ActionManager.getInstance().getId(it) }
    }

    class ReviewResult(
            val action: AnAction,
            val date: LocalDateTime,
            val recallGrade: RecallGrade,
            val nextReviewDate: LocalDateTime)

    fun getNextReviewAction(): AnAction {
        return actions[Random().nextInt(actions.size)]
    }

    fun getNextReviewDate(action: AnAction, recallGrade: RecallGrade): LocalDateTime {
        TODO()
    }

    fun addReviewResult(action: AnAction, recallGrade: RecallGrade, nextReviewDate: LocalDateTime) {
        val actionId = ActionManager.getInstance().getId(action)
        val actionReviewResults = reviewResults.getOrPut(actionId) { ArrayList() }
        actionReviewResults += ReviewResult(action, LocalDateTime.now(), recallGrade, nextReviewDate)
    }

    override fun disposeComponent() {
    }

    fun getCurrentLearnProgress(): String {
        return "42%"
    }
}

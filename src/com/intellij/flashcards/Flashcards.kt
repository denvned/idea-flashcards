package com.intellij.flashcards

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.KeyboardShortcut
import com.intellij.openapi.components.ApplicationComponent
import java.time.LocalDateTime
import java.util.*

class Flashcards : ApplicationComponent {
    private val actions = ArrayList<AnAction>()

    override fun getComponentName() = "Flashcards"

    override fun initComponent() {
        initActions()
    }

    private fun initActions() {
        actions += ActionManager.getInstance().run {
            getActionIds("").map { getAction(it) }.filter { it.shortcutSet.shortcuts.any { it is KeyboardShortcut } }
        }
    }

    fun getNextReviewAction(): AnAction {
        return actions[Random().nextInt(actions.size)]
    }

    fun addReview(recallGrade: RecallGrade, nextReviewTime: LocalDateTime) {

    }

    override fun disposeComponent() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
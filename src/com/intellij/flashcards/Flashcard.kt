package com.intellij.flashcards

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import javax.swing.KeyStroke

data class Flashcard(val actionId: String, val shortcuts: Set<Pair<KeyStroke, KeyStroke?>>) {
    val action get(): AnAction? = ActionManager.getInstance().getAction(actionId)
}

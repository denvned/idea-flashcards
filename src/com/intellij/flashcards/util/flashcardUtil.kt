package com.intellij.flashcards.util

import com.intellij.flashcards.data.Flashcard
import com.intellij.flashcards.data.KeyStroke
import com.intellij.flashcards.data.Shortcut
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.KeyboardShortcut

val Flashcard.action get(): AnAction? = actionId?.let { ActionManager.getInstance().getAction(it) }

fun AnAction.toCard(): Flashcard {
    val shortcuts = shortcutSet.shortcuts.filterIsInstance<KeyboardShortcut>()
    return Flashcard(
            ActionManager.getInstance().getId(this),
            shortcuts.map { it.toShortcut() }.toMutableSet())
}

val Flashcard.actualCard get() = action?.toCard()

fun KeyboardShortcut.toShortcut() = Shortcut(firstKeyStroke.toKeyStroke(), secondKeyStroke?.toKeyStroke())

fun javax.swing.KeyStroke.toKeyStroke() = KeyStroke(keyChar.toInt(), keyCode, modifiers, isOnKeyRelease)

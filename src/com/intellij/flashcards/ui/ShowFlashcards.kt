package com.intellij.flashcards.ui

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys

class ShowFlashcards : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getData(CommonDataKeys.PROJECT)
        project?.let { FlashcardToolWindow.getInstance(it).show() }
    }

    override fun update(e: AnActionEvent) {
        val project = e.getData(CommonDataKeys.PROJECT)
        e.presentation.isEnabled = project != null
    }
}

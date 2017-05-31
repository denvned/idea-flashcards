package com.intellij.flashcards.ui

import com.intellij.flashcards.Flashcards
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.LabeledIcon
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.layout.panel

class FlashcardToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        showCard(toolWindow)
    }

    private fun showCard(toolWindow: ToolWindow) {
        val contentFactory = ContentFactory.SERVICE.getInstance()

        val flashcards = ApplicationManager.getApplication().getComponent("Flashcards") as Flashcards
        val action = flashcards.getNextReviewAction()
        val panel = panel {
            row {
                label("Action:")
            }
            row {
                LabeledIcon(action.templatePresentation.icon, null, null)
                action.templatePresentation.text?.let { label(it) }
            }
            row {
                label(action.templatePresentation.description)
            }
            row {
                button("Show Answer") {
                    showCard(toolWindow)
                }
            }
        }

        val content = contentFactory.createContent(panel, "Flashcards", false)
        toolWindow.contentManager.removeAllContents(false)
        toolWindow.contentManager.addContent(content)
    }
}

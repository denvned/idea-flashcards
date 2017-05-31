package com.intellij.flashcards.ui

import com.intellij.flashcards.Flashcards
import com.intellij.flashcards.RecallGrade
import com.intellij.flashcards.keymap.SubKeymapUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.KeyboardShortcut
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.components.Label
import com.intellij.ui.components.htmlComponent
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.layout.LayoutBuilder
import com.intellij.ui.layout.panel
import java.awt.Font
import java.awt.Rectangle
import javax.swing.JButton

class FlashcardToolWindow(val toolWindow: ToolWindow) {
    private val contentFactory = ContentFactory.SERVICE.getInstance()
    private val LEFT_MARGIN = 10

    fun showNextQuestion() {
        val flashcards = ApplicationManager.getApplication().getComponent("Flashcards") as Flashcards
        val action = flashcards.getNextReviewAction()
        showQuestion(action)
    }

    fun showCard(init: LayoutBuilder.() -> Unit) {
        val panel = panel {
            init()
        }

        val content = contentFactory.createContent(panel, "", false)
        toolWindow.contentManager.removeAllContents(false)
        toolWindow.contentManager.addContent(content)
    }

    private fun showQuestion(action: AnAction) {
        showCard {
            showAction(action)
            row {
                label(gapLeft = LEFT_MARGIN, text = "")
                JButton("Show Answer").apply {
                    addActionListener {
                        showAnswer(action)
                    }
                }()
            }
        }
    }

    private fun showAnswer(action: AnAction) {
        showCard {
            showAction(action)

            row {
                label(gapLeft = LEFT_MARGIN, text = "Answer:")
            }
            action.shortcutSet.shortcuts.filterIsInstance<KeyboardShortcut>().forEach {
                row {
                    label(gapLeft = LEFT_MARGIN, text = (""))

                    val button = JButton(arrayOf(it.firstKeyStroke, it.secondKeyStroke)
                            .filterNotNull()
                            .map { SubKeymapUtil.getKeyStrokeTextSub(it) }
                            .joinToString())()
                            .apply { enabled = false }
                }
            }
            row {
                label(gapLeft = LEFT_MARGIN, text = ("How hard was it to recall?"), bold = true)
            }
            row {

                label(gapLeft = LEFT_MARGIN, text = "")
                RecallGrade.values().forEach {
                    JButton(it.text).apply {
                        addActionListener {
                            showNextQuestion()
                        }
                    }()
                }
            }
        }
    }

    private fun LayoutBuilder.showAction(action: AnAction) {
        row {
            label(gapLeft = LEFT_MARGIN, text = (" "))
        }

        row {
            label(gapLeft = LEFT_MARGIN, text = ("Do you remember the shortcut for the following action?"))
        }
        row {
            label(gapLeft = LEFT_MARGIN, text = "")
            Label(action.templatePresentation.text ?: "").apply {
                icon = action.templatePresentation.icon
                font = Font("Verdana", Font.BOLD, 12)
                bounds = Rectangle(0, 0, 100, 100)
            }()
        }
        row {
            label(gapLeft = LEFT_MARGIN, text = "Description: ${action.templatePresentation.description}")
        }
    }
}

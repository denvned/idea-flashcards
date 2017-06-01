package com.intellij.flashcards.ui

import com.intellij.flashcards.Flashcards
import com.intellij.flashcards.RecallGrade
import com.intellij.flashcards.keymap.SubKeymapUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.KeyboardShortcut
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowAnchor
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.components.Label
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.layout.LayoutBuilder
import com.intellij.ui.layout.panel
import java.awt.Font
import java.awt.Rectangle
import javax.swing.JButton

class FlashcardToolWindow(val project: Project, val toolWindowManager: ToolWindowManager): ProjectComponent {
    private lateinit var toolWindow: ToolWindow

    override fun getComponentName(): String {
        return FlashcardToolWindow::class.simpleName!!
    }

    override fun initComponent() {
    }

    override fun disposeComponent() {
    }

    override fun projectOpened() {
        toolWindow = toolWindowManager.registerToolWindow(ID, false, ToolWindowAnchor.RIGHT, project, true, true)
        toolWindow.icon = IconLoader.getIcon("/img/hotkeys_16.png")
        showNextQuestion()
    }

    override fun projectClosed() {
        toolWindowManager.unregisterToolWindow(ID)
    }

    fun show() {
        toolWindow.show(null)
    }

    fun showNextQuestion() {
        val flashcards = ApplicationManager.getApplication().getComponent("Flashcards") as Flashcards
        val action = flashcards.getNextReviewAction()
        showQuestion(action)
    }

    fun showCard(init: LayoutBuilder.() -> Unit) {
        val panel = panel {
            init()
        }

        val content = ContentFactory.SERVICE.getInstance().createContent(panel, "", false)
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

    companion object {
        private const val ID = "Flashcards"
        private const val LEFT_MARGIN = 10

        fun getInstance(project: Project): FlashcardToolWindow = project.getComponent(FlashcardToolWindow::class.java)
    }
}

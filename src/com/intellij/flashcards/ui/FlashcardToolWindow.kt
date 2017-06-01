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
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JSeparator
import javax.swing.border.CompoundBorder
import javax.swing.border.EmptyBorder

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
        toolWindow.activate(null)
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
                label(gapLeft = LEFT_MARGIN, text = " ")
            }
            row {
                label(gapLeft = 12 * LEFT_MARGIN, text = "")
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
//            row {
//                label(gapLeft = LEFT_MARGIN, text = " ")
//            }

//            row {
//                label(gapLeft = LEFT_MARGIN, text = "")
//                Label("Answer:").apply {
//                    font = Font("Verdana", Font.PLAIN, 15)
//                }()
//            }
            row { label(gapLeft = LEFT_MARGIN, text = " ") }
            action.shortcutSet.shortcuts.filterIsInstance<KeyboardShortcut>().forEach {
                row {

                    label(gapLeft = 5 * LEFT_MARGIN, text = " ")
                    Label(arrayOf(it.firstKeyStroke, it.secondKeyStroke)
                            .filterNotNull()
                            .map { SubKeymapUtil.getKeyStrokeTextSub(it) }
                            .joinToString()).apply {
                        font = Font("Verdana", Font.PLAIN, 40)
                        border = CompoundBorder(BorderFactory.createRaisedSoftBevelBorder(), EmptyBorder(0,10,0,10))
                        background = Color.WHITE
                        isOpaque = true


                    }()
                }
            }
            row { label(gapLeft = LEFT_MARGIN, text = " ") }
            row {
                label(gapLeft = LEFT_MARGIN, text = "")
                JSeparator(JSeparator.HORIZONTAL).apply {
                    border = BorderFactory.createLineBorder(Color.GRAY)
                    preferredSize = Dimension(420, 1)
                }()
            }
            row {
                label(gapLeft = LEFT_MARGIN, text = "")
                Label("How hard was it to recall?").apply {
                    font = Font("Verdana", Font.PLAIN, 15)
                }()
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
            label(gapLeft = LEFT_MARGIN, text = "")
            Label("Do you remember the shortcut for the following action?").apply {
                font = Font("Verdana", Font.PLAIN, 15)
            }()
        }
        row {
            label(gapLeft = LEFT_MARGIN, text = (" "))
        }
        row {
            label(gapLeft = LEFT_MARGIN, text = "")
            Label(action.templatePresentation.text ?: "").apply {
                icon = action.templatePresentation.icon
                font = Font("Verdana", Font.BOLD, 18)
            }()
        }
        row {
            label(gapLeft = LEFT_MARGIN, text = "")
            action.templatePresentation.description?.let {
                Label(it).apply {
                    font = Font("Verdana", Font.BOLD, 14)
                }()
            }
        }
        row {
            label(gapLeft = LEFT_MARGIN, text = (" "))
        }
        row {
            label(gapLeft = LEFT_MARGIN, text = "")
            JSeparator(JSeparator.HORIZONTAL).apply {
                border = BorderFactory.createLineBorder(Color.GRAY)
                preferredSize = Dimension(420, 1)
            }()
        }

    }

    companion object {
        private const val ID = "Flashcards"
        private const val LEFT_MARGIN = 10

        fun getInstance(project: Project): FlashcardToolWindow = project.getComponent(FlashcardToolWindow::class.java)
    }
}

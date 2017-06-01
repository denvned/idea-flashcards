package com.intellij.flashcards.ui

import com.intellij.flashcards.Flashcards
import com.intellij.flashcards.action
import com.intellij.flashcards.data.Flashcard
import com.intellij.flashcards.data.RecallGrade
import com.intellij.flashcards.keymap.SubKeymapUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowAnchor
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.components.Label
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.layout.LayoutBuilder
import com.intellij.ui.layout.panel
import com.intellij.util.ui.UIUtil
import java.awt.*
import java.awt.event.KeyEvent
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.border.CompoundBorder
import javax.swing.border.EmptyBorder


class FlashcardToolWindow(val project: Project, val toolWindowManager: ToolWindowManager) : ProjectComponent {
    private lateinit var toolWindow: ToolWindow

    private val flashcards = ServiceManager.getService(Flashcards::class.java)

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
        val card = flashcards.getNextCardToReview()
        card?.let { showQuestion(it) } ?: showNoMoreCards()
    }

    private fun showNoMoreCards() = showContent {
        row {
            label(gapLeft = LEFT_MARGIN, text = "Congratulations, you review all the cards for now!")
        }
    }

    fun showContent(init: LayoutBuilder.() -> Unit) {
        val panel = panel {
            init()
        }

        val content = ContentFactory.SERVICE.getInstance().createContent(panel, "", false)
        toolWindow.contentManager.removeAllContents(false)
        toolWindow.contentManager.addContent(content)
    }

    private fun showQuestion(card: Flashcard) = showContent {
        showAction(card.action)
        row {
            label(gapLeft = LEFT_MARGIN, text = " ")
        }
        row {
            label(gapLeft = 12 * LEFT_MARGIN, text = "")
            JButton("Show Answer").apply {
                //border = CompoundBorder(BorderFactory.createLineBorder(Color.BLACK, 2), EmptyBorder(0, 10, 0, 10))
                border = CompoundBorder(BorderFactory.createLineBorder(Color.BLACK, 1), CompoundBorder(BorderFactory.createRaisedSoftBevelBorder(), EmptyBorder(0, 10, 0, 10)))
                isOpaque = true
                isBorderPainted = false
                mnemonic = KeyEvent.VK_S
                addActionListener {
                    showAnswer(card)
                }
            }()
        }
        showProgress()
    }

    private fun showAnswer(card: Flashcard) = showContent {
        showAction(card.action)
        row { label(gapLeft = LEFT_MARGIN, text = " ") }
        row { label(gapLeft = LEFT_MARGIN, text = " ") }
        card.shortcuts.forEach {
            row {
                label(gapLeft = 5 * LEFT_MARGIN, text = " ")
                Label(arrayOf(it.firstKeyStroke, it.secondKeyStroke)
                        .filterNotNull()
                        .map { SubKeymapUtil.getKeyStrokeTextSub(it) }
                        .joinToString()).apply {
                    val buttonFont = UIUtil.getButtonFont()
                    font = buttonFont.deriveFont(buttonFont.style, 2.0f * buttonFont.size)
                    border = CompoundBorder(BorderFactory.createRaisedSoftBevelBorder(), EmptyBorder(0, 10, 0, 10))
                    background = Color.WHITE
                    isOpaque = true
                }()
            }
        }
        row { label(gapLeft = LEFT_MARGIN, text = " ") }
//            row { label(gapLeft = LEFT_MARGIN, text = " ") }
//            row {
//                label(gapLeft = LEFT_MARGIN, text = "")
//                JSeparator(JSeparator.HORIZONTAL).apply {
//                    border = BorderFactory.createLineBorder(Color.GRAY)
//                    preferredSize = Dimension(420, 1)
//                }()
//            }
        row { label(gapLeft = LEFT_MARGIN, text = " ") }
        row {
            label(gapLeft = LEFT_MARGIN, text = "")
            Label("How hard was it to recall?").apply {
                font = Font("Verdana", Font.PLAIN, 15)
            }()
        }
        row {

            label(gapLeft = LEFT_MARGIN, text = "")
            RecallGrade.values().forEach { recallGrade ->
                val nextReviewDate = flashcards.calculateNextReviewDate(card, recallGrade)

                JButton(recallGrade.text).apply {
                    border = CompoundBorder(BorderFactory.createLineBorder(recallGrade.color, 1), CompoundBorder(BorderFactory.createRaisedSoftBevelBorder(), EmptyBorder(0, 10, 0, 10)))
                    //background = it.color
                    isOpaque = true
                    //isBorderPainted = false
                    mnemonic = recallGrade.mnemonic
                    addActionListener {
                        flashcards.addReviewResult(card, recallGrade, nextReviewDate)
                        showNextQuestion()
                    }
                }()
            }
        }

//            row {
//
//                JPanel(BorderLayout()).apply {
//                    preferredSize = Dimension(600, 400)
//                    add(
//                            Label("Current learn progress ${flashcards.getCurrentLearnProgress()}", Label.RIGHT).apply {
//                                font = Font("Verdana", Font.PLAIN, 15)
//                            }, BorderLayout.PAGE_END)
//
//                }()
//            }
        showProgress()
    }

    private fun LayoutBuilder.showProgress() {

        row {

            JPanel(BorderLayout()).apply {
                preferredSize = Dimension(800, 800)
                add(
                        Label("Current learn progress ${flashcards.getCurrentLearnProgress()}", Label.RIGHT).apply {
                            font = Font("Verdana", Font.PLAIN, 15)
                        }, BorderLayout.PAGE_END)

            }()
        }

    }

    private fun LayoutBuilder.showAction(action: AnAction?) {
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
            Label(action?.templatePresentation?.text ?: "").apply {
                icon = action?.templatePresentation?.icon
                font = Font("Verdana", Font.BOLD, 18)
            }()
        }
        row {
            label(gapLeft = LEFT_MARGIN, text = "")
            action?.templatePresentation?.description?.let {
                Label(it).apply {
                    font = Font("Verdana", Font.BOLD, 14)
                }()
            }
        }
    }

    companion object {
        private const val ID = "Flashcards"
        private const val LEFT_MARGIN = 10

        fun getInstance(project: Project): FlashcardToolWindow = project.getComponent(FlashcardToolWindow::class.java)
    }
}

package com.intellij.flashcards.ui

import com.intellij.flashcards.storage.FlashcardStorage
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.MessageType
import com.intellij.openapi.ui.popup.Balloon
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.wm.WindowManager
import com.intellij.ui.awt.RelativePoint
import java.awt.event.ActionEvent
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel

class ShowDialogBox : AnAction() {


    override fun actionPerformed(actionEvent: AnActionEvent) {
        val dialog = MyDialog().initialize()
        with(dialog) {
            showAndGet()        }


        val statusBar = WindowManager.getInstance()
                .getStatusBar(DataKeys.PROJECT.getData(actionEvent.getDataContext()))


        val currentWarning = FlashcardStorage.getInstance().myState.value
        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder("Current storage value ${currentWarning}", MessageType.INFO, null)
                .setFadeoutTime(7500)
                .createBalloon()
                .show(RelativePoint.getCenterOf(statusBar.getComponent()),
                        Balloon.Position.atRight)
        FlashcardStorage.getInstance().myState.value = "Some storage value"


    }

    class MyDialog : DialogWrapper(null, true) {
        val dialogPanel = JPanel()

        fun initialize(): MyDialog {
            isModal = true
            title = ("Do you remember?")
            setOKButtonText("Remember me!")
            horizontalStretch = 1.33f
            verticalStretch = 1.25f

            val turnCardOverButton = JButton("Turn card").apply {
                addActionListener({})
            }

            dialogPanel.add(turnCardOverButton)
            init()
            return this
        }

        override fun createCenterPanel(): JComponent? = dialogPanel
    }


}


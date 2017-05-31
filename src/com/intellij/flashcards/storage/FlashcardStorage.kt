package com.intellij.flashcards.storage

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.ServiceManager

@State(name = "FlashcardStorage", storages = arrayOf(Storage("storageFlashcards.xml")))
class FlashcardStorage : PersistentStateComponent<FlashcardStorage.State> {

    companion object {
        fun getInstance(): FlashcardStorage = ServiceManager.getService(FlashcardStorage::class.java)
    }

    class State {
        var value: String? = null
    }

    var myState: State = State()

    override fun getState(): State? {
        return myState
    }

    override fun loadState(state: State) {
        myState = state
    }
}


package com.intellij.flashcards.storage

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.ServiceManager
import java.time.LocalDateTime

@State(name = "FlashcardStorage", storages = arrayOf(Storage("storageFlashcards.xml")))
class FlashcardStorage : PersistentStateComponent<FlashcardStorage.State> {

    companion object {
        val instance: FlashcardStorage get() = ServiceManager.getService(FlashcardStorage::class.java)
    }

    data class CardReview(val actionId: String, val data: LocalDateTime, val answer: String)

    class State() {
        val actionCardReviews = hashMapOf<String, CardReview>()
    }

    var myState: State = State()

    override fun getState(): State? {
        return myState
    }

    override fun loadState(state: State) {
        myState = state
    }
}

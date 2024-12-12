package org.dreamerslab.rc3.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.util.UUID

data class UiMessage(
    val message: String,
    val args: List<Any> = emptyList(),
    val id: Long = UUID.randomUUID().mostSignificantBits
)

class UiMessageManager {
    private val _message = MutableStateFlow(emptyList<UiMessage>())

    /**
     * A flow emitting the current message to display.
     */
    val message: Flow<UiMessage?> = _message.map { it.firstOrNull() }.distinctUntilChanged()

    fun emitMessage(message: UiMessage) {
        _message.update { it + message }
    }

    fun clearMessage(id: Long) {
        _message.update { messages ->
            messages.filterNot { it.id == id }
        }
    }
}
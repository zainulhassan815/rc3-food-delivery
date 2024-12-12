package org.dreamerslab.rc3.ui.auth.forgotpassword

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.dreamerslab.rc3.data.repository.AuthRepository
import org.dreamerslab.rc3.data.repository.SendPasswordResetEmailFailure
import org.dreamerslab.rc3.utils.UiMessage
import org.dreamerslab.rc3.utils.UiMessageManager
import org.dreamerslab.rc3.utils.form.Form
import org.dreamerslab.rc3.utils.form.FormSubmissionStatus
import org.dreamerslab.rc3.utils.formfields.Email
import javax.inject.Inject

data class ResetPasswordFormState(
    val email: Email = Email(""),
    val submissionStatus: FormSubmissionStatus = FormSubmissionStatus.Initial
) : Form {
    override val inputs = listOf(email)
}

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val messageManager: UiMessageManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val email: String = savedStateHandle["email"] ?: ""

    private val _state: MutableStateFlow<ResetPasswordFormState> =
        MutableStateFlow(
            ResetPasswordFormState(
                email = Email(email)
            )
        )
    val state: StateFlow<ResetPasswordFormState> = _state

    val message = messageManager.message.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = null
    )

    fun clearMessage(id: Long) {
        viewModelScope.launch { messageManager.clearMessage(id) }
    }

    fun updateEmail(email: String) {
        viewModelScope.launch {
            _state.update { it.copy(email = Email(email, false)) }
        }
    }

    fun sendPasswordResetEmail() {
        viewModelScope.launch {
            val formState = _state.value
            if (formState.isNotValid) return@launch

            _state.update { it.copy(submissionStatus = FormSubmissionStatus.InProgress) }

            authRepository.sendPasswordResetEmail(
                email = formState.email.value
            ).onLeft { failure ->
                _state.update { it.copy(submissionStatus = FormSubmissionStatus.Failure) }
                messageManager.emitMessage(failure.mapToUiMessage())
            }.onRight {
                _state.update { it.copy(submissionStatus = FormSubmissionStatus.Success) }
                messageManager.emitMessage(UiMessage("Password email sent successfully."))
            }
        }
    }

    private fun SendPasswordResetEmailFailure.mapToUiMessage(): UiMessage {
        val reason = cause::class.simpleName
        return UiMessage("Failed to send password reset email!" + reason?.let { "Cause = $it" })
    }

}

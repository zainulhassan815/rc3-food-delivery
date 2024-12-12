package org.dreamerslab.rc3.ui.auth.login

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
import org.dreamerslab.rc3.data.repository.LoginWithEmailAndPasswordFailure
import org.dreamerslab.rc3.utils.UiMessage
import org.dreamerslab.rc3.utils.UiMessageManager
import org.dreamerslab.rc3.utils.form.Form
import org.dreamerslab.rc3.utils.form.FormSubmissionStatus
import org.dreamerslab.rc3.utils.formfields.Email
import org.dreamerslab.rc3.utils.formfields.Password
import javax.inject.Inject

data class LoginFormState(
    val email: Email = Email(""),
    val password: Password = Password(""),
    val submissionStatus: FormSubmissionStatus = FormSubmissionStatus.Initial
) : Form {
    override val inputs = listOf(email, password)
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val messageManager: UiMessageManager
) : ViewModel() {

    private val _state: MutableStateFlow<LoginFormState> = MutableStateFlow(LoginFormState())
    val state: StateFlow<LoginFormState> = _state

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

    fun updatePassword(password: String) {
        viewModelScope.launch {
            _state.update { it.copy(password = Password(password, false)) }
        }
    }

    fun login() {
        viewModelScope.launch {
            val formState = _state.value
            if (formState.isNotValid) return@launch

            _state.update { it.copy(submissionStatus = FormSubmissionStatus.InProgress) }

            authRepository.signInWithEmailAndPassword(
                email = formState.email.value,
                password = formState.password.value
            ).onLeft { failure ->
                _state.update { it.copy(submissionStatus = FormSubmissionStatus.Failure) }
                messageManager.emitMessage(failure.mapToUiMessage())
            }.onRight {
                _state.update { it.copy(submissionStatus = FormSubmissionStatus.Success) }
                messageManager.emitMessage(UiMessage("Login Success"))
            }
        }
    }

    private fun LoginWithEmailAndPasswordFailure.mapToUiMessage(): UiMessage = when (this) {
        LoginWithEmailAndPasswordFailure.InvalidCredentials -> UiMessage("Invalid email or password")
        is LoginWithEmailAndPasswordFailure.ErrorOccurred -> {
            val reason = cause::class.simpleName
            UiMessage("Login Failed!" + reason?.let { "Cause = $it" })
        }
    }

}
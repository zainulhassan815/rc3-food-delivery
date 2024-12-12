package org.dreamerslab.rc3.ui.auth.signup

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
import org.dreamerslab.rc3.data.repository.SignupWithEmailAndPasswordFailure
import org.dreamerslab.rc3.utils.UiMessage
import org.dreamerslab.rc3.utils.UiMessageManager
import org.dreamerslab.rc3.utils.form.Form
import org.dreamerslab.rc3.utils.form.FormSubmissionStatus
import org.dreamerslab.rc3.utils.formfields.ConfirmPassword
import org.dreamerslab.rc3.utils.formfields.Email
import org.dreamerslab.rc3.utils.formfields.Password
import javax.inject.Inject

data class SignupFormState(
    val email: Email = Email(""),
    val password: Password = Password(""),
    val confirmPassword: ConfirmPassword = ConfirmPassword("", password),
    val submissionStatus: FormSubmissionStatus = FormSubmissionStatus.Initial
) : Form {
    override val inputs = listOf(email, password)
}

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val messageManager: UiMessageManager
) : ViewModel() {

    private val _state: MutableStateFlow<SignupFormState> = MutableStateFlow(SignupFormState())
    val state: StateFlow<SignupFormState> = _state

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
            _state.update {
                val updatedPassword = Password(password, false)
                it.copy(
                    password = updatedPassword,
                    confirmPassword = ConfirmPassword(
                        value = it.confirmPassword.value,
                        password = updatedPassword,
                        isPure = it.confirmPassword.isPure
                    )
                )
            }
        }
    }

    fun updateConfirmPassword(password: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    confirmPassword = ConfirmPassword(
                        value = password,
                        password = _state.value.password,
                        isPure = false
                    )
                )
            }
        }
    }

    fun signup() {
        viewModelScope.launch {
            val formState = _state.value
            if (formState.isNotValid) return@launch

            _state.update { it.copy(submissionStatus = FormSubmissionStatus.InProgress) }

            authRepository.signupWithEmailAndPassword(
                email = formState.email.value,
                password = formState.password.value
            ).onLeft { failure ->
                _state.update { it.copy(submissionStatus = FormSubmissionStatus.Failure) }
                messageManager.emitMessage(failure.mapToUiMessage())
            }.onRight {
                _state.update { it.copy(submissionStatus = FormSubmissionStatus.Success) }
                messageManager.emitMessage(UiMessage("Signup Success"))
            }
        }
    }

    private fun SignupWithEmailAndPasswordFailure.mapToUiMessage(): UiMessage = when (this) {
        is SignupWithEmailAndPasswordFailure.ErrorOccurred -> {
            val reason = cause::class.simpleName
            UiMessage("Signup Failed!" + reason?.let { "Cause = $it" })
        }

        SignupWithEmailAndPasswordFailure.AccountAlreadyExists -> UiMessage("Account already exists. Do you want to login?")
        SignupWithEmailAndPasswordFailure.InvalidEmail -> UiMessage("Invalid email or password")
        SignupWithEmailAndPasswordFailure.WeakPassword -> UiMessage("Password is weak. Enter a strong password.")
    }

}
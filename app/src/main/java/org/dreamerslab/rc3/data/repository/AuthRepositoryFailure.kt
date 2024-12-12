package org.dreamerslab.rc3.data.repository

sealed interface SignupWithEmailAndPasswordFailure {
    data object InvalidEmail : SignupWithEmailAndPasswordFailure
    data object WeakPassword : SignupWithEmailAndPasswordFailure
    data object AccountAlreadyExists : SignupWithEmailAndPasswordFailure
    data class ErrorOccurred(
        val cause: Throwable
    ) : SignupWithEmailAndPasswordFailure
}

sealed interface LoginWithEmailAndPasswordFailure {
    data object InvalidCredentials : LoginWithEmailAndPasswordFailure
    data class ErrorOccurred(
        val cause: Throwable
    ) : LoginWithEmailAndPasswordFailure
}

sealed interface LoginWithCredentialsFailure {
    data object InvalidUser : LoginWithCredentialsFailure
    data object InvalidCredentials : LoginWithCredentialsFailure
    data class ErrorOccurred(
        val cause: Throwable
    ) : LoginWithCredentialsFailure
}

data class SendPasswordResetEmailFailure(
    val cause: Throwable
)

data class LogoutFailure(
    val cause: Throwable
)

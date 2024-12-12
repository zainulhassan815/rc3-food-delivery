package org.dreamerslab.rc3.data.repository

import arrow.core.Either
import arrow.core.raise.either
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth
) {
    val currentUser: Flow<FirebaseUser?> = callbackFlow {
        val listener = AuthStateListener { trySend(it.currentUser) }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    suspend fun signupWithEmailAndPassword(
        email: String,
        password: String
    ): Either<SignupWithEmailAndPasswordFailure, FirebaseUser> = either {
        try {
            auth
                .createUserWithEmailAndPassword(email, password)
                .await()
                .user!!
        } catch (e: FirebaseAuthException) {
            val failure = when (e) {
                is FirebaseAuthWeakPasswordException -> SignupWithEmailAndPasswordFailure.WeakPassword
                is FirebaseAuthInvalidCredentialsException -> SignupWithEmailAndPasswordFailure.InvalidEmail
                is FirebaseAuthUserCollisionException -> SignupWithEmailAndPasswordFailure.AccountAlreadyExists
                else -> SignupWithEmailAndPasswordFailure.ErrorOccurred(e)
            }
            raise(failure)
        } catch (e: FirebaseException) {
            raise(SignupWithEmailAndPasswordFailure.ErrorOccurred(e))
        }
    }

    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Either<LoginWithEmailAndPasswordFailure, FirebaseUser> = either {
        try {
            auth
                .signInWithEmailAndPassword(email, password)
                .await()
                .user!!
        } catch (e: FirebaseAuthException) {
            val failure = when (e) {
                is FirebaseAuthInvalidCredentialsException -> LoginWithEmailAndPasswordFailure.InvalidCredentials
                else -> LoginWithEmailAndPasswordFailure.ErrorOccurred(e)
            }
            raise(failure)
        } catch (e: FirebaseException) {
            raise(LoginWithEmailAndPasswordFailure.ErrorOccurred(e))
        }
    }

    suspend fun loginWithCredentials(
        credentials: AuthCredential
    ): Either<LoginWithCredentialsFailure, FirebaseUser> = either {
        try {
            auth
                .signInWithCredential(credentials)
                .await()
                .user!!
        } catch (e: FirebaseAuthException) {
            val failure = when (e) {
                is FirebaseAuthInvalidUserException -> LoginWithCredentialsFailure.InvalidUser
                is FirebaseAuthInvalidCredentialsException -> LoginWithCredentialsFailure.InvalidCredentials
                else -> LoginWithCredentialsFailure.ErrorOccurred(e)
            }
            raise(failure)
        } catch (e: FirebaseException) {
            raise(LoginWithCredentialsFailure.ErrorOccurred(e))
        }
    }

    suspend fun sendPasswordResetEmail(
        email: String
    ): Either<SendPasswordResetEmailFailure, Unit> = either {
        try {
            auth.sendPasswordResetEmail(email).await()
        } catch (e: FirebaseException) {
            raise(SendPasswordResetEmailFailure(e))
        }
    }

    fun logout(): Either<LogoutFailure, Unit> = either {
        try {
            auth.signOut()
        } catch (e: FirebaseException) {
            raise(LogoutFailure(e))
        }
    }
}

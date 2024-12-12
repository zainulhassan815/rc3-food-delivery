package org.dreamerslab.rc3

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import org.dreamerslab.rc3.data.repository.AuthRepository
import javax.inject.Inject

sealed interface MainActivityState {
    data object Loading : MainActivityState
    data class Success(
        val firebaseUser: FirebaseUser? = null,
    ) : MainActivityState
}

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    authRepository: AuthRepository
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<MainActivityState> = authRepository
        .currentUser
        .mapLatest {
            MainActivityState.Success(firebaseUser = it)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = MainActivityState.Loading
        )

}
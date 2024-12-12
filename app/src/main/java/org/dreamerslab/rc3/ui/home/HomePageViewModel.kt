package org.dreamerslab.rc3.ui.home

import androidx.collection.intIntMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import org.dreamerslab.rc3.data.models.Restaurant
import org.dreamerslab.rc3.data.repository.AuthRepository
import org.dreamerslab.rc3.data.repository.RestaurantsRepository
import javax.inject.Inject

sealed interface HomePageState {
    data object Loading : HomePageState
    data class Success(
        val user: FirebaseUser?,
        val restaurants: List<Restaurant>,
    ) : HomePageState
}

@HiltViewModel
class HomePageViewModel @Inject constructor(
    authRepository: AuthRepository,
    restaurantsRepository: RestaurantsRepository
) : ViewModel() {

    val state: StateFlow<HomePageState> = combine(
        authRepository.currentUser,
        restaurantsRepository.restaurants
    ) { user, restaurants ->
        HomePageState.Success(
            user = user,
            restaurants = restaurants
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = HomePageState.Loading
    )

}
package org.dreamerslab.rc3.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.dreamerslab.rc3.data.models.Restaurant
import org.dreamerslab.rc3.data.repository.RestaurantsRepository
import javax.inject.Inject

sealed interface SearchScreenState {
    data object Initial : SearchScreenState
    data class Data(
        val restaurants: List<Restaurant>
    ) : SearchScreenState
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val restaurantsRepository: RestaurantsRepository
) : ViewModel() {

    private val _query: MutableStateFlow<String> = MutableStateFlow("")
    val query: StateFlow<String> = _query

    fun updateQuery(value: String) {
        viewModelScope.launch {
            _query.update { value }
        }
    }

    @OptIn(FlowPreview::class)
    val state = combine(
        _query
            .filter { it.isNotBlank() }
            .debounce(300),
        restaurantsRepository.restaurants
    ) { query, restaurants ->
        SearchScreenState.Data(
            restaurants = restaurants.filter {
                it.name.contains(query, true)
            }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = SearchScreenState.Initial
    )
}
package org.dreamerslab.rc3.ui.menu

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.dreamerslab.rc3.data.daos.CartDao
import org.dreamerslab.rc3.data.models.CartItem
import org.dreamerslab.rc3.data.models.MenuItem
import org.dreamerslab.rc3.data.models.toCartItem
import org.dreamerslab.rc3.data.repository.RestaurantsRepository
import javax.inject.Inject

sealed interface MenuPageState {
    data object Loading : MenuPageState
    data class Success(
        val menuItems: List<MenuItem>,
        val cartItems: List<CartItem>
    ) : MenuPageState
}

@HiltViewModel
class MenuPageViewModel @Inject constructor(
    restaurantsRepository: RestaurantsRepository,
    savedStateHandle: SavedStateHandle,
    private val cartDao: CartDao,
) : ViewModel() {

    private val _query: MutableStateFlow<String> = MutableStateFlow("")
    val query: StateFlow<String> = _query

    fun updateQuery(value: String) {
        viewModelScope.launch {
            _query.update { value }
        }
    }

    private val restaurantId: String = savedStateHandle["id"] ?: ""
    private val menuItems: Flow<List<MenuItem>> = restaurantsRepository.getMenu(restaurantId)

    val state: StateFlow<MenuPageState> = combine(
        _query,
        menuItems,
        cartDao.getAll()
    ) { query, items, cartItems ->
        MenuPageState.Success(
            menuItems = items.filter { it.name.contains(query, true) },
            cartItems = cartItems
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MenuPageState.Loading
    )

    fun addToCart(item: MenuItem) {
        viewModelScope.launch {
            val state = state.value as? MenuPageState.Success ?: return@launch
            val cartItem = state.cartItems.find { it.menuItemId == item.id }

            if (cartItem != null) {
                val newItem = cartItem.copy(
                    quantity = cartItem.quantity.inc(),
                )
                cartDao.updateItem(newItem)
            } else {
                cartDao.insertItem(item.toCartItem())
            }
        }
    }

}
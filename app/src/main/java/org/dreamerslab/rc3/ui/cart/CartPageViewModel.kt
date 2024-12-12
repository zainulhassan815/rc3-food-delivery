package org.dreamerslab.rc3.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.dreamerslab.rc3.data.daos.CartDao
import org.dreamerslab.rc3.data.models.CartItem
import javax.inject.Inject

data class CartPageState(
    val items: List<CartItem> = emptyList(),
    val price: Double = 0.0,
)

@HiltViewModel
class CartPageViewModel @Inject constructor(
    private val cartDao: CartDao
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<CartPageState> = cartDao
        .getAll()
        .mapLatest { items ->
            CartPageState(
                items = items,
                price = items.sumOf { it.price * it.quantity }
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CartPageState()
        )

    fun addItem(cartItem: CartItem) {
        viewModelScope.launch {
            cartDao.updateItem(cartItem.copy(quantity = cartItem.quantity.inc()))
        }
    }

    fun removeItem(cartItem: CartItem) {
        viewModelScope.launch {
            if (cartItem.quantity == 1) {
                cartDao.delete(cartItem)
            } else {
                cartDao.updateItem(cartItem.copy(quantity = cartItem.quantity.dec()))
            }
        }
    }
}
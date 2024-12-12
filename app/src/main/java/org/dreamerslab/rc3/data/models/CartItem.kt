package org.dreamerslab.rc3.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "cart_items"
)
data class CartItem(
    @PrimaryKey
    val menuItemId: String,
    val name: String,
    val price: Double,
    val image: String,
    val quantity: Int,
)

fun MenuItem.toCartItem(): CartItem = CartItem(
    menuItemId = id,
    name = name,
    price = price,
    image = image,
    quantity = 1,
)

package org.dreamerslab.rc3

import androidx.room.Database
import androidx.room.RoomDatabase
import org.dreamerslab.rc3.data.daos.CartDao
import org.dreamerslab.rc3.data.models.CartItem

@Database(
    entities = [CartItem::class],
    version = 1
)
abstract class RC3Database : RoomDatabase() {
    abstract fun getCartDao(): CartDao
}
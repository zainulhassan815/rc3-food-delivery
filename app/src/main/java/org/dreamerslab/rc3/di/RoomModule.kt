package org.dreamerslab.rc3.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.dreamerslab.rc3.RC3Database
import org.dreamerslab.rc3.data.daos.CartDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Singleton
    @Provides
    fun provideRoomDatabase(
        @ApplicationContext context: Context
    ): RC3Database = Room.databaseBuilder(
        context = context,
        RC3Database::class.java,
        "rc3_database"
    ).build()

    @Provides
    fun provideCartDao(
        database: RC3Database
    ): CartDao = database.getCartDao()

}
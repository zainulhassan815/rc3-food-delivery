package org.dreamerslab.rc3.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.dreamerslab.rc3.utils.UiMessageManager

@Module
@InstallIn(SingletonComponent::class)
object UtilsModule {

    @Provides
    fun provideUiMessageManager(): UiMessageManager = UiMessageManager()

}
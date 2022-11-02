package com.wolandpl.nordcodingtask.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    fun provideApplicationCoroutineScope(
        ioCoroutineDispatcher: CoroutineDispatcher,
    ): CoroutineScope =
        CoroutineScope(SupervisorJob() + ioCoroutineDispatcher)
}

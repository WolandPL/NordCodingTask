package com.wolandpl.nordcodingtask.data.di

import android.content.Context
import androidx.room.Room
import com.wolandpl.nordcodingtask.data.local.contact.ContactRepository
import com.wolandpl.nordcodingtask.data.local.contact.ContactRepositoryImpl
import com.wolandpl.nordcodingtask.data.local.phonecall.PhoneCallRepository
import com.wolandpl.nordcodingtask.data.local.phonecall.PhoneCallRepositoryImpl
import com.wolandpl.nordcodingtask.data.local.persistance.AppDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindPhoneCallRepository(
        phoneCallRepositoryImpl: PhoneCallRepositoryImpl
    ): PhoneCallRepository

    @Binds
    abstract fun bindContactRepository(
        contactRepositoryImpl: ContactRepositoryImpl
    ): ContactRepository

    companion object {
        @Singleton
        @Provides
        fun provideDatabase(
            @ApplicationContext applicationContext: Context
        ): AppDatabase = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "nord-coding-task-db"
        ).build()
    }
}

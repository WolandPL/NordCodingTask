package com.wolandpl.nordcodingtask.data.local.persistance

import androidx.room.Database
import androidx.room.RoomDatabase
import com.wolandpl.nordcodingtask.data.model.PhoneCall

@Database(entities = [PhoneCall::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getPhoneCallDao(): PhoneCallDao
}

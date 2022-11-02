package com.wolandpl.nordcodingtask.data.local.persistance

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.wolandpl.nordcodingtask.data.model.PhoneCall
import kotlinx.coroutines.flow.Flow

@Dao
interface PhoneCallDao {

    @Query("SELECT * FROM PhoneCall")
    fun getAll(): Flow<List<PhoneCall>>

    @Insert
    suspend fun insert(phoneCall: PhoneCall)

    @Query("UPDATE PhoneCall SET timesQueried = timesQueried + 1")
    suspend fun increaseQueryCount()
}

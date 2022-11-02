package com.wolandpl.nordcodingtask.data.local.phonecall

import com.wolandpl.nordcodingtask.data.model.PhoneCall
import kotlinx.coroutines.flow.Flow

interface PhoneCallRepository {

    fun getAll(): Flow<List<PhoneCall>>

    suspend fun insert(phoneCall: PhoneCall)

    suspend fun increaseQueryCount()
}

package com.wolandpl.nordcodingtask.data.local.phonecall

import com.wolandpl.nordcodingtask.data.local.persistance.AppDatabase
import com.wolandpl.nordcodingtask.data.model.PhoneCall
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class PhoneCallRepositoryImpl @Inject constructor(
    private val database: AppDatabase
) : PhoneCallRepository {

    override fun getAll(): Flow<List<PhoneCall>> =
        database.getPhoneCallDao().getAll()

    override suspend fun insert(phoneCall: PhoneCall) =
        database.getPhoneCallDao().insert(phoneCall)

    override suspend fun increaseQueryCount() =
        database.getPhoneCallDao().increaseQueryCount()
}

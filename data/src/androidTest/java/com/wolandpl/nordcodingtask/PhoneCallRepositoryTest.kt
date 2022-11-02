package com.wolandpl.nordcodingtask

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.wolandpl.nordcodingtask.data.local.persistance.AppDatabase
import com.wolandpl.nordcodingtask.data.local.persistance.PhoneCallDao
import com.wolandpl.nordcodingtask.data.local.phonecall.PhoneCallRepositoryImpl
import com.wolandpl.nordcodingtask.data.model.PhoneCall
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class PhoneCallRepositoryTest {

    private lateinit var database: AppDatabase
    private lateinit var phoneCallDao: PhoneCallDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        phoneCallDao = database.getPhoneCallDao()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun getAll_returnsData_fromDatabase() = runTest {

        // Given
        DUMMY_PHONE_CALLS.forEach {
            phoneCallDao.insert(it)
        }

        val repository = PhoneCallRepositoryImpl(database)

        // When
        val data = repository.getAll().first()

        // Then
        verify(database) { database.getPhoneCallDao() }
        verify(phoneCallDao) { phoneCallDao.getAll() }

        assertEquals(DUMMY_PHONE_CALLS.size, data.size)

        data.forEachIndexed { index, phoneCall ->
            assertEquals(phoneCall, DUMMY_PHONE_CALLS[index])
        }
    }

    @Test
    fun insert_addsData_toDatabase() = runTest {

        // Given
        val repository = PhoneCallRepositoryImpl(database)

        repository.insert(DUMMY_PHONE_CALLS[0])
        val dataBeforeInsert = repository.getAll().take(1).first()

        // When
        repository.insert(DUMMY_PHONE_CALLS[1])
        val dataAfterInsert = repository.getAll().take(1).first()

        // Then
        assertTrue(dataBeforeInsert.size == dataAfterInsert.size - 1)
    }

    @Test
    fun increaseQueryCount_increasesCounters() = runTest {

        // Given
        DUMMY_PHONE_CALLS.forEach {
            phoneCallDao.insert(it)
        }

        val repository = PhoneCallRepositoryImpl(database)

        // When
        repository.increaseQueryCount()

        // Then
        val data = repository.getAll().first()

        assertEquals(DUMMY_PHONE_CALLS.size, data.size)

        data.forEachIndexed { index, phoneCall ->
            assertTrue(phoneCall.timesQueried == DUMMY_PHONE_CALLS[index].timesQueried + 1)
        }
    }

    companion object {
        private val DUMMY_PHONE_CALLS = listOf(
            PhoneCall(
                beginning = "fakeBeginning1",
                number = "fakeNumber1",
                name = "fakeName1",
                timesQueried = 1
            ),
            PhoneCall(
                beginning = "fakeBeginning2",
                number = "fakeNumber2",
                name = "fakeName2",
                timesQueried = 2
            ),
            PhoneCall(
                beginning = "fakeBeginning3",
                number = "fakeNumber3",
                name = "fakeName3",
                timesQueried = 3
            )
        )
    }
}

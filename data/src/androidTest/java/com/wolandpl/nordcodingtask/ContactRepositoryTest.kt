package com.wolandpl.nordcodingtask

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.test.mock.MockContentResolver
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import com.wolandpl.nordcodingtask.data.local.contact.ContactRepositoryImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

@RunWith(AndroidJUnit4::class)
class ContactRepositoryTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @SdkSuppress(minSdkVersion = 27)
    @Test
    fun getNameByPhoneNumber_returnsData_fromContentProvider() = runTest{

        // Given
        val mockContentProvider = MockContactsContentProvider(DUMMY_PROJECTION)

        val dataUri: Uri =
            Uri.withAppendedPath(
                ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI,
                Uri.encode(DUMMY_PHONE_NUMBER)
            )

        mockContentProvider.insert(dataUri, arrayOf(DUMMY_NAME, DUMMY_PHONE_NUMBER))

        val contentResolver = MockContentResolver()
        contentResolver.addProvider(ContactsContract.AUTHORITY, mockContentProvider)

        val context = mock<Context> {
            on { getContentResolver() } doReturn contentResolver
        }

        val repository = ContactRepositoryImpl(context)

        // When
        val data = repository.getNameByPhoneNumber(DUMMY_PHONE_NUMBER)

        // Then
        assertEquals(DUMMY_NAME, data)
    }

    companion object {

        private const val DUMMY_NAME = "John Smith"
        private const val DUMMY_PHONE_NUMBER = "+0123456789"

        private val DUMMY_PROJECTION = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )
    }
}

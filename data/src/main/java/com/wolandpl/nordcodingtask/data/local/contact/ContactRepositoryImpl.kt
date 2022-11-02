package com.wolandpl.nordcodingtask.data.local.contact

import android.content.Context
import android.net.Uri
import android.provider.ContactsContract.CommonDataKinds.Phone
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ContactRepository {

    override suspend fun getNameByPhoneNumber(phoneNumber: String): String? {
        var displayName: String? = null

        val filterUri: Uri =
            Uri.withAppendedPath(Phone.CONTENT_FILTER_URI, Uri.encode(phoneNumber))
        val cursor = context.contentResolver.query(filterUri, PROJECTION, null, null, null)

        cursor?.let {
            if (it.moveToNext()) {
                val displayNameColumnIndex = it.getColumnIndex(Phone.DISPLAY_NAME)

                if (displayNameColumnIndex < it.columnCount) {
                    displayName = it.getString(displayNameColumnIndex)
                }
            }

            cursor.close()
        }

        return displayName
    }

    companion object {
        private val PROJECTION = arrayOf(Phone.DISPLAY_NAME)
    }
}

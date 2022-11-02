package com.wolandpl.nordcodingtask

import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.test.mock.MockContentProvider

class MockContactsContentProvider(
    private val dataProjection: Array<out String>
) : MockContentProvider() {

    private val data = mutableMapOf<Uri, Array<String>>()

    fun insert(key: Uri, value: Array<String>) {
        data[key] = value
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor {
        val cursor = MatrixCursor(projection)

        data[uri]?.let { rowData ->
            val rowBuilder = cursor.newRow()

            projection?.forEach { columnName ->
                rowBuilder.add(rowData[dataProjection.indexOf(columnName)])
            }
        }

        return cursor
    }
}

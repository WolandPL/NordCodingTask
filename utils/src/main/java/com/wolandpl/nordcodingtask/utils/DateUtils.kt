package com.wolandpl.nordcodingtask.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateUtils {

    companion object {
        private val dateTimeFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.UK)

        fun formatDateTime(date: Date): String = dateTimeFormatter.format(date)
    }
}

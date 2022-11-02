package com.wolandpl.nordcodingtask.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PhoneCall(

    @PrimaryKey
    val beginning: String,

    val number: String,
    val name: String? = null,
    var duration: Long = 0,
    var timesQueried: Int = 0
)

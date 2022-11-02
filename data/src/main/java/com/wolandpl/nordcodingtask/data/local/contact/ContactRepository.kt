package com.wolandpl.nordcodingtask.data.local.contact

interface ContactRepository {

    suspend fun getNameByPhoneNumber(phoneNumber: String): String?
}

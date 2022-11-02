package com.wolandpl.nordcodingtask.server.model

data class OngoingCall(
    val ongoing: Boolean,
    val number: String? = null,
    val name: String? = null
)

package com.wolandpl.nordcodingtask.ui.model

import com.wolandpl.nordcodingtask.data.model.PhoneCall

data class PhoneCallUiState(
    val loading: Boolean = true,
    val permissionsGranted: Boolean = false,
    val roleGranted: Boolean = false,
    val phoneCalls: List<PhoneCall> = emptyList(),
    val isServerStarted: Boolean = false,
    val serverAddress: String? = null
)

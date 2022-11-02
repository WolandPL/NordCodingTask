package com.wolandpl.nordcodingtask.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wolandpl.nordcodingtask.data.local.phonecall.PhoneCallRepository
import com.wolandpl.nordcodingtask.data.model.PhoneCall
import com.wolandpl.nordcodingtask.monitor.CallMonitor
import com.wolandpl.nordcodingtask.server.HttpServer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class PhoneCallViewModel @Inject constructor(
    private val repository: PhoneCallRepository,
    private val server: HttpServer,
    private val callMonitor: CallMonitor
) : ViewModel() {

    private var initialized = false

    private var _phoneCalls = listOf<PhoneCall>()

    var uiState by mutableStateOf(PhoneCallUiState())
        private set

    init {
        uiState = uiState.copy(
            loading = true
        )

        viewModelScope.launch {
            repository.getAll().collect {
                _phoneCalls = it

                uiState = uiState.copy(
                    loading = false,
                    phoneCalls = _phoneCalls.toList(),
                    serverAddress = server.serverAddress
                )
            }
        }
    }

    fun switchServerState() {
        viewModelScope.launch {
            when (uiState.isServerStarted) {
                false -> server.start()
                else -> server.stop()
            }

            uiState = uiState.copy(
                isServerStarted = !uiState.isServerStarted,
                serverAddress = server.serverAddress
            )
        }
    }

    fun onRoleGranted() {
        uiState = uiState.copy(
            roleGranted = true
        )

        if (uiState.permissionsGranted) {
            start()
        }
    }

    fun onPermissionsGranted() {
        uiState = uiState.copy(
            permissionsGranted = true
        )

        if (uiState.roleGranted) {
            start()
        }
    }

    private fun start() {
        if (!initialized) {
            initialized = true

            callMonitor.init()

            viewModelScope.launch {
                server.start()

                uiState = uiState.copy(
                    isServerStarted = true,
                    serverAddress = server.serverAddress
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()

        callMonitor
    }
}

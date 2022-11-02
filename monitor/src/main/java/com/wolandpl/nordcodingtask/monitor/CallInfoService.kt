package com.wolandpl.nordcodingtask.monitor

import android.os.Build
import android.telecom.Call
import android.telecom.CallScreeningService
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.S)
class CallInfoService : CallScreeningService() {

    @Inject
    lateinit var callMonitor: CallMonitor

    override fun onScreenCall(callDetails: Call.Details) {
        respondToCall(callDetails, CallResponse.Builder().build())

        if (callDetails.callDirection == Call.Details.DIRECTION_INCOMING) {
            callMonitor.handleCallStateChange(
                state = TelephonyManager.CALL_STATE_RINGING,
                phoneNumber = callDetails.handle?.schemeSpecificPart ?: "unknown"
            )
        }
    }
}

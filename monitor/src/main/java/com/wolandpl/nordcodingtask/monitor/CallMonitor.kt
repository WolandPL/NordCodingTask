package com.wolandpl.nordcodingtask.monitor

import android.content.Context
import android.os.Build
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import com.wolandpl.nordcodingtask.data.local.contact.ContactRepository
import com.wolandpl.nordcodingtask.data.local.phonecall.PhoneCallRepository
import com.wolandpl.nordcodingtask.data.model.PhoneCall
import com.wolandpl.nordcodingtask.utils.DateUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Singleton
class CallMonitor @Inject constructor(
    private val repository: PhoneCallRepository,
    private val contactRepository: ContactRepository,
    private val applicationIoCoroutineScope: CoroutineScope,
    @ApplicationContext private val context: Context
) {

    var callInProgress: PhoneCall? = null
        private set

    private var callInProgressStart: Date? = null
    private var incomingNumber = ""

    private lateinit var telephonyManager: TelephonyManager
    private lateinit var telephonyCallback: TelephonyCallback
    private lateinit var legacyCallback: PhoneStateListener

    fun init() {
        telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            telephonyCallback = object : TelephonyCallback(), TelephonyCallback.CallStateListener {
                override fun onCallStateChanged(state: Int) {
                    if ((state == TelephonyManager.CALL_STATE_IDLE) ||
                        (state == TelephonyManager.CALL_STATE_OFFHOOK)
                    ) {
                        handleCallStateChange(state)
                    }
                }
            }

            telephonyManager.registerTelephonyCallback(
                context.mainExecutor,
                telephonyCallback
            )
        } else {
            legacyCallback = object : PhoneStateListener() {
                override fun onCallStateChanged(state: Int, phoneNumber: String) {
                    handleCallStateChange(state, phoneNumber)
                }
            }
            telephonyManager.listen(
                legacyCallback,
                PhoneStateListener.LISTEN_CALL_STATE
            )
        }
    }

    fun clean() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            telephonyManager.unregisterTelephonyCallback(telephonyCallback)
        } else {
            telephonyManager.listen(
                legacyCallback,
                PhoneStateListener.LISTEN_NONE
            )
        }
    }

    internal fun handleCallStateChange(
        state: Int,
        phoneNumber: String = ""
    ) {
        when (state) {
            TelephonyManager.CALL_STATE_RINGING -> incomingNumber = phoneNumber
            TelephonyManager.CALL_STATE_OFFHOOK -> {
                applicationIoCoroutineScope.launch {
                    callInProgressStart = Calendar.getInstance().time.also { callStart ->
                        callInProgress = PhoneCall(
                            beginning = DateUtils.formatDateTime(callStart),
                            number = phoneNumber.takeIf { it.isNotBlank() } ?: incomingNumber,
                            name = contactRepository.getNameByPhoneNumber(phoneNumber)
                        )
                    }
                }
            }
            TelephonyManager.CALL_STATE_IDLE -> {
                callInProgress?.let {
                    callInProgressStart?.let { callStart ->
                        it.duration =
                            (Calendar.getInstance().time.time - callStart.time) / 1000
                    }

                    applicationIoCoroutineScope.launch {
                        repository.insert(it)

                        callInProgress = null
                    }
                }
            }
        }
    }
}

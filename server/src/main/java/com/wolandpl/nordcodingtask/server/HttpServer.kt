package com.wolandpl.nordcodingtask.server

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.TrafficStats
import androidx.annotation.RequiresPermission
import com.wolandpl.nordcodingtask.data.local.phonecall.PhoneCallRepository
import com.wolandpl.nordcodingtask.data.model.PhoneCall
import com.wolandpl.nordcodingtask.monitor.CallMonitor
import com.wolandpl.nordcodingtask.server.model.OngoingCall
import com.wolandpl.nordcodingtask.server.model.Root
import com.wolandpl.nordcodingtask.server.model.Service
import com.wolandpl.nordcodingtask.utils.DateUtils
import com.wolandpl.nordcodingtask.utils.JsonUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.net.Inet4Address
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Singleton
class HttpServer @Inject constructor(
    private val callMonitor: CallMonitor,
    private val phoneCallRepository: PhoneCallRepository,
    private val applicationIoCoroutineScope: CoroutineScope,
    @ApplicationContext private val context: Context
) {
    var serverAddress: String? = null
        private set

    private var server: NettyApplicationEngine? = null
    private var phoneCalls: List<PhoneCall> = emptyList()
    private var rootResponse: Root? = null

    init {
        applicationIoCoroutineScope.launch {
            phoneCallRepository.getAll().collect {
                phoneCalls = it
            }
        }
    }

    @RequiresPermission(
        allOf = [
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.INTERNET
        ]
    )

    suspend fun start() {
        TrafficStats.setThreadStatsTag(THREAD_TAG)
        server = embeddedServer(Netty, 0) {
            routing {
                get("/") {
                    rootResponse?.let {
                        call.respondText(
                            text = JsonUtils.toJson(it),
                            contentType = ContentType.Application.Json
                        )
                    }
                }

                get("/status") {
                    val callInProgress = callMonitor.callInProgress
                    val ongoingCall = if (callInProgress != null) {
                        callInProgress.timesQueried++

                        OngoingCall(
                            ongoing = true,
                            number = callInProgress.number,
                            name = callInProgress.name
                        )
                    } else OngoingCall(false)

                    call.respondText(
                        text = JsonUtils.toJson(ongoingCall),
                        contentType = ContentType.Application.Json
                    )
                }

                get("/log") {
                    call.respondText(
                        text = JsonUtils.toJson(phoneCalls),
                        contentType = ContentType.Application.Json
                    )

                    applicationIoCoroutineScope.launch {
                        phoneCallRepository.increaseQueryCount()
                    }
                }
            }
        }

        server?.start(wait = false)

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val host = connectivityManager.getLinkProperties(connectivityManager.activeNetwork)?.let {
            it.linkAddresses.firstOrNull { linkAddress ->
                linkAddress.address is Inet4Address
            }?.address ?: it.linkAddresses.firstOrNull()
        } ?: ""

        server?.resolvedConnectors()?.firstOrNull()?.let {
            serverAddress = "http://$host:${it.port}"

            rootResponse = Root(
                start = DateUtils.formatDateTime(Calendar.getInstance().time),
                services = availableServices.map { serviceName ->
                    Service(
                        name = serviceName,
                        url = "$serverAddress/$serviceName"
                    )
                }
            )
        }
    }

    fun stop() {
        serverAddress = null

        server?.stop()
    }

    companion object {
        private val THREAD_TAG = HttpServer.hashCode()

        private val availableServices = listOf(
            "status", "log"
        )
    }
}


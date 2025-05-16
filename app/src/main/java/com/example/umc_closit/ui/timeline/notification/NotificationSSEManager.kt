package com.example.umc_closit.ui.timeline.notification

import android.os.Build
import android.util.Log
import com.launchdarkly.eventsource.EventHandler
import com.launchdarkly.eventsource.EventSource
import com.launchdarkly.eventsource.MessageEvent
import okhttp3.Headers
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.util.concurrent.TimeUnit

object NotificationSSEManager {
    private var eventSource: EventSource? = null

    fun startSSEConnection(token: String) {
        val url = "http://54.180.132.28:8080/"

        val handler = object : EventHandler {
            override fun onOpen() {
                Log.d("SSE", "연결 열림")
            }

            override fun onClosed() {
                Log.d("SSE", "연결 닫힘")
            }

            override fun onMessage(event: String?, messageEvent: MessageEvent?) {
                Log.d("SSE", "Event: $event, Message: ${messageEvent?.data}")
            }

            override fun onComment(comment: String?) {}

            override fun onError(t: Throwable?) {
                Log.e("SSE", "Error: ${t?.message}")
            }
        }

        val builder = EventSource.Builder(handler, url.toHttpUrl())
            .headers(
                Headers.Builder()
                    .add("Authorization", "Bearer $token")
                    .build()
            )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.reconnectTime(java.time.Duration.ofMillis(3000))
        } else {
            // 이전 버전에서는 오류 나므로 일단 설정 공백 처리
        }

        eventSource = builder.build()
        eventSource?.start()
    }

    fun stopSSEConnection() {
        eventSource?.close()
    }
}

package com.desynatdealer.utils.agora

import android.content.Context
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine

object AgoraManager {

    private var rtcEngine: RtcEngine? = null

    fun initialize(context: Context, appId: String) {
        rtcEngine = RtcEngine.create(context, appId, object : IRtcEngineEventHandler() {})
    }

    fun getEngine(): RtcEngine? {
        return rtcEngine
    }
}
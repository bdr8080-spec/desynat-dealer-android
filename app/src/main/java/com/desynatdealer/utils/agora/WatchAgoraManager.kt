package com.desynatdealer.utils.agora

import android.content.Context
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine

object WatchAgoraManager {

    private var rtcEngine: RtcEngine? = null

    fun initialize(
        context: Context,
        appId: String,
        handler: IRtcEngineEventHandler
    ) {
        rtcEngine = RtcEngine.create(context, appId, handler)
    }

    fun getEngine(): RtcEngine? {
        return rtcEngine
    }
}
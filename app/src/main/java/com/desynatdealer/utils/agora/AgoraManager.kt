package com.desynatdealer.utils.agora

import android.content.Context
import android.util.Log
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine

object AgoraManager {

    private var rtcEngine: RtcEngine? = null

    fun initialize(context: Context, appId: String) {
        if (rtcEngine != null) return
        val rtcEventHandler = object : IRtcEngineEventHandler() {

            override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
                Log.d("HOST_TEST", "Joined channel successfully: $channel, uid: $uid")
            }

            override fun onConnectionStateChanged(state: Int, reason: Int) {
                Log.d("HOST_TEST", "Connection state: $state, reason: $reason")

                if (state == Constants.CONNECTION_STATE_CONNECTED) {
                    Log.d("HOST_TEST", "Connected to Agora server")
                }
            }

            override fun onLocalVideoStateChanged(
                source: Constants.VideoSourceType,
                state: Int,
                error: Int
            ) {
                Log.d("HOST_TEST", "Local video state: $state")

                if (state == Constants.LOCAL_VIDEO_STREAM_STATE_ENCODING) {
                    Log.d("HOST_TEST", "🎥 Video is being sent")
                }
            }

            override fun onError(err: Int) {
                Log.e("HOST_TEST", "Error: $err")
            }
        }

        rtcEngine = RtcEngine.create(context, appId, rtcEventHandler)
    }

    fun getEngine(): RtcEngine? {
        return rtcEngine
    }
}
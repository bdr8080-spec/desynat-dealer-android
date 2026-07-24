package com.desynatdealer.ui.home.liveStreaming

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentWatchLiveEventBinding
import com.desynatdealer.ui.home.liveStreaming.response.GetOtherLiveStreamingRoomResponse
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.agora.AgoraRetrofit
import com.desynatdealer.utils.agora.WatchAgoraManager
import com.desynatdealer.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.video.VideoCanvas
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class WatchLiveEventFragment : BaseFragment() {
    private var binding: FragmentWatchLiveEventBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var isTokenObserver = true
    private var channelId = ""
    private var token = ""
    private var appId = ""
    private var uidValue = 0
    private var isMuted = false
    private var isVideoSetup = false
    private var eventData: GetOtherLiveStreamingRoomResponse.Data?=null

    var basicAuth = ""

    private val userCountHandler = Handler(Looper.getMainLooper())
    private val userCountRunnable = object : Runnable {
        override fun run() {
            getLiveUserCount()
            userCountHandler.postDelayed(this, 5000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWatchLiveEventBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        convertBase64()
        val itemJson = arguments?.getString("eventData")
        if (!itemJson.isNullOrEmpty()) {
            eventData = Gson().fromJson(itemJson, GetOtherLiveStreamingRoomResponse.Data::class.java)
            channelId = eventData?.channelId.toString()
        }
        binding?.apply {

            if (eventData?.liveToken==null){
                getAgoraToken(eventData?.id.toString())
            }
            else{

                token = eventData?.liveToken.toString()
                uidValue = eventData?.uid?.toIntOrNull() ?: 0

                startLiveStreaming()
            }
        }


      //  getLiveStreaming()
    }

    override fun initControl() {
        binding?.apply {
            ivBack.setOnClickListener {
                WatchAgoraManager.getEngine()?.apply {
                    leaveChannel()
                }

                findNavController().popBackStack()
            }

            tvLeave.setOnClickListener {
                WatchAgoraManager.getEngine()?.apply {
                    leaveChannel()
                }

                findNavController().popBackStack()
            }

            tvMute.setOnClickListener {
                val engine = WatchAgoraManager.getEngine()

                isMuted = !isMuted

                engine?.muteAllRemoteAudioStreams(isMuted)

                if (isMuted) {
                    binding?.tvMute?.text = getString(R.string.unmute)
                } else {
                    binding?.tvMute?.text = getString(R.string.mute)
                }
            }
        }
    }

    override fun observe() {
        viewModel.getAgoraTokenResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    if (result.data?.data!=null){
                        token = result.data?.data?.token.toString()
                        uidValue = result.data?.data?.uid?.toIntOrNull()?:0
                        channelId = result.data?.data?.channel.toString()
                        appId = result.data?.data?.appId.toString()
                        startLiveStreaming()
                    }
                    else{
                        showNoDataUIWaiting()
                    }
                    viewModel.getAgoraTokenResponse.value = null

                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.getAgoraTokenResponse.value = null
                }

                else -> {}
            }
        }

    }

    private val rtcEventHandler = object : IRtcEngineEventHandler() {

        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            Log.d("LIVE_TEST", "Audience joined channel")
            activity?.runOnUiThread {
                showNoDataUIWaiting(true)
            }
        }

        override fun onUserJoined(uid: Int, elapsed: Int) {
            Log.d("LIVE_TEST", "Host joined: $uid")
            if (!isVideoSetup) {
                isVideoSetup = true

                activity?.runOnUiThread {
                    setupRemoteVideo(uid)
                    showNoDataUI(false)
                    startUserCount()
                }
            }
        }

        override fun onRemoteVideoStateChanged(
            uid: Int,
            state: Int,
            reason: Int,
            elapsed: Int
        ) {
            Log.d("LIVE_TEST", "Remote video state: $state for uid: $uid")

            if (state == 2 && !isVideoSetup) { // decoding
                isVideoSetup = true

                activity?.runOnUiThread {
                    setupRemoteVideo(uid)
                    showNoDataUI(false)
                    startUserCount()
                }
            }
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            Log.d("LIVE_TEST", "Host left")
            activity?.runOnUiThread {
                stopUserCount()
                binding?.videoContainer?.removeAllViews()

                WatchAgoraManager.getEngine()?.setupRemoteVideo(
                    VideoCanvas(null, VideoCanvas.RENDER_MODE_HIDDEN, uid)
                )
                CommonMethod.showSuccessDialog(requireActivity(), getString(R.string.host_left)){
                    findNavController().popBackStack()
                }
             //   showNoDataUI(true)

            }

        }

        override fun onError(err: Int) {
            Log.e("LIVE_TEST", "Agora Error: $err")
            activity?.runOnUiThread {
                showNoDataUI(true)
            }
        }
    }

    private fun showNoDataUI(isShow: Boolean=false) {
        binding?.apply {
            tvNoDataFound.isVisible = isShow
            ivBack.isVisible = isShow
            tvToolbar.isVisible = isShow
            tvLive.isVisible = !isShow
            tvAudienceCount.isVisible = !isShow
            relMute.isVisible = !isShow
            tvNoDataFound.text = getString(R.string.no_event_found)
            if (isShow) stopUserCount()
        }
    }

    private fun showNoDataUIForNextEvent(isShow: Boolean=false) {
        binding?.apply {
            tvNoDataFound.isVisible = isShow
            ivBack.isVisible = isShow
            tvToolbar.isVisible = isShow
            tvLive.isVisible = !isShow
            tvAudienceCount.isVisible = !isShow
            relMute.isVisible = !isShow
            tvNoDataFound.text = getString(R.string.waiting_for_next_live_event)
            if (isShow) stopUserCount()
        }
    }

    private fun showNoDataUIWaiting(isShow: Boolean=false) {
        binding?.apply {
            tvNoDataFound.isVisible = isShow
            ivBack.isVisible = isShow
            tvToolbar.isVisible = isShow
            tvLive.isVisible = !isShow
            tvAudienceCount.isVisible = !isShow
            relMute.isVisible = !isShow
            tvNoDataFound.text = getString(R.string.waiting_for_host_to_join)
            if (isShow) stopUserCount()
        }
    }

    private fun joinChannel() {

        val engine = WatchAgoraManager.getEngine()

        engine?.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING)
        engine?.setClientRole(Constants.CLIENT_ROLE_AUDIENCE)
        engine?.enableVideo()

        val options = ChannelMediaOptions().apply {
            clientRoleType = Constants.CLIENT_ROLE_AUDIENCE
            channelProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING
        }

        engine?.joinChannel(
               token,
            channelId,
            uidValue,
            options
        )


    }

    private fun setupRemoteVideo(uid: Int) {

        val surfaceView = RtcEngine.CreateRendererView(requireContext())

        binding?.videoContainer?.addView(surfaceView)

        WatchAgoraManager.getEngine()?.setupRemoteVideo(
            VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        userCountHandler.removeCallbacks(userCountRunnable)

        WatchAgoraManager.getEngine()?.leaveChannel()

        binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        userCountHandler.removeCallbacks(userCountRunnable)

        WatchAgoraManager.getEngine()?.leaveChannel()

        binding = null
    }

    private fun getAgoraToken(id: String){
        val map = HashMap<String,Any>()
        map["id"] = id
        map["role"] = 1
        viewModel.getAgoraToken(map)
        isTokenObserver = true
    }

    private fun checkForNextEvent() {

        Handler(Looper.getMainLooper()).postDelayed({

            Log.d("LIVE_TEST", "Checking for next event...")

          //  getLiveStreaming()

        }, 5000)
    }

    private fun startLiveStreaming(){

        WatchAgoraManager.initialize(
            requireContext(),
            getString(R.string.agora_app_id),
            rtcEventHandler
        )

        joinChannel()
    }

    private fun getLiveUserCount() {
        lifecycleScope.launch {
            try {
                val response = AgoraRetrofit.api.getLiveUserCount(
                    basicAuth,
                    appId,
                    channelId
                )

                val count = response.body()?.data?.audienceTotal ?: 0

                activity?.runOnUiThread {
                    setUserCountData(count)
                }

            } catch (e: Exception) {
                Log.e("LIVE_TEST", e.message ?: "Error")
            }
        }
    }

    private fun setUserCountData(audienceTotal: Int?) {
        binding?.apply {
            if ((audienceTotal ?: 0) > 0) {
             //   tvAudienceCount.isVisible = false
                tvAudienceCount.text = getString(R.string.viewers_format, getString(R.string.viewers), audienceTotal.toString())
            } else {
                //   tvAudienceCount.isVisible = false
            //    tvAudienceCount.isVisible = true
                tvAudienceCount.text = getString(R.string.viewers_format, getString(R.string.viewers), "0")
            }
        }
    }

    private fun convertBase64(){
        val customerID = "0ddf0359302c4e3b995282b44470e25e"
        val customerCertificate = "c258c2e0e578420fbdb0e43baf5b1ee3"
        val credentials = "$customerID:$customerCertificate"
        val base64Encoded = Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
        println(base64Encoded)
        basicAuth = "Basic " + base64Encoded
        Log.d("LIVE_TEST", "base64Encoded: $base64Encoded")
    }

    private fun startUserCount() {
        userCountHandler.removeCallbacks(userCountRunnable)
        userCountHandler.post(userCountRunnable)
    }

    private fun stopUserCount() {
        userCountHandler.removeCallbacks(userCountRunnable)
    }

}
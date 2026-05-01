package com.desynatdealer.ui.home.liveStreaming

import android.Manifest
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentLiveStreamingBinding
import com.desynatdealer.ui.home.liveStreaming.response.GetLiveStreamingRoomResponse
import com.desynatdealer.ui.home.liveStreaming.response.GetLiveUserCountResponse
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.agora.AgoraManager
import com.desynatdealer.utils.agora.AgoraRetrofit
import com.desynatdealer.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.video.VideoCanvas
import kotlinx.coroutines.launch
import kotlin.text.toIntOrNull

@AndroidEntryPoint
class LiveStreamingFragment : BaseFragment() {
    private var binding : FragmentLiveStreamingBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    private var channelId = ""
    private var appId = ""

    private var token = ""
    private var uid = 0
    var isLive = false

    private var isMuted = false
    var basicAuth = ""
    var eventData:GetLiveStreamingRoomResponse.Data.CurrentRequest?=null


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
        binding = FragmentLiveStreamingBinding.inflate(layoutInflater, container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        val itemJson = arguments?.getString("eventData")
        if (!itemJson.isNullOrEmpty()) {
            eventData = Gson().fromJson(itemJson, GetLiveStreamingRoomResponse.Data.CurrentRequest::class.java)
            getAgoraToken(eventData?.id.toString())
        }
      //  getLiveRoomRecords()
    }

    override fun initControl() {
        binding?.apply {

            ivBack.setOnClickListener {
                AgoraManager.getEngine()?.apply {
                    stopPreview()
                    leaveChannel()
                }

                findNavController().popBackStack()
            }

            ivSwitchCamera.setOnClickListener {
                AgoraManager.getEngine()?.switchCamera()
            }

            tvJoin.setOnClickListener {
               tvNoDataFound.isVisible = false
               tvToolbar.isVisible = false
                ivBack.isVisible = false
               tvJoin.isVisible = false
               ivSwitchCamera.isVisible = true
               relMute.isVisible = true
               tvLive.isVisible = true
                tvAudienceCount.isVisible = true
                startLiveStream()
            }

            tvLeave.setOnClickListener {
                AgoraManager.getEngine()?.apply {
                    stopPreview()
                    leaveChannel()
                }

                findNavController().popBackStack()
            }

            tvMute.setOnClickListener {
                val engine = AgoraManager.getEngine()

                isMuted = !isMuted

                engine?.muteLocalAudioStream(isMuted)

                if (isMuted) {
                    binding?.tvMute?.text = getString(R.string.unmute)
                } else {
                    binding?.tvMute?.text = getString(R.string.mute)
                }
            }
        }
    }

    override fun observe() {
        viewModel.getLiveRoomRecordsResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
          /*          if (result.data?.data!=null){
                        if (result.data?.data?.currentRequests?.isNotEmpty() == true){
                            channelId = result.data?.data?.currentRequests?.get(0)?.channelId.toString()
//                            if (result.data?.data?.currentRequests?.get(0)?.liveToken!=null&&result.data?.data?.currentRequests?.get(0)?.liveToken!="null"&&result.data?.data?.currentRequests?.get(0)?.liveToken!=""){
//                                token = result.data?.data?.currentRequests?.get(0)?.liveToken.toString()
//                                val uidValue = result.data?.data?.currentRequests?.get(0)?.uid.toString()
//                                uid = uidValue.toIntOrNull() ?: 0
//                                checkPermission()
//                            }
//                            else{
//                                getAgoraToken(result.data.data.currentRequests[0].id.toString())
//                            }
                            getAgoraToken(result.data.data.currentRequests[0].id.toString())
                        }
                        else{
                            showNoDataUIWaiting()
                        }
                    }
                    else{
                        showNoDataUIWaiting()
                    }
*/

                    val currentList = result.data?.data?.currentRequests

                    if (!currentList.isNullOrEmpty()) {

                        val liveIndex = currentList.indexOfFirst { item ->
                            isLiveTime(item.timeslotDetails)
                        }

                        if (liveIndex != -1) {
                            isLive = true

                            val liveItem = currentList[liveIndex]

                            getAgoraToken(liveItem.id.toString())

                        } else {
                            isLive = false
                            showNoDataUIWaiting()
                        }

                    } else {
                        showNoDataUIWaiting()
                    }
                    viewModel.getLiveRoomRecordsResponse.value = null

                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                      showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.getLiveRoomRecordsResponse.value = null
                }

                else -> {}
            }
        }

        viewModel.getAgoraTokenResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    if (result.data?.data!=null){
                        token = result.data?.data?.token.toString()
                        uid = result.data?.data?.uid?.toIntOrNull()?:0
                        channelId = result.data?.data?.channel.toString()
                        appId = result.data?.data?.appId.toString()
                        convertBase64()
                        checkPermission()
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

        viewModel.getLiveEventUserCountResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()

                    viewModel.getLiveEventUserCountResponse.value = null

                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.getLiveEventUserCountResponse.value = null
                }

                else -> {}
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()

        userCountHandler.removeCallbacks(userCountRunnable)

        AgoraManager.getEngine()?.apply {
            stopPreview()
            leaveChannel()
        }

        binding = null
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

            val cameraGranted = permissions[Manifest.permission.CAMERA] ?: false
            val micGranted = permissions[Manifest.permission.RECORD_AUDIO] ?: false

            if (cameraGranted && micGranted) {
                AgoraManager.initialize(requireActivity(), appId)
                binding?.apply {
                    tvNoDataFound.isVisible = false
                    tvToolbar.isVisible = false
                    ivBack.isVisible = false
                    tvJoin.isVisible = false
                    ivSwitchCamera.isVisible = true
                    relMute.isVisible = true
                    tvLive.isVisible = true
                    tvAudienceCount.isVisible = true
                }
                startLiveStream()
            } else {
                requireContext().showToast(getString(R.string.camera_and_microphone_permission_required))
            }
        }

    private fun startLiveStream() {
        val engine = AgoraManager.getEngine()

        engine?.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING)

        engine?.setClientRole(Constants.CLIENT_ROLE_BROADCASTER)

        engine?.enableVideo()
        engine?.enableAudio()

        val surfaceView = RtcEngine.CreateRendererView(requireContext())
        binding?.videoContainer?.addView(surfaceView)

        engine?.setupLocalVideo(
            VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid)
        )

        val options = ChannelMediaOptions().apply {
            clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
            channelProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING

            publishCameraTrack = true
            publishMicrophoneTrack = true

            autoSubscribeVideo = true
            autoSubscribeAudio = true
        }

        engine?.joinChannel(
            token,
            channelId,
            uid,
            options
        )
        engine?.startPreview()

        userCountHandler.post(userCountRunnable)
    }
    private fun getLiveRoomRecords() {

        viewModel.getLiveRoomRecords()
    }

    private fun getLiveEventUserCount() {

        viewModel.getLiveEventUserCount()
    }
    private fun getAgoraToken(id: String) {
        val map = HashMap<String,Any>()
        map["id"] = id
        map["role"] = 2
        viewModel.getAgoraToken(map)
    }

    private fun checkPermission(){
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            )
        )

    }

    private fun showNoDataUIWaiting(isShow: Boolean=true) {
        binding?.apply {
            tvNoDataFound.isVisible = isShow
            ivBack.isVisible = isShow
            tvToolbar.isVisible = isShow
            tvLive.isVisible = !isShow
            tvAudienceCount.isVisible = !isShow
            relMute.isVisible = !isShow
            ivSwitchCamera.isVisible = !isShow
            tvNoDataFound.text = getString(R.string.no_live_event_found)
        }
    }

    private fun getLiveUserCount() {
        lifecycleScope.launch {
            try {
                val response = AgoraRetrofit.api.getLiveUserCount(
                    basicAuth,
                    appId,
                    channelId
                )

              //  val count = response.body()?.data?.audienceTotal ?: 0
                val count = response.body()?.data?.audience?.size ?: 0


                activity?.runOnUiThread {

                    showLog(response.body()?.data)
                    setUserCountData(count)
                }

            } catch (e: Exception) {
                Log.e("LIVE_TEST", e.message ?: "Error")
            }
        }
    }

    private fun showLog(data: GetLiveUserCountResponse.Data?) {
        Log.e("LIVE_TEST",  data.toString())
    }

    private fun setUserCountData(audienceTotal: Int?) {
        binding?.apply {
            if ((audienceTotal ?: 0) > 0) {
                tvAudienceCount.isVisible = true
                tvAudienceCount.text = "${getString(R.string.viewers)} : $audienceTotal"
            } else {
             //   tvAudienceCount.isVisible = false
                tvAudienceCount.isVisible = true
                tvAudienceCount.text = "${getString(R.string.viewers)} : 0"
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

    private fun isLiveTime(timeslot: GetLiveStreamingRoomResponse.Data.CurrentRequest.TimeslotDetails): Boolean {
        return try {
            val date = timeslot.date
            val fromTime = timeslot.from
            val toTime = timeslot.to

            val formatter = java.text.SimpleDateFormat("yyyy-MM-dd hh:mm a", java.util.Locale.ENGLISH)

            val startDateTime = formatter.parse("$date $fromTime")
            val endDateTime = formatter.parse("$date $toTime")

            val currentTime = java.util.Date()

            currentTime.after(startDateTime) && currentTime.before(endDateTime)

        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

}
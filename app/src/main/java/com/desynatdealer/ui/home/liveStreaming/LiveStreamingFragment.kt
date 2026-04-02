package com.desynatdealer.ui.home.liveStreaming

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.desynatdealer.R
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentLiveStreamingBinding
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.SharedPreference
import com.desynatdealer.utils.agora.AgoraManager
import com.desynatdealer.utils.agora.AgoraRetrofit
import com.desynatdealer.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.video.VideoCanvas
import kotlinx.coroutines.launch
import kotlin.text.get

@AndroidEntryPoint
class LiveStreamingFragment : BaseFragment() {
    private var binding : FragmentLiveStreamingBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    private var channelId = ""
    private var token = ""
    private var uid = 0

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

        getLiveRoomRecords()
    }

    override fun initControl() {
        binding?.apply {
            ivSwitchCamera.setOnClickListener {
                AgoraManager.getEngine()?.switchCamera()
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
                    if (result.data?.data!=null){
                        if (result.data?.data?.currentRequests?.isNotEmpty() == true){
                            channelId = result.data?.data?.currentRequests?.get(0)?.channelId.toString()
                            if (result.data?.data?.currentRequests?.get(0)?.liveToken==null){
                                getAgoraLiveTokenApi(result.data.data.currentRequests[0].id.toString())
                            }
                            else{
                                token = result.data?.data?.currentRequests?.get(0)?.liveToken.toString()
                                val uidValue = result.data?.data?.currentRequests?.get(0)?.uid.toString()
                                uid = uidValue.toIntOrNull() ?: 0
                                checkPermission()
                            }


                        }
                        else{
                            showNoDataUIWaiting()
                        }
                    }
                    else{
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

    }

    override fun onDestroyView() {
        super.onDestroyView()

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
                startLiveStream()
            } else {
                requireContext().showToast("Camera and microphone permission required")
            }
        }

    private fun startLiveStream() {
        val engine = AgoraManager.getEngine()

        engine?.enableVideo()

        val surfaceView = io.agora.rtc2.RtcEngine.CreateRendererView(requireContext())

        binding?.videoContainer?.addView(surfaceView)

        engine?.setupLocalVideo(
            VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, 0)
        )

        engine?.startPreview()

        val options = ChannelMediaOptions().apply {
            clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
            channelProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING
        }

        engine?.joinChannel(
            token,
            channelId,
            uid,
            options
        )
    }

    private fun getLiveRoomRecords() {

        viewModel.getLiveRoomRecords()
    }

    private fun checkPermission(){
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            )
        )

    }

    private fun showNoDataUIWaiting(isShow: Boolean=false) {
        binding?.apply {
            tvNoDataFound.isVisible = isShow
            tvToolbar.isVisible = isShow
            tvLive.isVisible = !isShow
            tvNoDataFound.text = "No live event found"
        }
    }

    private fun getAgoraLiveTokenApi(id: String){
        val map = HashMap<String,Any>()
        map["id"] = id
        lifecycleScope.launch {
            try {
                val response = AgoraRetrofit.api.getAgoraToken(
                    SharedPreference.get(requireActivity()).accessToken,
                    map
                )
                ProgressDialogUtils.getInstance().hideProgress()
                Log.e("AGORA_TOKEN_RESPONSE", response.body().toString() ?: "Error")

                token = response.body()?.token.toString()
                val uidValue = response.body()?.uid.toString()
                uid = uidValue.toIntOrNull() ?: 0
                checkPermission()


            } catch (e: Exception) {
                ProgressDialogUtils.getInstance().hideProgress()
                Log.e("AGORA_TOKEN_ERROR", e.message ?: "Error")
            }
        }
    }


}
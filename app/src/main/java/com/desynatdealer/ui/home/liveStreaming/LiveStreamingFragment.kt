package com.desynatdealer.ui.home.liveStreaming

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.desynatdealer.R
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentLiveStreamingBinding
import com.desynatdealer.utils.agora.AgoraManager
import com.desynatdealer.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.video.VideoCanvas

@AndroidEntryPoint
class LiveStreamingFragment : BaseFragment() {
    private var binding : FragmentLiveStreamingBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()

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

        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            )
        )

    }

    override fun initControl() {
        binding?.apply {
            ivSwitchCamera.setOnClickListener {
                AgoraManager.getEngine()?.switchCamera()
            }
        }
    }

    override fun observe() {
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
            getString(R.string.agora_token),
            "liveChannel123",
            0,
            options
        )
    }

}
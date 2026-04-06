package com.desynatdealer.ui.home.profile

import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.desynatdealer.R
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentAddStoryBinding
import com.desynatdealer.databinding.FragmentPreviewStoryBinding
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.CommonMethod.toMultiPartBody
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.showToast
import com.otaliastudios.transcoder.Transcoder
import com.otaliastudios.transcoder.TranscoderListener
import com.otaliastudios.transcoder.resize.AtMostResizer
import com.otaliastudios.transcoder.strategy.DefaultAudioStrategy
import com.otaliastudios.transcoder.strategy.DefaultVideoStrategy
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import kotlin.getValue

@AndroidEntryPoint
class PreviewStoryFragment : BaseFragment() {
    private var binding: FragmentPreviewStoryBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var mediaType = ""
    private var uri=""
    private var from=""
    private var name=""

    private var compressedVideoFile: File? = null

    private var thumbnailFile: File? = null
    private var uploadImageVideo = ""
    private var uploadThumbnail = ""

    private var isThumbnailUploading = false
    private var isVideoUploading = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPreviewStoryBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        binding?.apply {
            toolbar.tvToolbar.text = "Add Story"

            mediaType = arguments?.getString("mediaType").toString()
            uri = arguments?.getString("uri").toString()
            from = arguments?.getString("from").toString()

            if (from=="highlights"){
                name = arguments?.getString("name").toString()
            }

                ivImage.isVisible = false
                ivImage.setImageDrawable(null)

                videoView.isVisible = false
                videoView.stopPlayback()
                videoView.setVideoURI(null)

                if (mediaType == "image") {
                    ivImage.isVisible = true
                    Glide.with(requireActivity())
                        .load(uri)
                        .placeholder(R.drawable.ic_placeholder)
                        .into(ivImage)
                } else {
                    videoView.isVisible = true
                    videoView.setVideoURI( Uri.parse(uri))

                    videoView.setOnPreparedListener { mp ->
                        val videoWidth = mp.videoWidth
                        val videoHeight = mp.videoHeight

                        val viewWidth = videoView.width
                        val viewHeight = videoView.height

                        val videoProportion = videoWidth.toFloat() / videoHeight.toFloat()
                        val viewProportion = viewWidth.toFloat() / viewHeight.toFloat()

                        val layoutParams = videoView.layoutParams

                        if (videoProportion > viewProportion) {
                            layoutParams.width = viewWidth
                            layoutParams.height = (viewWidth / videoProportion).toInt()
                        } else {
                            layoutParams.height = viewHeight
                            layoutParams.width = (viewHeight * videoProportion).toInt()
                        }

                        videoView.layoutParams = layoutParams


                        mp.isLooping = false
                        val duration = mp.duration.toLong()

                        videoView.start()
                    }
                }


        }
    }

    override fun initControl() {
        binding?.apply {

            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            tvPost.setOnClickListener {
                if (mediaType == "image") {
                    val file = CommonMethod.uriToFileForStory(requireActivity(), Uri.parse(uri))
                    val image = file?.toMultiPartBody("file")
                    viewModel.uploadVideoImage(image)
                }
                else{
                    if (!isVideoWithinLimit(Uri.parse(uri), 30)) {
                        showToast("Video must not be more than 30 seconds")
                        return@setOnClickListener
                    }
                    thumbnailFile = CommonMethod.getVideoThumbnailFile(requireActivity(), Uri.parse(uri))
                    compressAndUploadVideo(Uri.parse(uri))
                }

            }
        }
    }

    override fun observe() {
        viewModel.uploadVideoImageResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ApiResult.Success -> {

                    val url = result.data?.data?.url ?: ""

                    when {
                        isThumbnailUploading -> {
                            uploadThumbnail = url
                            isThumbnailUploading = false

                            isVideoUploading = true
                            uploadCompressedVideo(compressedVideoFile)
                        }

                        isVideoUploading -> {
                            uploadImageVideo = url
                            isVideoUploading = false

                            addStory(uploadImageVideo)
                        }

                        else -> {
                            addStory(url) // image case
                        }
                    }

                    viewModel.uploadVideoImageResponse.value = null
                }

                is ApiResult.Error -> {
                    showToast(result.message ?: "Error")
                    ProgressDialogUtils.getInstance().hideProgress()
                    viewModel.uploadVideoImageResponse.value = null
                }

                else -> {}
            }
        }


        viewModel.addStoryResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    CommonMethod.showSuccessDialog(requireContext(), "Story added successfully!"){
                        findNavController().popBackStack(R.id.viewProfile, false)
                    }
                    viewModel.addStoryResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.addStoryResponse.value = null
                }

                else -> {}
            }
        }
    }
    private fun compressAndUploadVideo(inputUri: Uri) {
        ProgressDialogUtils.getInstance().showProgress(requireActivity())

        val outputFile = File(
            requireContext().cacheDir,
            "story_${System.currentTimeMillis()}.mp4"
        )

        Transcoder.into(outputFile.absolutePath)
            .addDataSource(requireContext(), inputUri)
            .setVideoTrackStrategy(
                DefaultVideoStrategy.Builder()
                    .addResizer(AtMostResizer(1280)) // 720p
                    .bitRate(500_000)              // 1.5 Mbps
                    .frameRate(30)
                    .build()
            )
            .setAudioTrackStrategy(
                DefaultAudioStrategy.Builder()
                    .bitRate(64_000)
                    .build()
            )
            .setListener(object : TranscoderListener {

                override fun onTranscodeProgress(progress: Double) {
                }

                override fun onTranscodeCompleted(successCode: Int) {
                    requireActivity().runOnUiThread {
                        ProgressDialogUtils.getInstance().hideProgress()

                        compressedVideoFile = outputFile

                        isThumbnailUploading = true
                        uploadThumbnailApi(thumbnailFile)
                    }
                }

                override fun onTranscodeCanceled() {
                    requireActivity().runOnUiThread {
                        ProgressDialogUtils.getInstance().hideProgress()
                        showToast("Video compression cancelled")
                    }
                }

                override fun onTranscodeFailed(exception: Throwable) {
                    requireActivity().runOnUiThread {
                        ProgressDialogUtils.getInstance().hideProgress()
                        showToast("Video compression failed")
                    }
                    exception.printStackTrace()
                }
            })
            .transcode()
    }

    private fun uploadCompressedVideo(file: File?) {
        file?.exists()?.let {
            if (!it) {
                showToast("Compressed file not found")
                return
            }
        }
        val image = file?.toMultiPartBody("file")
        viewModel.uploadVideoImage(image)
    }

    private fun uploadThumbnailApi(file: File?) {
        file?.exists()?.let {
            if (!it) {
                showToast("Thumbnail not found")
                return
            }
        }
        val image = file?.toMultiPartBody("file")
        viewModel.uploadVideoImage(image)
    }

    private fun addStory(story: String) {
        val map = HashMap<String,Any>()
        if (mediaType!="image"&&uploadThumbnail!=""){
            map["thumbnail"] = uploadThumbnail
        }
        map["file"] = story
        viewModel.addStory(map)
    }

    fun isVideoWithinLimit(uri: Uri, maxDurationSec: Int = 30): Boolean {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(requireContext(), uri)

            val durationMs = retriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_DURATION
            )?.toLongOrNull() ?: return false

            val allowedMs = (maxDurationSec * 1000) + 1000

            durationMs <= allowedMs

        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            retriever.release()
        }
    }

}
package com.desynatdealer.ui.home.profile

import android.Manifest
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.desynatdealer.R
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentAddNewReelBinding
import com.desynatdealer.ui.home.profile.response.GetStoryResponse
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.CommonMethod.toMultiPartBody
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.StoryMediaPicker
import com.desynatdealer.utils.showToast
import com.google.gson.Gson
import com.otaliastudios.transcoder.Transcoder
import com.otaliastudios.transcoder.TranscoderListener
import com.otaliastudios.transcoder.resize.AtMostResizer
import com.otaliastudios.transcoder.strategy.DefaultAudioStrategy
import com.otaliastudios.transcoder.strategy.DefaultVideoStrategy
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import kotlin.getValue

@AndroidEntryPoint
class AddNewReelFragment : BaseFragment() {
    private var binding: FragmentAddNewReelBinding?=null
    private val viewModel: HomeViewModel by viewModels()

    private lateinit var storyMediaPicker: StoryMediaPicker
    private var mediaType = ""
    private  var imageUri: Uri?=null
    private val PAGE_SIZE = 40

    private var compressedVideoFile: File? = null
    private var thumbnailFile: File? = null
    private var uploadImageVideo = ""
    private var isThumbnailUploading = false
    private var isVideoUploading = false
    private var uploadThumbnail = ""

    private var reelData: GetStoryResponse.Data?=null

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            storyMediaPicker.onImageCameraResult(success)
        }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            storyMediaPicker.onImageGalleryResult(uri)
        }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            storyMediaPicker.onPermissionResult(granted)
        }

    private val videoCameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            storyMediaPicker.onVideoCameraResult(
                result.resultCode == AppCompatActivity.RESULT_OK
            )
        }


    private val videoGalleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            storyMediaPicker.onVideoGalleryResult(uri)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddNewReelBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
        checkPermission()
    }

    override fun initView() {
        binding?.apply {

            val itemJson = arguments?.getString("reelData")

            if (!itemJson.isNullOrEmpty()) {
                reelData = Gson().fromJson(itemJson, GetStoryResponse.Data::class.java)
                tvToolbar.text = "Edit Reel"
                tvAdd.text = "Update"
                setData()
            }


            storyMediaPicker = StoryMediaPicker(
                requireActivity(),
                cameraLauncher,
                galleryLauncher,
                videoCameraLauncher,
                videoGalleryLauncher,
                permissionLauncher
            ) { uri, type ->
                uri?.let {
                    mediaType = type
                    imageUri = it
                    if (type == "image") {
                        ivStory.setImageURI(it)
                    } else {
                        Glide.with(requireActivity())
                            .load(imageUri)
                            .placeholder(R.drawable.ic_placeholder)
                            .into(ivStory)
                      //  ivStory.setImageResource(R.drawable.ic_add_reel)
                    }

                }
            }

        }

    }

    private fun setData() {
        binding?.apply {
            val url = reelData?.file ?: ""

            etCaption.setText(reelData?.caption)
            uploadImageVideo = url

            val isVideo = url.endsWith(".mp4", true) ||
                    url.endsWith(".mov", true)||
                    url.endsWith(".avi", true)||
                    url.endsWith(".mkv", true)

            if (isVideo) {
                mediaType = "video"

                uploadThumbnail = reelData?.thumbnail.toString()

                Glide.with(requireActivity())
                    .load(url)
                    .placeholder(R.drawable.ic_placeholder)
                    .into(ivStory)

            } else {
                mediaType = "image"

                Glide.with(requireActivity())
                    .load(url)
                    .into(ivStory)
            }
        }
    }

    override fun initControl() {
        binding?.apply {

            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            ivStory.setOnClickListener {
                storyMediaPicker.showReelPicker()
            }

//            tvAdd.setOnClickListener {
//
//                if (etCaption.text?.trim().toString().isEmpty()){
//                    showToast("Please enter caption")
//                    return@setOnClickListener
//                }
//
//                if (imageUri == null && reelData != null) {
//                    addReel(uploadImageVideo)
//                    return@setOnClickListener
//                }
//
//                if (mediaType == "image") {
//                    val file = CommonMethod.uriToFileForStory(requireActivity(), uri = imageUri!!)
//                    val image = file?.toMultiPartBody("file")
//                    viewModel.uploadVideoImage(image)
//                } else {
//                    if (!isVideoWithinLimit(imageUri!!, 30)) {
//                        showToast("Video longer than 30 sec, trimming automatically...")
//
//                    } else {
//                        val outputFile = File(requireContext().cacheDir, "story_trimmed_${System.currentTimeMillis()}.mp4")
//
//                        CommonMethod.trimVideoTo30SecWithMediaMuxer(requireActivity(),imageUri!!,outputFile)
//                        thumbnailFile = CommonMethod.getVideoThumbnailFile(requireActivity(), imageUri!!)
//                        compressAndUploadVideo(imageUri!!)
//                    }
////                    thumbnailFile = CommonMethod.getVideoThumbnailFile(requireActivity(), imageUri!!)
////                    compressAndUploadVideo(imageUri!!)
//                }
//            }

            tvAdd.setOnClickListener {

                if (etCaption.text?.trim().toString().isEmpty()) {
                    showToast("Please enter caption")
                    return@setOnClickListener
                }

                if (imageUri == null && reelData != null) {
                    addReel(uploadImageVideo)
                    return@setOnClickListener
                }

                if (mediaType == "image") {
                    val file = CommonMethod.uriToFileForStory(requireActivity(), uri = imageUri!!)
                    val image = file?.toMultiPartBody("file")
                    viewModel.uploadVideoImage(image)
                } else {

//                    if (!isVideoWithinLimit(imageUri!!, 30)) {
//                        showToast("Video must not be more than 30 seconds")
//                        return@setOnClickListener
//                    }

               /*     val outputFile = File(
                        requireContext().cacheDir,
                        "story_trimmed_${System.currentTimeMillis()}.mp4"
                    )

                    CommonMethod.trimVideoTo30SecWithMediaMuxer(requireContext(), imageUri!!, outputFile)
                    compressAndUploadVideo(Uri.fromFile(outputFile))
*/
                    thumbnailFile = CommonMethod.getVideoThumbnailFile(requireActivity(), imageUri!!)
                    compressAndUploadVideo(imageUri!!)
                }
            }
        }
    }

    override fun observe() {
        viewModel.uploadVideoImageResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    if (!isVideoUploading){
                        ProgressDialogUtils.getInstance().showProgress(requireActivity())
                    }
                }
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

                            addReel(uploadImageVideo)
                        }

                        else -> {
                            addReel(url) // image case
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
        viewModel.addReelResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                 //   ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    CommonMethod.showSuccessDialog(requireContext(), "Reel added successfully!"){
                        findNavController().popBackStack()
                    }
                    viewModel.addReelResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.addReelResponse.value = null
                }

                else -> {}
            }
        }

        viewModel.updateReelResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    CommonMethod.showSuccessDialog(requireContext(), "Reel updated successfully!"){
                        findNavController().popBackStack(R.id.viewProfile, false)
                    }
                    viewModel.updateReelResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.updateReelResponse.value = null
                }

                else -> {}
            }
        }

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

    private fun addReel(story: String) {
        val map = HashMap<String,Any>()
        if (mediaType!="image"&&uploadThumbnail!=""){
            map["thumbnail"] = uploadThumbnail
        }
        map["file"] = story
        map["caption"] = binding?.etCaption?.text?.trim().toString()
        if (reelData!=null){
            map["reel_id"] = reelData?.id.toString()
            viewModel.updateReel(map)
        }
        else{
            viewModel.addReel(map)
        }

    }


    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            galleryPermissionLauncher.launch(
                arrayOf(
//                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO
                )
            )
        } else {
            galleryPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        }
    }

    private val galleryPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

            val granted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            //    val imageGranted = permissions[Manifest.permission.READ_MEDIA_IMAGES] ?: false
                val videoGranted = permissions[Manifest.permission.READ_MEDIA_VIDEO] ?: false
              //  imageGranted ||
                        videoGranted
            } else {
                permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: false
            }

            if (granted) {
            } else {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_VIDEO)) {
                    showToast("Permission permanently denied. Please allow from settings.")
                } else {
                }
            }
        }

    fun isVideoWithinLimit(uri: Uri, maxDurationSec: Int = 30): Boolean {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(requireContext(), uri)

            val durationMs = retriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_DURATION
            )?.toLongOrNull()

            durationMs != null && durationMs <= maxDurationSec * 1000
        } catch (e: Exception) {
            e.printStackTrace()
            false   // treat invalid video as not allowed
        } finally {
            try {
                retriever.release()
            } catch (e: Exception) { }
        }
    }

    //5 mb limit
    private fun compressAndUploadVideo5mb(inputUri: Uri) {
        ProgressDialogUtils.getInstance().showProgress(requireActivity())

        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(requireContext(), inputUri)

        val durationMs = retriever.extractMetadata(
            MediaMetadataRetriever.METADATA_KEY_DURATION
        )?.toLongOrNull() ?: 0

        retriever.release()

        val durationSec = durationMs / 1000.0

        // Target: 5MB
        val targetSizeBytes = 5 * 1024 * 1024
        val targetBitrate = ((targetSizeBytes * 8) / durationSec).toLong()

        val outputFile = File(
            requireContext().cacheDir,
            "story_${System.currentTimeMillis()}.mp4"
        )

        Transcoder.into(outputFile.absolutePath)
            .addDataSource(requireContext(), inputUri)
            .setVideoTrackStrategy(
                DefaultVideoStrategy.Builder()
                    .addResizer(AtMostResizer(720))
                    .bitRate(targetBitrate)
                    .frameRate(24)
                    .build()
            )
            .setAudioTrackStrategy(
                DefaultAudioStrategy.Builder()
                    .bitRate(64_000)
                    .build()
            )
            .setListener(object : TranscoderListener {

                override fun onTranscodeCompleted(successCode: Int) {
                    requireActivity().runOnUiThread {
                        ProgressDialogUtils.getInstance().hideProgress()

                        val fileSizeMB = outputFile.length() / (1024 * 1024)

                        if (fileSizeMB > 5) {
                            showToast("Video still too large, try shorter video")
                            return@runOnUiThread
                        }

                        compressedVideoFile = outputFile

                        isThumbnailUploading = true
                        uploadThumbnailApi(thumbnailFile)
                    }
                }

                override fun onTranscodeCanceled() {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast("Compression cancelled")
                }

                override fun onTranscodeFailed(exception: Throwable) {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast("Compression failed")
                }

                override fun onTranscodeProgress(progress: Double) {}
            })
            .transcode()
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


}
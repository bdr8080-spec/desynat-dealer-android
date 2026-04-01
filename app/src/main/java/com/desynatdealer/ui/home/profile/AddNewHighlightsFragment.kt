package com.desynatdealer.ui.home.profile

import android.Manifest
import android.content.ContentUris
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.desynatdealer.R
import com.desynatdealer.adapter.GalleryAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentAddNewHighlightsBinding
import com.desynatdealer.ui.home.profile.response.GalleryMedia
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.CommonMethod.toMultiPartBody
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.StoryMediaPicker
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
class AddNewHighlightsFragment : BaseFragment() {
    private var binding: FragmentAddNewHighlightsBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var storyMediaPicker: StoryMediaPicker
    private var mediaType = ""
    private var galleryAdapter: GalleryAdapter?=null
    private val imageList = mutableListOf<Uri>()
    private var offset = 0
    private var isLoading = false
    private lateinit var imageUri: Uri
    private val PAGE_SIZE = 40

    private var compressedVideoFile: File? = null

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
        binding = FragmentAddNewHighlightsBinding.inflate(layoutInflater,container,false)
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

            toolbar.tvToolbar.text = "Add New Highlight"

            galleryAdapter = GalleryAdapter(requireActivity(),mutableListOf()) { media ->
                when (media) {
                    is GalleryMedia.Image -> {
                        mediaType = "image"
                        imageUri = media.uri
                        ivAddStory.setImageURI(imageUri)
                    }
                    is GalleryMedia.Video -> {
                        if(isVideoWithinLimit(media.uri,30)) {
                            mediaType = "video"
                            imageUri = media.uri
                            ivAddStory.setImageURI(imageUri)
                        }
                        else{
                            showToast("Please select a video of 30 seconds or less.")
                        }
                    }
                }
            }

            rvGallery.apply {
                layoutManager = GridLayoutManager(requireContext(), 3)
                adapter = galleryAdapter
                addOnScrollListener(scrollListener)
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
                        binding?.ivAddStory?.setImageURI(it)
                    } else {
                        binding?.ivAddStory?.setImageResource(R.drawable.ic_placeholder)
                    }

                }
            }

        }

    }

    override fun initControl() {
        binding?.apply {

            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            ivAddStory.setOnClickListener {
                storyMediaPicker.showCameraStoryPicker()
            }

            tvAdd.setOnClickListener {

                if (imageUri==null){
                    showToast("Please select image")
                }
                else if (etName.text?.trim().toString().isEmpty()){
                    showToast("Please enter name")
                }
                else{
                    if (mediaType == "image") {
                        val file = CommonMethod.uriToFileForStory(requireActivity(), uri = imageUri)
                        val image = file?.toMultiPartBody("file")
                        viewModel.uploadVideoImage(image)
                    }
                    else{
                        compressAndUploadVideo(imageUri)
                    }
                }

            }

        }
    }

    override fun observe() {
        viewModel.uploadVideoImageResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    addHighlights(result.data?.data?.url.toString())
                    compressedVideoFile?.delete()
                    viewModel.uploadVideoImageResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.uploadVideoImageResponse.value = null
                }

                else -> {}
            }
        }

        viewModel.addHighlightsResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    CommonMethod.showSuccessDialog(requireContext(), "Highlights added successfully!"){
                        findNavController().popBackStack(R.id.viewProfile, false)
                    }
                    viewModel.addHighlightsResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.addHighlightsResponse.value = null
                }

                else -> {}
            }
        }
    }

    private fun uploadCompressedVideo(file: File) {
        if (!file.exists()) {
            showToast("Compressed file not found")
            return
        }
        val image = file?.toMultiPartBody("file")
        viewModel.uploadVideoImage(image)
    }
    private fun addHighlights(story: String) {
        val map = HashMap<String,Any>()
        map["file"] = story
        map["title"] = binding?.etName?.text?.trim().toString()
        viewModel.addHighlights(map)
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val lm = recyclerView.layoutManager as GridLayoutManager
            val visibleCount = lm.childCount
            val totalCount = lm.itemCount
            val firstVisible = lm.findFirstVisibleItemPosition()

            if (!isLoading && visibleCount + firstVisible >= totalCount) {
                loadGalleryMedia()
            }
        }
    }

    private fun loadGalleryMedia() {
        if (isLoading) return
        isLoading = true

        val newItems = ArrayList<GalleryMedia>()

        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.MEDIA_TYPE
        )

        val selection =
            "${MediaStore.Files.FileColumns.MEDIA_TYPE}=? OR " +
                    "${MediaStore.Files.FileColumns.MEDIA_TYPE}=?"

        val selectionArgs = arrayOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
        )

        val sortOrder = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"
        val cursor = requireContext().contentResolver.query(
            MediaStore.Files.getContentUri("external"),
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        cursor?.use {
            val allItems = mutableListOf<GalleryMedia>()
            val idCol = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val typeCol = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)

            while (it.moveToNext()) {
                val id = it.getLong(idCol)
                val type = it.getInt(typeCol)
                val uri = when (type) {
                    MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE ->
                        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO ->
                        ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
                    else -> null
                }
                uri?.let {
                    if (type == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)
                        allItems.add(GalleryMedia.Image(it))
                    else
                        allItems.add(GalleryMedia.Video(it))
                }
            }

            // Apply pagination manually
            val pagedItems = allItems.drop(offset).take(PAGE_SIZE)
            galleryAdapter?.addItems(pagedItems)
            offset += PAGE_SIZE
            isLoading = false

        }
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            galleryPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
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
                val imageGranted = permissions[Manifest.permission.READ_MEDIA_IMAGES] ?: false
                val videoGranted = permissions[Manifest.permission.READ_MEDIA_VIDEO] ?: false
                imageGranted || videoGranted
            } else {
                permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: false
            }

            if (granted) {
                loadGalleryMedia()
            } else {
                showToast("Permission required to access images and videos")
            }
        }

    fun isVideoWithinLimit(uri: Uri, maxDurationSec: Int = 30): Boolean {
        val retriever = android.media.MediaMetadataRetriever()
        return try {
            retriever.setDataSource(requireContext(), uri)

            val durationMs = retriever.extractMetadata(
                android.media.MediaMetadataRetriever.METADATA_KEY_DURATION
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
                    .bitRate(1_500_000)              // 1.5 Mbps
                    .frameRate(30)
                    .build()
            )
            .setAudioTrackStrategy(
                DefaultAudioStrategy.Builder()
                    .bitRate(96_000)
                    .build()
            )
            .setListener(object : TranscoderListener {

                override fun onTranscodeProgress(progress: Double) {
                }

                override fun onTranscodeCompleted(successCode: Int) {
                    requireActivity().runOnUiThread {
                        ProgressDialogUtils.getInstance().hideProgress()

                        compressedVideoFile = outputFile
                        uploadCompressedVideo(outputFile)
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
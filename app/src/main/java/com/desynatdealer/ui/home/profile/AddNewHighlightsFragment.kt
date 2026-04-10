package com.desynatdealer.ui.home.profile

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.desynatdealer.R
import com.desynatdealer.adapter.CountryPickerAdapter
import com.desynatdealer.adapter.GalleryAdapter
import com.desynatdealer.adapter.HighlightPickerAdapter
import com.desynatdealer.adapter.PreviousHighlightsAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.CustomCountryPickerBinding
import com.desynatdealer.databinding.FragmentAddNewHighlightsBinding
import com.desynatdealer.model.country.GetCountryResponse
import com.desynatdealer.ui.home.profile.response.GalleryMedia
import com.desynatdealer.ui.home.profile.response.GetStoryResponse
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.CommonMethod.toMultiPartBody
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.StoryMediaPicker
import com.desynatdealer.utils.showToast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
    private var imageUri: Uri?=null
    private var imageUrl: String?=null
    private val PAGE_SIZE = 40
    private var isMediaSelected = false
    private val highlightList = ArrayList<GetStoryResponse.Data>()

    private var thumbnailFile: File? = null
    private var uploadImageVideo = ""
    private var uploadThumbnail = ""

    private var isThumbnailUploading = false
    private var isVideoUploading = false


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

        val json = arguments?.getString("highlightsData")

        if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<ArrayList<GetStoryResponse.Data>>() {}.type
            val highlightsData: ArrayList<GetStoryResponse.Data> =
                Gson().fromJson(json, type)

            highlightList.clear()
            highlightList.addAll(highlightsData)
        }
        binding?.apply {
            rvGallery?.visibility = View.VISIBLE
            rvPrevious?.visibility = View.GONE
            toolbar.tvToolbar.text = "Add New Highlight"

            galleryAdapter = GalleryAdapter(requireActivity(),mutableListOf()) { media ->
                when (media) {
                    is GalleryMedia.Image -> {
                        mediaType = "image"
                        imageUri = media.uri
                        isMediaSelected = true

                     //   ivAddStory.setImageURI(imageUri)
                        Glide.with(requireActivity())
                            .load(imageUri)
                            .placeholder(R.drawable.ic_placeholder)
                            .into(ivAddStory)

                    }
                    is GalleryMedia.Video -> {
                      //  if(isVideoWithinLimit(media.uri,30)) {
                            mediaType = "video"
                            imageUri = media.uri
                            isMediaSelected = true
                            Glide.with(requireActivity())
                                .load(imageUri)
                                .placeholder(R.drawable.ic_placeholder)
                                .into(ivAddStory)
                         //   ivAddStory.setImageURI(imageUri)
//                        }
//                        else{
//                            showToast("Please select a video of 30 seconds or less.")
//                        }
                    }
                }
            }

            rvGallery.apply {
                layoutManager = GridLayoutManager(requireContext(), 3)
                adapter = galleryAdapter
                addOnScrollListener(scrollListener)
            }


            val previousAdapter = PreviousHighlightsAdapter(requireActivity(),highlightList) { item ->
                Glide.with(requireActivity())
                    .load(item.file )
                    .into(ivAddStory)
                imageUrl = item.file
                isMediaSelected = true


                item.file?.let { mediaType = if (it.endsWith(".mp4")) "video" else "image" }
                imageUrl = item.file
                uploadThumbnail = item.thumbnail.toString()
            }

            rvPrevious.apply {
                layoutManager = GridLayoutManager(requireContext(), 3)
                adapter = previousAdapter
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
                    isMediaSelected = true

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

            rgHighlights?.setOnCheckedChangeListener { _, checkedId ->
                ivAddStory?.setImageResource(R.drawable.ic_upload_highlights)
                imageUrl = null
                imageUri = null
                mediaType = ""
                isMediaSelected = false

                when (checkedId) {

                    R.id.cbAddNew -> {
                        binding?.rvGallery?.visibility = View.VISIBLE
                        binding?.rvPrevious?.visibility = View.GONE
                        binding?.etCaption?.visibility = View.GONE
                        binding?.etName?.visibility = View.VISIBLE
                        etCaption.setText("")
                        etName.setText("")


                        binding?.tvSelectFromGallery?.text = "Select From Gallery"
                        checkData()
                    }

                    R.id.cbPrevious -> {
                        binding?.rvGallery?.visibility = View.GONE
                        binding?.rvPrevious?.visibility = View.VISIBLE
                        binding?.etCaption?.visibility = View.VISIBLE
                        binding?.etName?.visibility = View.GONE
                        etCaption.setText("")
                        etName.setText("")
                        binding?.tvSelectFromGallery?.text = "Select From Previous"
                        checkData()
                    }
                }
            }

            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            etCaption.setOnClickListener {
                showHighlightPicker(requireActivity(),etCaption)
            }

            ivAddStory.setOnClickListener {
                if (rvGallery.isVisible){
                    storyMediaPicker.showCameraStoryPicker("Add Highlights")

                }
                else{
                    storyMediaPicker.showAllStoryPicker("Add Highlights")
                }
            }

            tvAdd.setOnClickListener {


                if (!isMediaSelected) {
                    showToast("Please select image")
                }
                else if (imageUri==null&&imageUrl==null){
                    showToast("Please select image")
                }
                else if (etName.text?.trim().toString().isEmpty()){
                    showToast("Please enter name")
                }
                else{

                    if (imageUrl!=null) {
                        addHighlights(imageUrl.toString())
                    } else {
                        if (mediaType == "image") {
                            if (imageUri != null) {
                                val file = CommonMethod.uriToFileForStory(requireActivity(), imageUri!!)
                                val image = file?.toMultiPartBody("file")
                                viewModel.uploadVideoImage(image)
                            }
                        } else {
                            if (imageUri != null) {
                                thumbnailFile = CommonMethod.getVideoThumbnailFile(requireActivity(),
                                    imageUri!!
                                )
                                isThumbnailUploading = true
                                uploadThumbnailApi(thumbnailFile)
                              //  compressAndUploadVideo(imageUri!!)
                            }
                        }
                    }
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
                    }                }
                is ApiResult.Success -> {

                    val url = result.data?.data?.url ?: ""

                    when {
                        isThumbnailUploading -> {
                            uploadThumbnail = url
                            isThumbnailUploading = false

                            isVideoUploading = true
                            val file = CommonMethod.uriToFileForStory(requireActivity(), imageUri!!)
                            uploadCompressedVideo(file)
                        }

                        isVideoUploading -> {
                            uploadImageVideo = url
                            isVideoUploading = false

                            addHighlights(uploadImageVideo)
                        }

                        else -> {
                            addHighlights(url) // image case
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
              //      ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    if (result.statusCode==499){
                        CommonMethod.showAlertDialog(requireActivity(),result?.message.toString()){
                            findNavController().navigate(R.id.action_addPreviewStoryFragment_to_SubscriptionPlanFragment)
                        }

                    }
                    else {
                        CommonMethod.showSuccessDialog(
                            requireContext(),
                            "Highlights added successfully!"
                        ) {
                            findNavController().popBackStack(R.id.viewProfile, false)
                        }
                    }
                    viewModel.addHighlightsResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    if (result.message=="You have no active plan"){
                        CommonMethod.showAlertDialog(requireActivity(),result.message){
                            findNavController().navigate(R.id.action_addHighlightsFragment_to_SubscriptionPlanFragment)
                        }

                    }
                    else{
                        showToast(result.message ?: getString(R.string.generic_error))
                    }
                    viewModel.addHighlightsResponse.value = null
                }

                else -> {}
            }
        }
    }

    private fun uploadCompressedVideo(file: File?) {
        file?.exists()?.let {
            if (!it) {
                showToast("Video not found")
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
    private fun addHighlights(story: String) {
        val map = HashMap<String,Any>()
        if (mediaType!="image"&&uploadThumbnail!=""){
            map["thumbnail"] = uploadThumbnail
        }
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

                    .bitRate(2_500_000)
                    .frameRate(30)
                    .build()
            )
            .setAudioTrackStrategy(
                DefaultAudioStrategy.Builder()
                    .bitRate(128_000)
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

    private fun checkData(){
        binding?.apply {
            if (rvPrevious.isVisible){
                tvNoDataFound.isVisible = highlightList.isEmpty()

            }
            else{
                tvNoDataFound.isVisible = false
            }
        }

    }

    fun showHighlightPicker(
        context: Context,
        anchor: View) {
        val dialogBinding = CustomCountryPickerBinding.inflate(LayoutInflater.from(context))

        val popupWindow = PopupWindow(
            dialogBinding?.root,
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        popupWindow.isOutsideTouchable = true
        popupWindow.elevation = 20f
        popupWindow.showAsDropDown(anchor)

        dialogBinding.rvCountry.adapter = HighlightPickerAdapter(
            context, highlightList,
            object : HighlightPickerAdapter.ClickOnItem {
                override fun onClickItem(position: Int) {
                    binding?.etName?.setText(highlightList[position].title)
                    binding?.etCaption?.setText(highlightList[position].title)
                    popupWindow.dismiss()
                }
            }
        )
    }


}
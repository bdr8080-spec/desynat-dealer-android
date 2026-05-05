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
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentAddStoryBinding
import com.desynatdealer.ui.home.profile.response.GalleryMedia
import com.desynatdealer.utils.StoryMediaPicker
import com.desynatdealer.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import kotlin.getValue

@AndroidEntryPoint
class AddStoryFragment : BaseFragment() {
    private var binding: FragmentAddStoryBinding?=null
    private val viewModel: HomeViewModel by viewModels()

    private lateinit var storyMediaPicker: StoryMediaPicker
    private var mediaType = ""
    private var galleryAdapter: GalleryAdapter?=null
    private val imageList = mutableListOf<Uri>()
    private var offset = 0
    private var isLoading = false
    private val PAGE_SIZE = 40


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
        binding = FragmentAddStoryBinding.inflate(layoutInflater,container,false)
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

            toolbar.tvToolbar.text = getString(R.string.add_story)

            galleryAdapter = GalleryAdapter(requireActivity(),mutableListOf()) { media ->
                when (media) {
                    is GalleryMedia.Image -> {
                        mediaType = "image"
                        val bundle = Bundle().apply {
                            putString("mediaType", mediaType)
                            putString("uri",media.uri.toString())
                            putString("from","story")
                        }
                        findNavController().navigate(R.id.action_addStoryFragment_to_addPreviewStoryFragment,bundle)
                    }
                    is GalleryMedia.Video -> {
//                        if(isVideoWithinLimit(media.uri,30)) {
                            mediaType = "video"
                            val bundle = Bundle().apply {
                                putString("mediaType", mediaType)
                                putString("uri",media.uri.toString())
                                putString("from","story")
                            }
                            findNavController().navigate(R.id.action_addStoryFragment_to_addPreviewStoryFragment,bundle)
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

                    val bundle = Bundle().apply {
                        putString("mediaType", mediaType)
                        putString("uri",it.toString())
                        putString("from","story")
                    }
                    findNavController().navigate(R.id.action_addStoryFragment_to_addPreviewStoryFragment,bundle)
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

        }
    }

    override fun observe() {

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
                showToast(getString(R.string.permission_required_to_access_images_and_videos))
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




}
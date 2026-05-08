package com.desynatdealer.utils

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.desynatdealer.R
import java.io.File

class StoryMediaPicker(
    private val context: Context,
    private val imageCameraLauncher: ActivityResultLauncher<Uri>,
    private val imageGalleryLauncher: ActivityResultLauncher<String>,
    private val videoCameraLauncher: ActivityResultLauncher<Intent>,
    private val videoGalleryLauncher: ActivityResultLauncher<String>,
    private val permissionLauncher: ActivityResultLauncher<String>,
    private val callback: (Uri?, String) -> Unit
) {

    private var cameraUri: Uri? = null
    private enum class PendingAction {
        IMAGE_CAMERA,
        VIDEO_CAMERA,
        NONE
    }

    private var pendingAction = PendingAction.NONE

    fun showCameraStoryPicker(title: String?=context.getString(R.string.add_story)) {
        val options = arrayOf(context.getString(R.string.camera_photo),
            context.getString(R.string.record_video))
        AlertDialog.Builder(context)
            .setTitle(title)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCameraImage()
                    1 -> openCameraVideo()
                }
            }.show()
    }

    fun showAllStoryPicker(title: String?=context.getString(R.string.add_story)) {
        val options = arrayOf(context.getString(R.string.camera_photo), context.getString(R.string.record_video),
            context.getString(
                R.string.gallery_image
            ),
            context.getString(R.string.gallery_video))
        AlertDialog.Builder(context)
            .setTitle(title)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCameraImage()
                    1 -> openCameraVideo()
                    2 -> openGalleryImage()
                    3 -> openGalleryVideo()
                }
            }.show()
    }

    fun showReelPicker(title: String?=context.getString(R.string.add_story)) {
        val options = arrayOf(context.getString(R.string.gallery_video), context.getString(R.string.record_video))
        AlertDialog.Builder(context)
            .setTitle(title)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openGalleryVideo()
                    1 -> openCameraVideo()
                }
            }.show()
    }

    fun showCameraImagePicker() {
        openCameraImage()
    }

    fun showCameraVideoPicker() {
        openCameraVideo()
    }

//    fun showCameraStoryPicker() {
//        val options = arrayOf("Camera Photo", "Record Video","Gallery Photo", "Gallery Video")
//        AlertDialog.Builder(context)
//            .setTitle("Add Story")
//            .setItems(options) { _, which ->
//                when (which) {
//                    0 -> openCameraImage()
//                    1 -> openCameraVideo()
//                    2 -> openGalleryImage()
//                    3 -> openGalleryVideo()
//                }
//            }.show()
//    }


    private fun openCameraImage() {
        if (!checkCameraPermission(PendingAction.IMAGE_CAMERA)) return

        cameraUri = createUri("IMG", "jpg")

        imageCameraLauncher.launch(cameraUri!!)
    }


    /*
        private fun openCameraImage() {
            if (!checkCameraPermission()) return
            cameraUri = createUri("IMG", "jpg")
            cameraUri?.let { uri ->
                imageCameraLauncher.launch(uri)
            }
        }
    */

    private fun openCameraVideo() {
        if (!checkCameraPermission(PendingAction.VIDEO_CAMERA)) return

        cameraUri = createUri("VID", "mp4")

        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, cameraUri)
        //    putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30)
            putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)

            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }

        videoCameraLauncher.launch(intent)
    }


    /*private fun openCameraVideo() {
        if (!checkCameraPermission()) return

        cameraUri = createUri("VID", "mp4")

        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, cameraUri)
            putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30)
            putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
        }

        videoCameraLauncher.launch(intent)
    }
*/
    private fun openGalleryImage() {
        imageGalleryLauncher.launch("image/*")
    }

    private fun openGalleryVideo() {
        videoGalleryLauncher.launch("video/*")
    }

    fun onImageCameraResult(success: Boolean) {
        if (success) callback(cameraUri, "image")
        else callback(null, "")
        cameraUri = null
    }

    fun onVideoCameraResult(success: Boolean) {
        if (success) callback(cameraUri, "video")
        else callback(null, "")
        cameraUri = null
    }

   /* fun onImageGalleryResult(uri: Uri?) {
        callback(uri, "image")
    }
*/
    fun onImageGalleryResult(uri: Uri?) {
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        callback(uri, "image")
    }

   /* fun onVideoGalleryResult(uri: Uri?) {
        callback(uri, "video")
    }
*/
    fun onVideoGalleryResult(uri: Uri?) {
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        callback(uri, "video")
    }

    fun onPermissionResult(granted: Boolean) {
        if (!granted) {
            callback(null, "")
            pendingAction = PendingAction.NONE
            return
        }

        when (pendingAction) {
            PendingAction.IMAGE_CAMERA -> openCameraImage()
            PendingAction.VIDEO_CAMERA -> openCameraVideo()
            else -> Unit
        }

        pendingAction = PendingAction.NONE
    }

    private fun checkCameraPermission(action: PendingAction): Boolean {
        return if (
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            pendingAction = action
            permissionLauncher.launch(Manifest.permission.CAMERA)
            false
        }
    }

    private fun createUri(prefix: String, ext: String): Uri {
        val file = File(context.cacheDir, "${prefix}_${System.currentTimeMillis()}.$ext")
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    }
}


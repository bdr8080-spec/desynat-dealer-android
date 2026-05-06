package com.desynatdealer.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.desynatdealer.R
import java.io.File

class ImagePicker(
    private val context: Context,
    private val cameraLauncher: ActivityResultLauncher<Uri>,
    private val galleryLauncher: ActivityResultLauncher<String>,
    private val permissionLauncher: ActivityResultLauncher<String>,
    private val callback: (Uri?) -> Unit
) {
    private var cameraImageUri: Uri? = null
    private var alertDialog: AlertDialog? = null

    fun showImageSourceDialog() {
        if (alertDialog?.isShowing == true) {
            return
        }
        val options = arrayOf(context.getString(R.string.camera),
            context.getString(R.string.gallery))
        alertDialog = AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.select_image_source))
            .setItems(options) { _, which ->
                when (which) {
                    0 -> checkCameraPermissionAndOpen()
                    1 -> openGallery()
                }
            }
            .show()
    }

    private fun checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            openCamera()
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun openCamera() {
        cameraImageUri = createImageUri()
        cameraLauncher.launch(cameraImageUri!!)
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun createImageUri(): Uri {
        val imageFile = File(context.cacheDir, "IMG_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            imageFile
        )
    }

    fun onCameraResult(success: Boolean) {
        if (success) callback(cameraImageUri) else callback(null)
        cameraImageUri = null
    }

    fun onGalleryResult(uri: Uri?) {
        callback(uri)
    }

    fun onPermissionResult(isGranted: Boolean) {
        if (isGranted) openCamera() else callback(null)
    }
}

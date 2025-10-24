package com.desynatdealer.utils

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.text.InputFilter
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

fun AppCompatActivity.statusBarTransparentWithWhite() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            statusBarColor = Color.TRANSPARENT
        }
    }
}


fun Activity.moveActivity(activity: Activity) {
    startActivity(Intent(this, activity::class.java))
}

fun Context.showToast(message: String){
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

}

// 🔍 Utility: Check if internet is available
fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

fun Fragment.pickImageFromCameraOrGallery(onImagePicked: (File) -> Unit) {
    var cameraImageUri: Uri? = null
    var pendingAction: (() -> Unit)? = null

    val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.all { it.value }) {
                pendingAction?.invoke()
            }
            pendingAction = null
        }

    val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { onImagePicked(requireContext().uriToFile(it)) }
        }

    val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                cameraImageUri?.let { onImagePicked(requireContext().uriToFile(it)) }
            }
        }

    fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    fun openCamera() {
        cameraImageUri = requireContext().createImageUri()
        cameraLauncher.launch(cameraImageUri)
    }

    fun checkCameraPermissionAndOpen() {
        pendingAction = { openCamera() }
        requestPermissionLauncher.launch(arrayOf(Manifest.permission.CAMERA))
    }

    fun checkGalleryPermissionAndOpen() {
        pendingAction = { openGallery() }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.READ_MEDIA_IMAGES))
        } else {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
        }
    }

    AlertDialog.Builder(requireContext())
        .setTitle("Select Image Source")
        .setItems(arrayOf("Camera", "Gallery")) { _, which ->
            when (which) {
                0 -> checkCameraPermissionAndOpen()
                1 -> checkGalleryPermissionAndOpen()
            }
        }
        .show()
}

fun Context.uriToFile(uri: Uri): File {
    val inputStream = contentResolver.openInputStream(uri)
        ?: throw IllegalStateException("Cannot open input stream for URI: $uri")
    val tempFile = File(cacheDir, "upload_${System.currentTimeMillis()}.jpg")
    tempFile.outputStream().use { output ->
        inputStream.copyTo(output)
    }
    return tempFile
}

fun Context.createImageUri(): Uri {
    val imageFile = File(cacheDir, "camera_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(this, "$packageName.fileprovider", imageFile)
}

fun ComponentActivity.pickImageFromCameraOrGallery(onImagePicked: (File) -> Unit) {
    var cameraImageUri: Uri? = null
    var pendingAction: (() -> Unit)? = null

    val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.all { it.value }) {
                pendingAction?.invoke()
            }
            pendingAction = null
        }

    val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { onImagePicked(this.uriToFile(it)) }
        }

    val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                cameraImageUri?.let { onImagePicked(this.uriToFile(it)) }
            }
        }

    fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    fun openCamera() {
        val imageUri = createImageUri()
        cameraLauncher.launch(imageUri)
    }

    fun checkCameraPermissionAndOpen() {
        pendingAction = { openCamera() }
        requestPermissionLauncher.launch(arrayOf(Manifest.permission.CAMERA))
    }

    fun checkGalleryPermissionAndOpen() {
        pendingAction = { openGallery() }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.READ_MEDIA_IMAGES))
        } else {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
        }
    }

    AlertDialog.Builder(this)
        .setTitle("Select Image Source")
        .setItems(arrayOf("Camera", "Gallery")) { _, which ->
            when (which) {
                0 -> checkCameraPermissionAndOpen()
                1 -> checkGalleryPermissionAndOpen()
            }
        }
        .show()
}

fun String?.toPlainRequestBodyOrNull(): RequestBody? =
    this?.toRequestBody("text/plain".toMediaTypeOrNull())


fun String.toPlainRequestBody(): RequestBody =
    this.toRequestBody("text/plain".toMediaTypeOrNull())

fun File.toMultipartPart(fieldName: String): MultipartBody.Part {
    val mimeType = when (extension.lowercase()) {
        "jpg", "jpeg" -> "image/jpeg"
        "png" -> "image/png"
        else -> throw IllegalArgumentException("Unsupported file type: $extension")
    }
    val requestFile = asRequestBody(mimeType.toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(fieldName, name, requestFile)
}

fun isValidEmail(email: String) =
    android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

fun isValidMobile(mobile: String) =
    mobile.length in 7..15 && mobile.all { it.isDigit() }


fun EditText.preventLeadingSpaces() {
    val noLeadingSpaceFilter = InputFilter { source, start, end, dest, dstart, dend ->
        // If inserting at position 0 and the first char is a space → block it
        if (dstart == 0 && source.startsWith(" ")) {
            return@InputFilter source.trimStart()
        }
        null // keep original
    }
    this.filters = this.filters.plus(noLeadingSpaceFilter)
}

fun EditText.preventSpacesAnywhere() {
    val noSpaceFilter = InputFilter { source, start, end, dest, dstart, dend ->
        val filtered = source.subSequence(start, end).toString().replace("\\s+".toRegex(), "")
        if (filtered == source.subSequence(start, end).toString()) {
            null // no change, accept original input
        } else {
            filtered // return input with all spaces removed
        }
    }
    // Apply the filter while preserving existing filters
    this.filters = (this.filters ?: arrayOf()) + noSpaceFilter
}


fun AppCompatActivity.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Fragment.showToast(message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

}

fun EditText.togglePasswordVisibility(passwordToggle: ImageView) {
    var passwordVisible = false

    passwordToggle.setOnClickListener {
        val pass = this.text.toString()
        val newVisibility = !passwordVisible
        val transformationMethod = if (newVisibility) null else PasswordTransformationMethod.getInstance()

        passwordToggle.setImageResource(if (newVisibility)  com.google.android.material.R.drawable.avd_hide_password else com.google.android.material.R.drawable.avd_show_password)
        this.transformationMethod = transformationMethod
        this.setSelection(pass.length)
        passwordVisible = newVisibility
    }
}

fun String?.orDash(): String = if (this.isNullOrBlank()) "-" else this

fun String?.orNA(): String = if (this.isNullOrBlank()) "NA" else this


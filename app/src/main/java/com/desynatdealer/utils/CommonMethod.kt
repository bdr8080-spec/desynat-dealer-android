package com.desynatdealer.utils

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.media.MediaMuxer
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.Html
import android.text.InputFilter
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextWatcher
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.util.Size
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.desynatdealer.R
import com.desynatdealer.adapter.CountryPickerAdapter
import com.desynatdealer.adapter.ImagePreviewAdapter
import com.desynatdealer.databinding.AlertDialogLayoutBinding
import com.desynatdealer.databinding.CustomCountryPickerBinding
import com.desynatdealer.databinding.DeleteDialogLayoutBinding
import com.desynatdealer.databinding.DialogCustomCalendarBinding
import com.desynatdealer.databinding.ImagePreviewDialogBinding
import com.desynatdealer.databinding.SuccessDialogLayoutBinding
import com.desynatdealer.model.country.GetCountryResponse
import com.desynatdealer.ui.home.staffManagement.response.GetAllStaffResponse
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.nio.ByteBuffer
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

object CommonMethod {

    fun openDatePicker(
        context: Context,
        editText: EditText,
        format: String = "dd-MM-yyyy",
        pastDate: Boolean = false,
        futureDate: Boolean = true,
        onDateSelected: (String) -> Unit
    ) {
        val calendar = Calendar.getInstance(
            if (Constants.language == "ar") Locale(
                "ar",
                "EG"
            ) else Locale.getDefault()
        )
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            context, R.style.DialogTheme,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(selectedYear, selectedMonth, selectedDay)

                val sdf = SimpleDateFormat(
                    format, if (Constants.language == "ar")
                        Locale("ar", "EG")
                    else
                        Locale.getDefault()
                )
                val formattedDate = sdf.format(selectedCalendar.time)

                editText.setText(formattedDate)
                onDateSelected(formattedDate)
            },
            year,
            month,
            day
        )

        if (!pastDate) {
            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        }
        if (!futureDate) {
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        }

        datePickerDialog.show()
    }


    fun detectGestureMode(root: View, viewBottom: View) {
        root.setOnApplyWindowInsetsListener { _, insets ->
            val windowInsetsCompat = WindowInsetsCompat.toWindowInsetsCompat(insets)
            val navigationBarInsets =
                windowInsetsCompat.getInsets(WindowInsetsCompat.Type.navigationBars())

            val isUsingNavigationButtons = navigationBarInsets.bottom > 0

            if (isUsingNavigationButtons) {
                viewBottom.isVisible = true
            } else {
                viewBottom.isVisible = false
            }
            insets
        }
    }


    fun uriToFile(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "picked_image_${System.currentTimeMillis()}.jpg")
        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

    fun File.toMultiPartBody(fieldName: String): MultipartBody.Part {
        val mimeType = when (extension.lowercase()) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "pdf" -> "application/pdf"
            "mp4" -> "video/mp4"
            "mov" -> "video/mov"
            "avi" -> "video/avi"
            "mkv" -> "video/mkv"
            "gif" -> "image/gif"
            "webp" -> "image/webp"
            else -> throw IllegalArgumentException("Unsupported file type: $extension")
        }
        val requestFile = asRequestBody(mimeType.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(fieldName, name, requestFile)
    }

    fun removeAllTextSpaces(editText: AppCompatEditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {

            }

            override fun onTextChanged(
                charSequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                val text = charSequence.toString()
                val newText = text.replace(" ", "")
                if (text != newText) {
                    editText.setText(newText)
                    editText.setSelection(newText.length)
                }
            }

            override fun afterTextChanged(editable: Editable?) {

            }
        })
    }


    fun removeSpace(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {

                if (start == 0 && count > 0 && charSequence?.startsWith(" ") == true) {
                    editText.setText(charSequence.toString().trim())
                    editText.setSelection(editText.text.length)
                }
            }

            override fun afterTextChanged(editable: Editable?) {

            }
        })
    }

    fun applyArabicOnlyFilter(editText: EditText) {

        val filter = InputFilter { source, start, end, dest, dstart, dend ->

            // Block English alphabets
            val englishRegex = Regex("[a-zA-Z]")

            if (source != null && source.contains(englishRegex)) {
                return@InputFilter ""
            }

            // Block leading space
            if (dstart == 0 && source.startsWith(" ")) {
                return@InputFilter ""
            }

            return@InputFilter null
        }

        editText.filters = arrayOf(filter)
    }


    fun showAlertDialog(context: Context, description: String, onDismiss: (() -> Unit)? = null) {
        val dialog = Dialog(context, R.style.CustomDialog)
        val dialogBinding = AlertDialogLayoutBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(dialogBinding.root)

        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        dialogBinding.tvDescription.text = description

        dialogBinding.tvOkay.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            onDismiss?.invoke()
        }

        dialog.show()

    }

    fun deleteAlertDialog(
        context: Context,
        description: String,
        onResult: (action: DialogAction) -> Unit
    ) {
        val dialog = Dialog(context, R.style.CustomDialog)
        val dialogBinding = DeleteDialogLayoutBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(dialogBinding.root)

        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        dialogBinding.tvDescription.text = description

        dialogBinding.btnDelete.setOnClickListener {
            onResult(DialogAction.DELETE)
            dialog.dismiss()
        }
        dialogBinding.btnDiscard.setOnClickListener {
            onResult(DialogAction.DISCARD)
            dialog.dismiss()
        }

        dialog.show()
    }

    enum class DialogAction {
        DELETE, DISCARD
    }

    fun spannableText(
        context: Context,
        fullString: String,
        spannableString: String,
        onClick: (() -> Unit)? = null,
        color: Int = R.color.brown,
        fontRes: Int = R.font.poppins_semi_bold
    ): SpannableString {
        val textSpannable = SpannableString(fullString)

        val typeface = ResourcesCompat.getFont(context, fontRes)

        fun applySpan(target: String, click: (() -> Unit)?) {
            val startIndex = textSpannable.indexOf(target)
            if (startIndex != -1) {
                val endIndex = startIndex + target.length

                typeface?.let {
                    textSpannable.setSpan(
                        CustomTypefaceSpan(null, it),
                        startIndex, endIndex,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }

                textSpannable.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(context, color)),
                    startIndex, endIndex,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                click?.let {
                    textSpannable.setSpan(
                        object : ClickableSpan() {
                            override fun onClick(widget: View) {
                                it()
                            }

                            override fun updateDrawState(ds: TextPaint) {
                                super.updateDrawState(ds)
                                ds.isUnderlineText = false // no underline
                                ds.color = ContextCompat.getColor(context, color)
                            }
                        },
                        startIndex, endIndex,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
        }

        applySpan(spannableString, onClick)

        return textSpannable

    }

    fun commonErrorHandling(
        context: Context,
        errorCode: Int,
        errorMessage: String

    ) {
        try {
            when (errorCode) {

                401 -> {
                    clearAllData(context)
                }


            }
        } catch (e: Exception) {

        }
    }

    fun clearAllData(context: Context) {
        /*SharedPreference.get(context).accessToken = ""
        SharedPreference.get(context).language = ""
        SharedPreference.get(context).clearUser()
        Constants.latitude = 0.0
        Constants.longitude = 0.0
        val intent = Intent(context, AuthActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(intent)*/
    }

    fun formatPrice(price: Double): String {
        return if (price % 1.0 == 0.0) {
            price.toInt().toString()
        } else {
            price.toString()
        }
    }


    fun customizeChangeDateFormat(
        inputDate: String,
        inputFormat: String = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        outputFormat: String = "dd MMM,yyyy"
    ): String {
        val originalFormat: SimpleDateFormat = SimpleDateFormat(inputFormat, Locale.ENGLISH).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val targetFormat = SimpleDateFormat(outputFormat, Locale.ENGLISH).apply {
            timeZone = TimeZone.getDefault()
        }
        val date: Date
        try {

            if (Constants.language == "ar") {
                val date = originalFormat.parse(inputDate)

                var formattedDate = targetFormat.format(date!!)

                val englishMonthsMMMM = arrayOf(
                    "January", "February", "March", "April",
                    "May", "June", "July", "August",
                    "September", "October", "November", "December"
                )

                val arabicMonthsMMMM = arrayOf(
                    "يناير", "فبراير", "مارس", "أبريل",
                    "مايو", "يونيو", "يوليو", "أغسطس",
                    "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"
                )

                for (i in englishMonthsMMMM.indices) {
                    formattedDate =
                        formattedDate.replace(
                            englishMonthsMMMM[i],
                            arabicMonthsMMMM[i]
                        )
                }

                val englishMonths = arrayOf(
                    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
                )

                val arabicMonths = arrayOf(
                    "يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو",
                    "يوليو", "أغسطس", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"
                )

                for (i in englishMonths.indices) {
                    formattedDate =
                        formattedDate.replace(
                            englishMonths[i],
                            arabicMonths[i]
                        )
                }

                val englishDays = arrayOf(
                    "Sun", "Mon", "Tue", "Wed",
                    "Thu", "Fri", "Sat"
                )

                val arabicDays = arrayOf(
                    "الأحد", "الاثنين", "الثلاثاء", "الأربعاء",
                    "الخميس", "الجمعة", "السبت"
                )

                for (i in englishDays.indices) {
                    formattedDate = formattedDate.replace(
                        englishDays[i],
                        arabicDays[i]
                    )
                }

                formattedDate = formattedDate
                    .replace("AM", "ص")
                    .replace("PM", "م")
                return formattedDate
            } else {
                date = originalFormat.parse(inputDate)
                return targetFormat.format(date)
            }


        } catch (e: ParseException) {
            e.printStackTrace()
            return ""
        }
    }

    fun customizeChangeDateFormatKuwaitFromUtc(
        inputDate: String,
        inputFormat: String = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'",
        outputFormat: String = "dd MMM, yyyy hh:mm a"
    ): String {

        val originalFormat = SimpleDateFormat(inputFormat, Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC") // ✅ FIX
        }

        val targetFormat = SimpleDateFormat(outputFormat, Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("Asia/Kuwait") // ✅ Convert here
        }

        return try {
            if (Constants.language == "ar") {
                val date = originalFormat.parse(inputDate)

                var formattedDate = targetFormat.format(date!!)

                val englishMonthsMMMM = arrayOf(
                    "January", "February", "March", "April",
                    "May", "June", "July", "August",
                    "September", "October", "November", "December"
                )

                val arabicMonthsMMMM = arrayOf(
                    "يناير", "فبراير", "مارس", "أبريل",
                    "مايو", "يونيو", "يوليو", "أغسطس",
                    "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"
                )

                for (i in englishMonthsMMMM.indices) {
                    formattedDate =
                        formattedDate.replace(
                            englishMonthsMMMM[i],
                            arabicMonthsMMMM[i]
                        )
                }

                val englishMonths = arrayOf(
                    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
                )

                val arabicMonths = arrayOf(
                    "يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو",
                    "يوليو", "أغسطس", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"
                )

                for (i in englishMonths.indices) {
                    formattedDate =
                        formattedDate.replace(
                            englishMonths[i],
                            arabicMonths[i]
                        )
                }

                val englishDays = arrayOf(
                    "Sun", "Mon", "Tue", "Wed",
                    "Thu", "Fri", "Sat"
                )

                val arabicDays = arrayOf(
                    "الأحد", "الاثنين", "الثلاثاء", "الأربعاء",
                    "الخميس", "الجمعة", "السبت"
                )

                for (i in englishDays.indices) {
                    formattedDate = formattedDate.replace(
                        englishDays[i],
                        arabicDays[i]
                    )
                }

                formattedDate = formattedDate
                    .replace("AM", "ص")
                    .replace("PM", "م")
                return formattedDate
            }
            else{
                val date = originalFormat.parse(inputDate)
                targetFormat.format(date!!)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun customizeChangeDateFormatZone(
        inputDate: String,
        inputFormat: String = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        outputFormat: String = "dd MMM, yyyy"
    ): String {

        return try {
            val originalFormat = SimpleDateFormat(inputFormat, Locale.ENGLISH).apply {
                timeZone = TimeZone.getTimeZone("Asia/Kuwait")
            }
            val date = originalFormat.parse(inputDate)

            val targetFormat = SimpleDateFormat(outputFormat, Locale.ENGLISH).apply {
                timeZone = TimeZone.getDefault()
            }
            if (Constants.language == "ar") {
                val date = originalFormat.parse(inputDate)

                var formattedDate = targetFormat.format(date!!)

                val englishMonthsMMMM = arrayOf(
                    "January", "February", "March", "April",
                    "May", "June", "July", "August",
                    "September", "October", "November", "December"
                )

                val arabicMonthsMMMM = arrayOf(
                    "يناير", "فبراير", "مارس", "أبريل",
                    "مايو", "يونيو", "يوليو", "أغسطس",
                    "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"
                )

                for (i in englishMonthsMMMM.indices) {
                    formattedDate =
                        formattedDate.replace(
                            englishMonthsMMMM[i],
                            arabicMonthsMMMM[i]
                        )
                }

                val englishMonths = arrayOf(
                    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
                )

                val arabicMonths = arrayOf(
                    "يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو",
                    "يوليو", "أغسطس", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"
                )

                for (i in englishMonths.indices) {
                    formattedDate =
                        formattedDate.replace(
                            englishMonths[i],
                            arabicMonths[i]
                        )
                }

                val englishDays = arrayOf(
                    "Sun", "Mon", "Tue", "Wed",
                    "Thu", "Fri", "Sat"
                )

                val arabicDays = arrayOf(
                    "الأحد", "الاثنين", "الثلاثاء", "الأربعاء",
                    "الخميس", "الجمعة", "السبت"
                )

                for (i in englishDays.indices) {
                    formattedDate = formattedDate.replace(
                        englishDays[i],
                        arabicDays[i]
                    )
                }

                formattedDate = formattedDate
                    .replace("AM", "ص")
                    .replace("PM", "م")
                return formattedDate
            }
            else {




                targetFormat.format(date!!)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun htmlText(text: String): Spanned {
        return HtmlCompat.fromHtml(
            text,
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
    }

    fun formatDate(
        date: String,
        input: String = "dd/MM/yyyy",
        output: String = "yyyy-MM-dd"
    ): String {
        val inputFormat = SimpleDateFormat(input, Locale.getDefault())
        val outputFormat = SimpleDateFormat(output, Locale.getDefault())
        val date = inputFormat.parse(date)
        return outputFormat.format(date!!)
    }

    fun isValidPassword(password: String): Boolean {
        // At least 8 characters, 1 uppercase, 1 lowercase, 1 digit, 1 special character
        val passwordPattern =
            "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#\$%^&+=!.*_\\-]).{8,}\$"
        return password.matches(passwordPattern.toRegex())
    }

    fun showSuccessDialog(
        context: Context,
        description: String,
        button: Boolean = false,
        onDismiss: (() -> Unit)? = null
    ) {
        val dialog = Dialog(context, R.style.CustomDialog)
        val dialogBinding = SuccessDialogLayoutBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(dialogBinding.root)

        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        dialogBinding.tvDescription.text = description
        dialogBinding.tvOkay.isVisible = button

        dialogBinding.tvOkay.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            onDismiss?.invoke()
        }

        dialog.show()
        if (button == false) {
            Handler(Looper.getMainLooper()).postDelayed({
                if (dialog.isShowing) {
                    dialog.dismiss()
                }
            }, 1000)
        }
    }

    fun clearData(context: Context) {
        SharedPreference.get(context).clearUser()
        SharedPreference.get(context).accessToken = ""
        SharedPreference.get(context).isSubscription = false
    }

    fun showCountryPicker(
        context: Context,
        anchor: View,
        countryList: ArrayList<GetCountryResponse.Data>,
        isDial: Boolean = true,
        onCountrySelected: (GetCountryResponse.Data) -> Unit
    ) {
        val binding = CustomCountryPickerBinding.inflate(LayoutInflater.from(context))

        val popupWindow = PopupWindow(
            binding.root,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        popupWindow.isOutsideTouchable = true
        popupWindow.elevation = 20f
        popupWindow.showAsDropDown(anchor)

        binding.rvCountry.adapter = CountryPickerAdapter(
            context, countryList, isDial,
            object : CountryPickerAdapter.ClickOnItem {
                override fun onClickItem(position: Int) {
                    onCountrySelected(countryList[position])
                    popupWindow.dismiss()
                }
            }
        )
    }

    /*  fun getWatcher(
        position: Int,
        onChange: (pos: Int, newValue: String) -> Unit
    ): TextWatcher {

        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                onChange(position, s?.toString().orEmpty())
            }
        }
    }*/

    fun getWatcher(
        position: Int,
        isNumberField: Boolean = false,
        onChange: (pos: Int, newValue: String) -> Unit
    ): TextWatcher {

        return object : TextWatcher {

            private var isEditing = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isEditing) return
                isEditing = true

                if (isNumberField && s != null) {

                    val value = s.toString()

                    if (value == "0") {
                        s.clear()
                        isEditing = false
                        onChange(position, "")
                        return
                    }

                    if (value.length > 1 && value.startsWith("0")) {
                        val newValue = value.trimStart('0')

                        s.replace(0, s.length, newValue)

                        onChange(position, newValue)
                        isEditing = false
                        return
                    }

                    onChange(position, value)
                    isEditing = false
                } else {
                    onChange(position, s?.toString().orEmpty())
                    isEditing = false
                }
            }
        }
    }

    fun shareProduct(context: Context, name: String, desc: String, imageUrl: String) {

        val shareText = """
        Title: $name
        Description: $desc
        Image:
        $imageUrl
    """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        context.startActivity(Intent.createChooser(intent, "Share via"))
    }

    fun shareProductWithImage(context: Context, name: String, desc: String, imageUrl: String,link: String) {

        CoroutineScope(Dispatchers.IO).launch {

            try {

                val url = URL(imageUrl)
                val bitmap = BitmapFactory.decodeStream(url.openStream())

                val file = File(context.cacheDir, "shared_image.png")
                val output = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
                output.close()

                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )

                val shareText = """
                $name
                $desc
                $link
            """.trimIndent()

                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/*"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_TEXT, shareText)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                withContext(Dispatchers.Main) {
                    context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_via)))
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    fun applyGradientText(
        textView: AppCompatTextView,
        startColor: String = "#DFBF8F",
        endColor: String = "#6E4527"
    ) {

        textView.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(
                v: View?,
                left: Int, top: Int, right: Int, bottom: Int,
                oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int
            ) {
                textView.removeOnLayoutChangeListener(this)

                val width = textView.paint.measureText(textView.text.toString())

                val shader = LinearGradient(
                    0f,
                    0f,
                    width,
                    textView.textSize,
                    intArrayOf(
                        Color.parseColor(startColor),
                        Color.parseColor(endColor)
                    ),
                    null,
                    Shader.TileMode.CLAMP
                )
                textView.paint.shader = shader
            }
        })
    }

    fun isUpcoming(startDate: String): Boolean {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = sdf.parse(sdf.format(Date()))
        val start = sdf.parse(startDate)
        return start != null && start.after(today)
    }

    fun isActive(startDate: String, endDate: String): Boolean {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = sdf.parse(sdf.format(Date()))
        val start = sdf.parse(startDate)
        val end = sdf.parse(endDate)

        return start != null && end != null &&
                (today.equals(start) || today.after(start)) &&
                (today.equals(end) || today.before(end))
    }

    fun isExpired(endDate: String): Boolean {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = sdf.parse(sdf.format(Date()))
        val end = sdf.parse(endDate)
        return end != null && today.after(end)
    }

    fun formatChatTime(context: Context, inputTime: String,inputFormat: String="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"): String {
        val inputFormat = SimpleDateFormat(inputFormat, Locale.ENGLISH)
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
        val fullDateFormat = SimpleDateFormat("dd/MM/yy, hh:mm a", Locale.ENGLISH)

        val messageDate = inputFormat.parse(inputTime) ?: return inputTime
        val now = Date()

        val calendar = Calendar.getInstance()
        calendar.time = now
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val startOfToday = calendar.time

        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val startOfYesterday = calendar.time

        return when {
            messageDate.after(startOfToday) -> {
                "${context.getString(R.string.today)}, ${
                    timeFormat.format(messageDate)
                        .replace("AM", if (Constants.language=="ar")"ص" else "AM")
                        .replace("PM", if (Constants.language=="ar")"م" else "PM")
                }"
            }

            messageDate.after(startOfYesterday) -> {
                "${context.getString(R.string.yesterday)}, ${
                    timeFormat.format(messageDate).replace("AM", if (Constants.language=="ar")"ص" else "AM")
                        .replace("PM", if (Constants.language=="ar")"م" else "PM")
                }"
            }

            else -> {
                val diffMillis = now.time - messageDate.time
                val daysDiff = TimeUnit.MILLISECONDS.toDays(diffMillis)

                if (daysDiff in 2..30) {
                    "$daysDiff ${context.getString(R.string.days_ago)}, ${
                        timeFormat.format(messageDate).replace("AM", if (Constants.language=="ar")"ص" else "AM")
                            .replace("PM", if (Constants.language=="ar")"م" else "PM")
                    }"
                } else {
                    fullDateFormat.format(messageDate).replace("AM", if (Constants.language=="ar")"ص" else "AM")
                        .replace("PM", if (Constants.language=="ar")"م" else "PM")
                }
            }
        }
    }

    fun imagePreviewDialog(context: Context, imageList: ArrayList<String>) {
        val dialog = Dialog(context, R.style.CustomDialog)
        val dialogBinding = ImagePreviewDialogBinding.inflate(LayoutInflater.from(context))
        dialogBinding.root.let { dialog?.setContentView(it) }

        val window: Window = dialog?.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.CENTER
        window.attributes = params

        dialogBinding.apply {

            ivCross.setOnClickListener {
                dialog?.dismiss()
            }

            val imagePreviewAdapter = ImagePreviewAdapter(context, imageList)
            viewPager?.adapter = imagePreviewAdapter

            if (imageList.size > 1) {
                TabLayoutMediator(dotsIndicator, viewPager) { _, _ -> }.attach()

            }

        }

        dialog?.show()
    }

    fun uriToFileForStory(context: Context, uri: Uri): File? {
        val mimeType = context.contentResolver.getType(uri)

        val extension = when {
            mimeType == null -> "bin" // fallback
            mimeType.startsWith("image") -> "jpg"
            mimeType.startsWith("video") -> "mp4"
            mimeType == "application/pdf" -> "pdf"
            else -> "bin"
        }

        val file = File(context.cacheDir, "picked_file_${System.currentTimeMillis()}.$extension")

        context.contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        /* if (extension=="mp4") {
             val maxSize = 5 * 1024 * 1024 // 5 MB in bytes
             if (file.length() > maxSize) {
                 file.delete()
                 context.showToast("Upload size exceed to 5 MB")
                 return null
             }
         }*/

        return file
    }


    fun getVideoThumbnail(context: Context, uri: Uri): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                context.contentResolver.loadThumbnail(uri, Size(200, 200), null)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getVideoThumbnailFile(context: Context, uri: Uri): File? {
        val retriever = MediaMetadataRetriever()

        return try {
            retriever.setDataSource(context, uri)

            val bitmap = retriever.getFrameAtTime(0) // first frame

            bitmap?.let {
                val file = File(
                    context.cacheDir,
                    "thumb_${System.currentTimeMillis()}.jpg"
                )

                val fos = file.outputStream()
                it.compress(Bitmap.CompressFormat.JPEG, 80, fos)
                fos.flush()
                fos.close()

                file
            }

        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            retriever.release()
        }
    }

    fun trimVideoTo30SecWithMediaMuxer(context: Context, inputUri: Uri, outputFile: File) {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, inputUri)
        val durationMs =
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull()
                ?: 0
        retriever.release()

        val trimEndMs =
            minOf(30_000L, durationMs) * 1000 // convert to microseconds for MediaExtractor

        val extractor = MediaExtractor()
        extractor.setDataSource(context, inputUri, null)

        val muxer = MediaMuxer(outputFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

        val trackIndexMap = mutableMapOf<Int, Int>()
        for (i in 0 until extractor.trackCount) {
            val format = extractor.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME) ?: continue
            val trackIndex = muxer.addTrack(format)
            trackIndexMap[i] = trackIndex
        }

        muxer.start()

        val buffer = ByteBuffer.allocate(1024 * 1024)
        val bufferInfo = android.media.MediaCodec.BufferInfo()

        for (i in 0 until extractor.trackCount) {
            extractor.selectTrack(i)
            var sampleTime: Long
            do {
                bufferInfo.offset = 0
                bufferInfo.size = extractor.readSampleData(buffer, 0)
                sampleTime = extractor.sampleTime

                if (bufferInfo.size < 0 || sampleTime > trimEndMs) {
                    bufferInfo.size = 0
                    break
                }

                bufferInfo.presentationTimeUs = sampleTime
                bufferInfo.flags = extractor.sampleFlags
                muxer.writeSampleData(trackIndexMap[i]!!, buffer, bufferInfo)
                extractor.advance()
            } while (true)

            extractor.unselectTrack(i)
        }

        muxer.stop()
        muxer.release()
        extractor.release()
    }

    fun getTimeAgo(context: Context, timestamp: String): String {
        val time = ZonedDateTime.parse(timestamp, DateTimeFormatter.ISO_DATE_TIME)
        val now = ZonedDateTime.now(ZoneId.systemDefault())

        val localTime = time.withZoneSameInstant(ZoneId.systemDefault())

        val today = now.toLocalDate()
        val date = localTime.toLocalDate()

        val minutes = ChronoUnit.MINUTES.between(localTime, now)
        val hours = ChronoUnit.HOURS.between(localTime, now)

        return when {
            minutes < 1 -> context.getString(R.string.just_now)
            minutes < 60 -> "$minutes ${context.getString(R.string.min_ago)}"

            date.isEqual(today.minusDays(1)) -> {
                "${context.getString(R.string.yesterday)}, ${
                    localTime.format(
                        DateTimeFormatter.ofPattern(
                            "hh:mm a"
                        )
                    )
                }"
            }

            date.isEqual(today) -> "$hours ${context.getString(R.string.hr_ago)}"

            ChronoUnit.DAYS.between(date, today) < 7 -> {
                "${ChronoUnit.DAYS.between(date, today)} ${context.getString(R.string.days_ago)}"
            }

            else -> {
                localTime.format(DateTimeFormatter.ofPattern("dd MMM, hh:mm a"))
            }
        }
    }

    fun setHtmlText(html: String): Spanned {
        val cleanHtml = html
            .replace("\\/", "/")
            .replace("\n", "<br>")
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(cleanHtml, Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml(cleanHtml)
        }
    }

    fun getPermission(context: Context, route: String): GetAllStaffResponse.Data.Data.RoleAction? {
        val staff = SharedPreference.get(context).getStaff()

        return staff?.roleAction?.firstOrNull { role ->
            role.sellermenu.any { menu ->
                menu.route.equals(route, true)
            }
        }
    }

    fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        context.resources.updateConfiguration(
            config,
            context.resources.displayMetrics
        )
    }

    fun openCustomCalendarDialog(
        context: Context,
        onDateSelected: (String) -> Unit
    ) {

        val dialog = Dialog(context)

        val binding = DialogCustomCalendarBinding.inflate(
            LayoutInflater.from(context)
        )

        dialog.setContentView(binding.root)

        val calendar = Calendar.getInstance()

        fun updateHeader(timeInMillis: Long) {

            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.timeInMillis = timeInMillis

            val englishDay = SimpleDateFormat(
                "EEEE",
                Locale.ENGLISH
            ).format(selectedCalendar.time)

            val englishMonth = SimpleDateFormat(
                "MMMM",
                Locale.ENGLISH
            ).format(selectedCalendar.time)

            val dayArabic = when (englishDay) {
                "Sunday" -> "الأحد"
                "Monday" -> "الاثنين"
                "Tuesday" -> "الثلاثاء"
                "Wednesday" -> "الأربعاء"
                "Thursday" -> "الخميس"
                "Friday" -> "الجمعة"
                else -> "السبت"
            }

            val monthArabic = when (englishMonth) {
                "January" -> "يناير"
                "February" -> "فبراير"
                "March" -> "مارس"
                "April" -> "أبريل"
                "May" -> "مايو"
                "June" -> "يونيو"
                "July" -> "يوليو"
                "August" -> "أغسطس"
                "September" -> "سبتمبر"
                "October" -> "أكتوبر"
                "November" -> "نوفمبر"
                else -> "ديسمبر"
            }

            val dayNumber = SimpleDateFormat(
                "dd",
                Locale.ENGLISH
            ).format(selectedCalendar.time)
            if (Constants.language=="ar"){
                binding.tvHeader.text = "$dayArabic، $dayNumber $monthArabic"
            }
            else{
                binding.tvHeader.text = "${englishDay}، $dayNumber $englishMonth"

            }
        }

        updateHeader(calendar.timeInMillis)

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->

            calendar.set(year, month, dayOfMonth)

            updateHeader(calendar.timeInMillis)
        }

        binding.tvCancel.setOnClickListener {
            dialog.dismiss()
        }

        binding.tvOk.setOnClickListener {

            val formattedDate = SimpleDateFormat(
                "dd/MM/yyyy",
                Locale.ENGLISH
            ).format(calendar.time)

            onDateSelected(formattedDate)

            dialog.dismiss()
        }

        dialog.show()
    }



}

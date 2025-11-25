package com.desynatdealer.utils

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputFilter
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextWatcher
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.desynatdealer.R
import com.desynatdealer.adapter.CountryPickerAdapter
import com.desynatdealer.databinding.AlertDialogLayoutBinding
import com.desynatdealer.databinding.CustomCountryPickerBinding
import com.desynatdealer.databinding.DeleteDialogLayoutBinding
import com.desynatdealer.databinding.SuccessDialogLayoutBinding
import com.desynatdealer.model.country.GetCountryResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object CommonMethod {

    fun openDatePicker(
        context: Context,
        editText: EditText,
        format: String = "dd-MM-yyyy",
        pastDate: Boolean = false,
        futureDate: Boolean = true,
        onDateSelected: (String) -> Unit
    ) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            context, R.style.DialogTheme,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(selectedYear, selectedMonth, selectedDay)

                val sdf = SimpleDateFormat(format, Locale.getDefault())
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

    fun File.toMultiPartBody(key: String): MultipartBody.Part? {
        val requestFile = this.asRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(key, this.name, requestFile)
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


    fun removeSpace(editText: EditText){
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {

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


    fun showAlertDialog(context: Context,description:String,onDismiss: (() -> Unit)? = null) {
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

        dialogBinding.tvDescription.text=description

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

    fun formatPriceToReduceLength(amountStr: String?): String {
        if (amountStr.isNullOrBlank() || amountStr == "null") return ""
        return try {
            val amount = amountStr.toDouble()
            when {
                amount >= 1_00_00_000 -> "${amount / 1_00_00_000}Cr"
                amount >= 1_00_000 -> "${amount / 1_00_000}L"
                amount >= 1_000 -> "${amount / 1_000}K"
                else -> {
                    if (amount % 1.0 == 0.0) {
                        amount.toInt().toString()
                    } else {
                        amount.toString()
                    }
                }
            }
        } catch (e: NumberFormatException) {
            amountStr // fallback → return original string if parsing fails
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
        val targetFormat = SimpleDateFormat(outputFormat, Locale.ENGLISH)

        val date: Date
        try {
            date = originalFormat.parse(inputDate)
            return targetFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
            return ""
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

    fun clearData(context: Context){
        SharedPreference.get(context).clearUser()
        SharedPreference.get(context).accessToken = ""
        SharedPreference.get(context).isSubscription = false
    }

    fun showCountryPicker(
        context: Context,
        anchor: View,
        countryList: ArrayList<GetCountryResponse.Data>,
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

        binding.rvCountry.adapter = CountryPickerAdapter(context, countryList,
            object : CountryPickerAdapter.ClickOnItem {
                override fun onClickItem(position: Int) {
                    onCountrySelected(countryList[position])
                    popupWindow.dismiss()
                }
            }
        )
    }

    fun getWatcher(
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
    }

}

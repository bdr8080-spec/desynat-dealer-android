package com.desynatdealer.ui.auth.fragment

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.adapter.CountryPickerAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.AuthViewModel
import com.desynatdealer.databinding.CustomCountryPickerBinding
import com.desynatdealer.databinding.FragmentSignUpBinding
import com.desynatdealer.model.country.GetCountryResponse
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.CommonMethod.toMultiPartBody
import com.desynatdealer.utils.CustomTypefaceSpan
import com.desynatdealer.utils.ImagePicker
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.SharedPreference
import com.desynatdealer.utils.showToast
import com.desynatdealer.utils.togglePasswordVisibility
import java.io.File

class SignUpFragment : BaseFragment() {
    private var binding: FragmentSignUpBinding? = null
    private val viewModel: AuthViewModel by activityViewModels()
    private lateinit var imagePicker: ImagePicker
    private var profileImage: File? = null
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            imagePicker.onCameraResult(success)
        }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            imagePicker.onGalleryResult(uri)
        }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            imagePicker.onPermissionResult(granted)
        }

    private var profilePhotoUrl: String? = null
    var countryList = ArrayList<GetCountryResponse.Data>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        setCountry()
        binding?.apply {
            CommonMethod.removeAllTextSpaces(etEmail)
            CommonMethod.removeAllTextSpaces(etConfirmPassword)
            CommonMethod.removeAllTextSpaces(etPassword)
            CommonMethod.removeSpace(etFirstName)
            CommonMethod.removeSpace(etLastName)
            tvAlreadyAccount.text = CommonMethod.spannableText(
                requireActivity(), requireActivity().getString(
                    R.string.already_have_an_account_sign_in
                ), requireActivity().getString(R.string.sign_in), onClick = {
                    findNavController().popBackStack()

                })
            tvAlreadyAccount.movementMethod = LinkMovementMethod.getInstance()
            tvAlreadyAccount.highlightColor = Color.TRANSPARENT

            etPassword.togglePasswordVisibility(ivPasswordVisible)
            etConfirmPassword.togglePasswordVisibility(ivConfirmPasswordVisible)
            tvTermsPolicy.text = spannableText()
            tvTermsPolicy?.movementMethod = LinkMovementMethod.getInstance()
            tvTermsPolicy?.highlightColor = Color.TRANSPARENT

            imagePicker = ImagePicker(
                requireActivity(),
                cameraLauncher,
                galleryLauncher,
                permissionLauncher
            ) { uri ->
                uri?.let {
                    profileImage = CommonMethod.uriToFile(requireActivity(), it)
                    ivProfile.setImageURI(it)
                    clPlaceholder.isVisible = false
                    val image = profileImage?.toMultiPartBody("file")
                    viewModel.upload(image)
                }
            }
        }
    }

    override fun initControl() {
        binding?.apply {

            ivProfile.setOnClickListener {
                imagePicker.showImageSourceDialog()
            }

            tvCountryPicker.setOnClickListener {
                    CommonMethod.showCountryPicker(requireActivity(),clMobile,countryList,true){countryData->
                        tvCountryPicker.text = countryData.dialCode
                    }
            }

            btnContinue.setOnClickListener {
                if (validation()) {

                    PreviewFragment.createAccountReq = hashMapOf(
                        "profile_pic" to profilePhotoUrl.toString(),
                        "first_name" to binding?.etFirstName?.text.toString(),
                        "last_name" to binding?.etLastName?.text.toString(),
                       // "country_code" to binding?.ccp?.selectedCountryCodeWithPlus.toString(),
                        "country_code" to binding?.tvCountryPicker?.text.toString(),
                        "mobile" to binding?.etMobile?.text.toString(),
                        "email" to binding?.etEmail?.text.toString(),
                        "password" to binding?.etPassword?.text.toString(),
                        "confirm_password" to binding?.etConfirmPassword?.text.toString(),
                        "accept_terms" to "1",
                        "device_type" to "1",
                        "device_token" to "123456",
                        "lang" to SharedPreference.get(requireActivity()).language
                    )
                    viewModel.createAccount(
                        hashMapOf(
                            "profile_pic" to profilePhotoUrl.toString(),
                            "first_name" to binding?.etFirstName?.text.toString(),
                            "last_name" to binding?.etLastName?.text.toString(),
                            "country_code" to binding?.tvCountryPicker?.text.toString(),
                            "mobile" to binding?.etMobile?.text.toString(),
                            "email" to binding?.etEmail?.text.toString(),
                            "password" to binding?.etPassword?.text.toString(),
                            "confirm_password" to binding?.etConfirmPassword?.text.toString(),
                            "accept_terms" to "1",
                            "device_type" to "1",
                            "device_token" to "123456",
                            "lang" to SharedPreference.get(requireActivity()).language
                        )
                    )

                }
            }

        }
    }

    override fun observe() {

        viewModel.uploadResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())                  }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    profilePhotoUrl = result.data?.data?.url
                    viewModel.uploadResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.uploadResponse.value = null
                }

                else -> {}
            }
        }

        viewModel.createAccountResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
//                    binding.loader.setVisible()
                }

                is ApiResult.Success -> {

                    val bundle = Bundle().apply {
                        putString("countryCode", binding?.tvCountryPicker?.text.toString(),)
                        putString("mobileNumber", binding?.etMobile?.text.toString().trim())
                        putString("from", "signUp")
                        putString("id", result.data?.data?.id.toString())
                    }
                    findNavController().navigate(
                        R.id.action_signUpFragment_to_OtpFragment,
                        bundle
                    )

                    viewModel.createAccountResponse.value = null
                }

                is ApiResult.Error -> {
//                    binding.loader.setGone()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.createAccountResponse.value = null
                }

                else -> {}
            }
        }

    }

    private fun validation(): Boolean {
        binding?.apply {
            val firstName = etFirstName.text.toString().trim()
            val lastName = etLastName.text.toString().trim()
            val mobile = etMobile.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            return when {

                profilePhotoUrl == null -> {
                    showToast(getString(R.string.please_upload_profile_photo))
                    false
                }

                firstName.isEmpty() -> {
                    showToast(getString(R.string.please_enter_your_first_name))
                    false
                }

                lastName.isEmpty() -> {
                    showToast(getString(R.string.please_enter_your_last_name))
                    false
                }

                mobile.isEmpty() -> {
                    showToast(getString(R.string.please_enter_your_mobile_number))
                    false
                }

                mobile.length < 7 -> {
                    showToast(getString(R.string.number_must_be_at_least_7_digits_long))
                    false
                }

                email.isEmpty() -> {
                    showToast(getString(R.string.please_enter_your_email_address))
                    false
                }

                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    showToast(getString(R.string.please_enter_a_valid_email_address))
                    false
                }

                password.isEmpty() -> {
                    showToast(getString(R.string.please_enter_a_password))
                    false
                }

                !CommonMethod.isValidPassword(password) -> {
                    CommonMethod.showAlertDialog(requireActivity(),getString(R.string.password_must_be_at_least_8_characters_long_and_include_uppercase_lowercase_number_and_special_symbol))
                  //  showToast("Password must be at least 8 characters long and include uppercase, lowercase, number, and special symbol")
                    false
                }

                confirmPassword.isEmpty() -> {
                    showToast(getString(R.string.please_enter_confirm_password))
                    false
                }

                confirmPassword != password -> {
                    showToast(getString(R.string.new_password_and_confirm_password_didn_t_match))
                    false
                }

                !cbTermsPolicy.isChecked -> {
                    showToast(getString(R.string.please_accept_terms_policies_to_continue))
                    false
                }

                else -> true
            }
        }
        return false
    }

    private fun spannableText(
    ): SpannableString {
        val textSpannable =
            SpannableString(getString(R.string.i_accept_the_terms_conditions_and_policies))
        val typeface = ResourcesCompat.getFont(requireActivity(), R.font.poppins_regular)

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
                    ForegroundColorSpan(ContextCompat.getColor(requireActivity(), R.color.brown)),
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
                                ds.isUnderlineText = true
                                ds.color = ContextCompat.getColor(requireActivity(), R.color.brown)
                            }
                        },
                        startIndex, endIndex,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
        }

        applySpan(getString(R.string.terms_conditions)) {
            val bundle = Bundle().apply {
                putInt("type", 1)
            }
            findNavController().navigate(R.id.action_signUpFragment_to_cmsContentFragment,bundle)
        }
        applySpan(getString(R.string.policies)) {

        }

        return textSpannable
    }

    private fun setCountry() {
        val data = SharedPreference.get(requireActivity()).getCountryList()
        countryList.clear()
        data?.let { countryList.addAll(it) }
        if (!data.isNullOrEmpty()) {
            val countryCodes = data.map { it.isoCode }
            val countryCodeString = countryCodes.joinToString(",")
            binding?.ccp?.setCustomMasterCountries(countryCodeString)
        }
    }


}
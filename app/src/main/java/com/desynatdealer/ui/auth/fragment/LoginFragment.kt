package com.desynatdealer.ui.auth.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.AuthViewModel
import com.desynatdealer.databinding.CustomCountryPickerBinding
import com.desynatdealer.databinding.FragmentLoginBinding
import com.desynatdealer.databinding.ItemCountryBinding
import com.desynatdealer.model.country.GetCountryResponse
import com.desynatdealer.ui.home.HomeActivity
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.Constants
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.SharedPreference
import com.desynatdealer.utils.showToast
import com.desynatdealer.utils.togglePasswordVisibility
import com.google.firebase.messaging.FirebaseMessaging
import kotlin.text.get

class LoginFragment : BaseFragment() {

    private var binding: FragmentLoginBinding? = null

    private val viewModel: AuthViewModel by activityViewModels()
    var type = 1
    var countryList = ArrayList<GetCountryResponse.Data>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e("FCM_ERROR", "Token failed", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result
            Log.d("FCM_TOKEN", token)
            SharedPreference.get(requireActivity()).deviceToken = token
        }
        binding?.apply {
            getCountry()
            CommonMethod.removeAllTextSpaces(etEmailMobile)
            CommonMethod.removeAllTextSpaces(etPassword)
            tvCreateNewAccount.text = CommonMethod.spannableText(
                requireActivity(), requireActivity().getString(
                    R.string.don_t_have_an_account_create_new
                ), requireActivity().getString(R.string.signUp), onClick = {
                    findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)

                })
            tvCreateNewAccount.movementMethod = LinkMovementMethod.getInstance()
            tvCreateNewAccount.highlightColor = Color.TRANSPARENT

            etPassword.togglePasswordVisibility(ivPasswordVisible)

            etEmailMobile.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val input = s.toString().trim()
                    if (input.isNotEmpty()) {
                        if (input.matches(Regex("^[0-9]+$"))) {
                            type = 0
                            tvCountryPicker.isVisible = true
                            ivEmail.setImageResource(R.drawable.ic_mobile)
                            etEmailMobile.inputType = InputType.TYPE_CLASS_NUMBER
                        } else {
                            type = 1
                            tvCountryPicker.isVisible = false
                            ivEmail.setImageResource(R.drawable.ic_email)
                            etEmailMobile.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                        }
                    } else {
                        type = 1
                        tvCountryPicker.isVisible = false
                        ivEmail.setImageResource(R.drawable.ic_email)
                        etEmailMobile.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

        }
    }

    override fun initControl() {
        binding?.apply {
            tvForgotPassword.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_forgotFragment)
            }

            tvCountryPicker.setOnClickListener {
                CommonMethod.showCountryPicker(requireActivity(),clMobile,countryList,true){countryData->
                    tvCountryPicker.text = countryData.dialCode
                }
            }

            btnLogin.setOnClickListener {
                if (validation()) {
                    callLogin()
                }
            }

        }
    }

    private fun callLogin() {
        val map = HashMap<String,Any>()
        if (type==0){
          //  map["country_code"] = binding?.ccp?.selectedCountryCodeWithPlus?.trim().toString()
            map["country_code"] = binding?.tvCountryPicker?.text?.trim().toString()
        }
        map["email_mobile"] = binding?.etEmailMobile?.text?.trim().toString()
        map["password"] = binding?.etPassword?.text?.trim().toString()
        map["type"] = type
        map["lang"] = SharedPreference.get(requireActivity()).language
        map["device_type"] = Constants.DEVICE_TYPE
        map["fcm_token"] = SharedPreference.get(requireActivity()).deviceToken

        viewModel.login(map)

    }

    private fun getCountry() {

        viewModel.getCountry()

    }

    override fun observe() {

        viewModel.loginResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    if (result.data?.data?.adminVerify=="1"){
                        showToast("Your profile is under review")
                    }
                    else if (result.data?.data?.adminVerify=="2"){
                        SharedPreference.get(requireActivity()).userType = result.data.data.type
                        if (result.data?.data?.type=="1"){
                            SharedPreference.get(requireActivity()).staffToken = result.data.data.staffaccessToken
                            SharedPreference.get(requireActivity()).accessToken = result.data.data.accessToken
                        }
                        else{
                            SharedPreference.get(requireActivity()).accessToken = result.data.data.accessToken
                            SharedPreference.get(requireActivity()).staffToken = ""
                        }
                        requireActivity().startActivity(
                            Intent(
                                requireActivity(),
                                HomeActivity::class.java
                            )
                        )
                        requireActivity().finish()
                    }

                    else if (result.data?.data?.adminVerify=="3"){
                    //   showToast("Your Account is Rejected by Admin. Please create a New account")
                       showToast("Your document is rejected by admin please upload Again")
                        val data = result.data.data
                        PreviewFragment.createAccountReq = hashMapOf(
                            "profile_pic" to data.profilePic,
                            "first_name" to data.firstName,
                            "last_name" to data.lastName,
                            "country_code" to  data.countryCode,
                            "mobile" to  data.mobile,
                            "email" to  data.email
                        )
                        val bundle = Bundle().apply {
                            putString("id", data.id.toString())
                        }
                        findNavController().navigate(R.id.action_loginFragment_to_storeInfoFragment,bundle)

                    }
                    else if (result.data?.data?.adminVerify=="4"){
                        showToast("Your document is rejected by admin please upload Again")
                        val data = result.data.data
                        PreviewFragment.createAccountReq = hashMapOf(
                            "profile_pic" to data.profilePic,
                            "first_name" to data.firstName,
                            "last_name" to data.lastName,
                            "country_code" to  data.countryCode,
                            "mobile" to  data.mobile,
                            "email" to  data.email
                        )
                        val bundle = Bundle().apply {
                            putString("id", data.id.toString())
                        }
                        findNavController().navigate(R.id.action_loginFragment_to_storeInfoFragment,bundle)
                    }

                    else{
                        showToast(result.data?.message.toString())
                    }

                    viewModel.loginResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.loginResponse.value = null
                }

                else -> {}
            }
        }

        viewModel.countryResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    result.data?.data?.let {
                        SharedPreference.get(requireActivity()).saveCountryList(
                            it
                        )
                    }
                    countryList.clear()
                    result.data?.data?.let { countryList.addAll(it) }
                    viewModel.countryResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.countryResponse.value = null
                }

                else -> {}
            }
        }

    }

    private fun validation():Boolean{

        if (binding?.etEmailMobile?.text?.isEmpty() == true) {
            showToast("Please enter Mobile Number or Email Address")
            return false
        }
        else if (binding?.etEmailMobile?.text?.length!! < 7&&type==0) {
            showToast("Please enter valid mobile number")
            return false
        }
        else if (binding?.etEmailMobile?.text?.toString()?.isBlank() == true &&type==1) {
            showToast("Please enter email")
            return false
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(binding?.etEmailMobile?.text.toString()).matches() && type==1) {
            showToast("Please enter valid email")
            return false
        }
        else if (binding?.etPassword?.text?.isBlank() == true) {
            showToast("Please enter password")
            return false
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        binding?.etEmailMobile?.setText("")
        binding?.etPassword?.setText("")
    }



}
package com.desynatdealer.ui.auth.fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.AuthViewModel
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentOtpVerificationBinding
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.SharedPreference
import com.desynatdealer.utils.showToast
import kotlin.text.get

class OtpVerificationFragment : BaseFragment() {

    private var binding: FragmentOtpVerificationBinding? = null
    private val viewModel: AuthViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()
    var mobileNumber = ""
    var countryCode = ""
    var email = ""
    var from = ""
    var type = 0

    lateinit var timer: CountDownTimer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOtpVerificationBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        startTimer()
        mobileNumber = arguments?.getString("mobileNumber").toString()
        countryCode = arguments?.getString("countryCode").toString()
        from = arguments?.getString("from").toString()
        email = arguments?.getString("email").toString()
        setupOtpInputs()
        binding?.apply {
            toolbar.tvToolbar.text = getString(R.string.otp_verification)

            clView.isVisible = from == "signUp"

           if (email != "" && email != "null") {
               tvMobile.text =    email
               type = 1
            } else {
               tvMobile.text = "${countryCode} ${mobileNumber}"
               type = 0
            }
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            tvResendOtp.setOnClickListener {
                resendOtp()
            }

            btnVerify.setOnClickListener {
                val otp = if (
                    resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL
                ) {

                    binding?.etOtp6?.text.toString() +
                            binding?.etOtp5?.text.toString() +
                            binding?.etOtp4?.text.toString() +
                            binding?.etOtp3?.text.toString() +
                            binding?.etOtp2?.text.toString() +
                            binding?.etOtp1?.text.toString()

                } else {

                    binding?.etOtp1?.text.toString() +
                            binding?.etOtp2?.text.toString() +
                            binding?.etOtp3?.text.toString() +
                            binding?.etOtp4?.text.toString() +
                            binding?.etOtp5?.text.toString() +
                            binding?.etOtp6?.text.toString()
                }
                //val otp = pinView.text.toString().trim()

                when {
                    otp.isEmpty() -> {
                        showToast(getString(R.string.please_enter_otp))
                    }

                    otp.length < 6 -> {
                        showToast(getString(R.string.please_enter_a_valid_6_digit_otp))
                    }

                    else -> {

                        when(from){
                            "edit", "change" ->{
                                homeViewModel.otpVerification(
                                    hashMapOf(
                                        "id" to arguments?.getString("id").toString(),
                                        "otp" to otp)
                                )
                            }
                            else ->{
                                viewModel.verifyOtp(
                                    hashMapOf(
                                        "user_id" to arguments?.getString("id").toString(),
                                        "otp" to otp,
                                        "type" to if (from == "signUp") "0" else "1",
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

    }

    private fun resendOtp(){
        val map = HashMap<String,String>()
        if (email!=""&&email!="null"){
            map["email_mobile"] = email
        }
        else{
            map["email_mobile"] = mobileNumber
            map["country_code"] = countryCode
        }
        map["type"] = type.toString()
        viewModel.resendOtp(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
    }

    override fun observe() {

        viewModel.verifyOtpResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(getString(R.string.otp_verified_successfully))
                    when (from) {
                        "signUp" -> {
                            val bundle = Bundle().apply {
                                putString("id", arguments?.getString("id").toString())
                            }
                            findNavController().navigate(
                                R.id.action_otpFragment_to_storeInfoFragment,
                                bundle
                            )
                        }

                        "forgot" -> {
                            val bundle = Bundle().apply {
                                putString("id", arguments?.getString("id").toString())
                            }
                            findNavController().navigate(
                                R.id.action_otpFragment_to_resetPasswordFragment,
                                bundle
                            )
                        }
                    }
                    viewModel.verifyOtpResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.verifyOtpResponse.value = null
                }

                else -> {}
            }
        }

        homeViewModel.otpVerificationResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    when(from){
                        "change" -> {
                            val data = SharedPreference.get(requireActivity()).getUser()
                            if (type==1) {
                              //  data?.email = response.data.email
                            }
                            else{
                              //  data?.mobile = response.data.mobile
                              //  data?.countryCode = response.data.countryCode
                            }
                            if (data != null) {
                                SharedPreference.get(requireActivity()).saveUser(data)
                            }
                            CommonMethod.showSuccessDialog(requireActivity(),  if (from=="email") getString(
                                R.string.email_address_changed_successfully
                            ) else getString(R.string.mobile_number_changed_successfully)){
                                findNavController().popBackStack(R.id.editProfile, false)
                            }
                        }
                        "edit" -> {
                            showToast(getString(
                                R.string.otp_verified_successfully
                            ))
                            val bundle = Bundle().apply {
                                putString("from", if (type==1)"email" else "mobile")
                            }
                            findNavController().navigate(R.id.action_otpFragment_to_changeMobileFragment,bundle)
                        }
                    }

                    homeViewModel.otpVerificationResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    homeViewModel.otpVerificationResponse.value = null
                }

                else -> {}
            }
        }

        viewModel.resendOtpResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    if (type==1){
                        showToast(getString(R.string.otp_sent_to_email_address_successfully))
                    }
                    else{
                        showToast(getString(R.string.otp_sent_to_mobile_number_successfully))
                    }
                    startTimer()
                    viewModel.resendOtpResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.resendOtpResponse.value = null
                }

                else -> {}
            }
        }

    }

    private fun startTimer() {
        timer = object : CountDownTimer(90000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                binding?.tvResendOtp?.text = "${seconds} Sec"
                binding?.tvResendOtp?.isClickable = false
            }

            override fun onFinish() {
                binding?.tvResendOtp?.text = getString(R.string.resend_otp)
                binding?.tvResendOtp?.isClickable = true
            }
        }.start()
    }

    /*   private fun startTimer() {
           timer = object : CountDownTimer(60000, 1000) { // 2 minutes = 120000 milliseconds
               override fun onTick(millisUntilFinished: Long) {
                   val minutes = millisUntilFinished / 60000
                   val seconds = (millisUntilFinished % 60000) / 1000
                   binding?.tvResendOtp?.text = makeMeTwoDigits(minutes, seconds)
                   binding?.tvResendOtp?.isClickable = false
               }

               override fun onFinish() {
                   binding?.tvResendOtp?.text = getString(R.string.resend_otp)
                   binding?.tvResendOtp?.isClickable = true
               }
           }.start()
       }

       fun makeMeTwoDigits(minutes: Long, seconds: Long):String {
           val formattedMinutes = if (minutes < 10) "0$minutes" else minutes.toString()
           val formattedSeconds = if (seconds < 10) "0$seconds" else seconds.toString()

           return "${formattedMinutes}:${formattedSeconds}"

       }*/

    override fun onDestroyView() {
        super.onDestroyView()
        if (::timer.isInitialized) {
            timer.cancel()
        }
    }

    private fun setupOtpInputs() {

        val otpFields = listOf(
            binding?.etOtp1,
            binding?.etOtp2,
            binding?.etOtp3,
            binding?.etOtp4,
            binding?.etOtp5,
            binding?.etOtp6
        )

        val isArabic =
            resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL

        // First focus
        if (isArabic) {
            binding?.etOtp6?.requestFocus()
        } else {
            binding?.etOtp1?.requestFocus()
        }

        otpFields.forEachIndexed { index, editText ->

            editText?.addTextChangedListener(object : android.text.TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                }

                override fun afterTextChanged(s: android.text.Editable?) {

                    if (s?.length == 1) {

                        if (isArabic) {

                            // Move LEFT in Arabic
                            if (index > 0) {
                                otpFields[index - 1]?.requestFocus()
                            }

                        } else {

                            // Move RIGHT in English
                            if (index < otpFields.size - 1) {
                                otpFields[index + 1]?.requestFocus()
                            }
                        }
                    }
                }
            })

            editText?.setOnKeyListener { _, keyCode, event ->

                if (event.action == android.view.KeyEvent.ACTION_DOWN &&
                    keyCode == android.view.KeyEvent.KEYCODE_DEL &&
                    editText.text.isEmpty()
                ) {

                    if (isArabic) {

                        // Back move RIGHT in Arabic
                        if (index < otpFields.size - 1) {
                            otpFields[index + 1]?.requestFocus()
                        }

                    } else {

                        // Back move LEFT in English
                        if (index > 0) {
                            otpFields[index - 1]?.requestFocus()
                        }
                    }
                }

                false
            }
        }
    }

}
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
import com.desynatdealer.databinding.FragmentOtpVerificationBinding
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.showToast

class OtpVerificationFragment : BaseFragment() {

    private var binding: FragmentOtpVerificationBinding? = null
    private val viewModel: AuthViewModel by activityViewModels()
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

        binding?.apply {
            toolbar.tvToolbar.text = "OTP Verification"

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
                val otp = pinView.text.toString().trim()

                when {
                    otp.isEmpty() -> {
                        showToast("Please enter OTP")
                    }

                    otp.length < 6 -> {
                        showToast("Please enter a valid 6-digit OTP")
                    }

                    else -> {

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
                    showToast("OTP verified successfully")
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


        viewModel.resendOtpResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    if (type==1){
                        showToast("OTP sent to email address successfully ")
                    }
                    else{
                        showToast("OTP sent to mobile number successfully ")
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
}
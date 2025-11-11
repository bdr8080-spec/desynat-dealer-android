package com.desynatdealer.ui.auth.fragment

import android.os.Bundle
import android.util.Patterns
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
import com.desynatdealer.databinding.FragmentForgotPasswordBinding
import com.desynatdealer.model.country.GetCountryResponse
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.SharedPreference
import com.desynatdealer.utils.showToast

class ForgotPasswordFragment : BaseFragment() {

    private var binding: FragmentForgotPasswordBinding? = null
    private val viewModel: AuthViewModel by activityViewModels()
    private var type = "mobile"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForgotPasswordBinding.inflate(layoutInflater, container, false)
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
            toolbar.tvToolbar.text = "Forgot Password"
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            cbEmail.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    type = "email"
                    clearData()
                    cbMobile.isChecked = false
                    clMobile.isVisible = false
                    tvInputTitle.text = "Email Address"
                    etEmail.isVisible = true
                }
            }

            cbMobile.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    type = "mobile"
                    clearData()
                    cbEmail.isChecked = false
                    clMobile.isVisible = true
                    etEmail.isVisible = false
                    tvInputTitle.text = "Mobile Number"

                }
            }

            btnContinue.setOnClickListener {
                if (validation()) {
                    val map = HashMap<String, String>()
                    if (binding?.cbMobile?.isChecked == true) {
                        map["country_code"] = binding?.ccp?.selectedCountryCodeWithPlus.toString()
                        map["mobile"] = binding?.etMobile?.text.toString().trim()
                        map["type"] = "0"
                    } else {
                        map["email"] = binding?.etEmail?.text.toString().trim()
                        map["type"] = "1"
                    }

                    viewModel.forgotPassword(map)
                }
            }
        }
    }

    override fun observe() {

        viewModel.forgotPasswordResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
//                    binding.loader.setVisible()
                }

                is ApiResult.Success -> {
                    showToast("Otp Sent Successfully")
                    val bundle = Bundle().apply {
                        if (binding?.cbMobile?.isChecked == true) {
                            putString("countryCode", binding?.ccp?.selectedCountryCodeWithPlus)
                            putString("mobileNumber", binding?.etMobile?.text.toString().trim())
                        } else {
                            putString("email", binding?.etEmail?.text.toString().trim())
                        }
                        putString("from", "forgot")
                        putString("id", result.data?.data?.id?.toString())
                    }
                    findNavController().navigate(R.id.action_forgotFragment_to_otpFragment, bundle)

                    viewModel.forgotPasswordResponse.value = null
                }

                is ApiResult.Error -> {
//                    binding.loader.setGone()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.forgotPasswordResponse.value = null
                }

                else -> {}
            }
        }

    }

    private fun validation(): Boolean {
        val email = binding?.etEmail?.text.toString().trim()

        if (binding?.etMobile?.text?.isEmpty() == true && binding?.cbMobile?.isChecked == true) {
            showToast("Please enter mobile number")
            return false
        } else if (binding?.etMobile?.text?.length!! < 6 && binding?.cbMobile?.isChecked == true) {
            showToast("Mobile number must be between 6 and 15 digits ")
            return false
        } else if (email.isBlank() && binding?.cbEmail?.isChecked == true) {
            showToast("Please enter email")
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email)
                .matches() && !email.all { it.isDigit() } && binding?.cbEmail?.isChecked == true
        ) {
            showToast("Please enter valid email")
            return false
        }
        return true
    }

    private fun clearData() {
        binding?.etMobile?.setText("")
        binding?.etEmail?.setText("")
    }

    private fun setCountry() {
        val data = SharedPreference.get(requireActivity()).getCountryList()
        if (!data.isNullOrEmpty()) {
            val countryCodes = data.map { it.isoCode }
            val countryCodeString = countryCodes.joinToString(",")
            binding?.ccp?.setCustomMasterCountries(countryCodeString)
        }
    }

}
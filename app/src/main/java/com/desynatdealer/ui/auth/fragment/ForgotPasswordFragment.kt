package com.desynatdealer.ui.auth.fragment

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.data.view_model.AuthViewModel
import com.desynatdealer.databinding.FragmentForgotPasswordBinding
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordFragment : BaseFragment() {
    private var binding: FragmentForgotPasswordBinding? = null
    private val viewModel: AuthViewModel by viewModels()
    var isObserver = false
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
                        map["countryCode"] = binding?.ccp?.selectedCountryCodeWithPlus.toString()
                        map["mobileNumber"] = binding?.etMobile?.text.toString().trim()
                    }
                    else {
                        map["email"] = binding?.etEmail?.text.toString().trim()
                    }

                    val bundle = Bundle().apply {
                        if (binding?.cbMobile?.isChecked == true) {
                            putString("countryCode", binding?.ccp?.selectedCountryCodeWithPlus)
                            putString("mobileNumber", binding?.etMobile?.text.toString().trim())
                        }
                        else {
                            putString("email", binding?.etEmail?.text.toString().trim())
                        }
                        putString("from", "forgot")
                    }
                    findNavController().navigate(R.id.action_forgotFragment_to_otpFragment,bundle)
                }
            }
        }
    }

    override fun observe() {
    }

    private fun validation():Boolean{
        val email = binding?.etEmail?.text.toString().trim()

        if (binding?.etMobile?.text?.isEmpty() == true&&binding?.cbMobile?.isChecked==true) {
            showToast("Please enter mobile number")
            return false
        }
        else if (binding?.etMobile?.text?.length!! < 7&&binding?.cbMobile?.isChecked==true) {
            showToast("Please enter valid mobile number between 7 to 15 digits")
            return false
        }
        else if (email.isBlank()&&binding?.cbEmail?.isChecked==true) {
            showToast("Please enter email")
            return false
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() && !email.all { it.isDigit() }&&binding?.cbEmail?.isChecked==true) {
            showToast("Please enter valid email")
            return false
        }
        return true
    }

    private fun clearData() {
        binding?.etMobile?.setText("")
        binding?.etEmail?.setText("")
    }

}
package com.desynatdealer.ui.auth.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
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
import com.desynatdealer.databinding.FragmentLoginBinding
import com.desynatdealer.model.country.GetCountryResponse
import com.desynatdealer.ui.home.HomeActivity
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.SharedPreference
import com.desynatdealer.utils.showToast
import com.desynatdealer.utils.togglePasswordVisibility

class LoginFragment : BaseFragment() {

    private var binding: FragmentLoginBinding? = null

    private val viewModel: AuthViewModel by activityViewModels()
    var type = 1

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
                            ccp.isVisible = true
                            ivEmail.setImageResource(R.drawable.ic_mobile)
                            etEmailMobile.inputType = InputType.TYPE_CLASS_NUMBER
                        } else {
                            type = 1
                            ccp.isVisible = false
                            ivEmail.setImageResource(R.drawable.ic_email)
                            etEmailMobile.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                        }
                    } else {
                        type = 1
                        ccp.isVisible = false
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
              //  findNavController().navigate(R.id.action_loginFragment_to_forgotFragment)
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
            map["country_code"] = binding?.ccp?.selectedCountryCodeWithPlus?.trim().toString()
        }
        map["email_mobile"] = binding?.etEmailMobile?.text?.trim().toString()
        map["password"] = binding?.etPassword?.text?.trim().toString()
        map["type"] = type

        viewModel.login(map)

    }

    private fun getCountry() {

        viewModel.getCountry()

    }

    override fun observe() {

        viewModel.loginResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast("Your profile is under review")
                  /*  if (result.data?.data?.adminVerify=="0"){
                        showToast("Your profile is under review")
                    }
                    else{
                        requireActivity().startActivity(
                            Intent(
                                requireActivity(),
                                HomeActivity::class.java
                            )
                        )
                        requireActivity().finish()
                    }*/

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
                    setCountry(result.data?.data)
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

    private fun setCountry(data: List<GetCountryResponse.Data>?) {
        if (!data.isNullOrEmpty()) {
            val countryCodes = data.map { it.isoCode }
            val countryCodeString = countryCodes.joinToString(",")
            binding?.ccp?.setCustomMasterCountries(countryCodeString)
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
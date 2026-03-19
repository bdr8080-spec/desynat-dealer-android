package com.desynatdealer.ui.home.profile

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
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.model.country.GetCountryResponse
import com.desynatdealer.databinding.FragmentChangeMobileBinding
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.SharedPreference
import com.desynatdealer.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangeMobileFragment : BaseFragment() {
    private var binding: FragmentChangeMobileBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    var isObserver = false
    var from = "mobile"
    var countryList = ArrayList<GetCountryResponse.Data>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChangeMobileBinding.inflate(layoutInflater,container,false)
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
        from = arguments?.getString("from").toString()
            binding?.apply {
                CommonMethod.removeAllTextSpaces(etEmail)
                if (from=="email"){
                    toolbar.tvToolbar.text = "Change Email Address"
                    tvInputTitle.text = "Email Address"
                    tvInfo.text = "Enter your new mobile number to change"
                    clMobile.isVisible = false
                    etEmail.isVisible = true
                }
                else{
                    toolbar.tvToolbar.text = "Change Mobile Number"
                    tvInputTitle.text = "Mobile Number"
                    tvInfo.text = "Enter your new email to change"
                    clMobile.isVisible = true
                    etEmail.isVisible = false
                }
            }
    }

    override fun initControl() {

        binding?.apply {

            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack(R.id.editProfile, false)
            }

            btnContinue.setOnClickListener{
                if (validation()) {
                    changeMobileOrEmail()
                }
            }

            tvCountryPicker.setOnClickListener {
                CommonMethod.showCountryPicker(requireContext(),
                    clMobile,
                    countryList,true
                ) { selectedCountry ->
                    tvCountryPicker?.text = selectedCountry.dialCode
                }
            }

        }
    }

    override fun observe() {
        viewModel.changeNumberResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    if (from=="email") {
                        showToast("OTP sent successfully to new email address")
                    }
                    else{
                        showToast("OTP sent successfully")
                    }
                    val bundle = Bundle().apply {
                        if (from=="email"){
                            putString("email", binding?.etEmail?.text.toString())
                        } else {
                            putString("mobileNumber", binding?.etMobile?.text.toString())
                            putString("countryCode", binding?.tvCountryPicker?.text.toString())
                        }
                        putString("from", "change")
                    //    putString("userId", result.data.id)
                    }
                    findNavController().navigate(R.id.action_changeMobileEmailFragment_to_otpFragment,bundle)

                    viewModel.changeNumberResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.changeNumberResponse.value = null
                }

                else -> {}
            }
        }
    }

    private fun validation(): Boolean {
        binding?.apply {
            val mobile = etMobile.text.toString().trim()
            val email = etEmail.text.toString().trim()

            return when {
                mobile.isEmpty()&&from=="mobile" -> {
                    showToast("Please enter new mobile number")
                    false
                }

                mobile.length < 7 &&from=="mobile"-> {
                    showToast("Please enter a valid new mobile number")
                    false
                }

                email.isEmpty() &&from=="email"-> {
                    showToast("Please enter new email address")
                    false
                }

                !Patterns.EMAIL_ADDRESS.matcher(email).matches() &&from=="email"-> {
                    showToast("Please enter a valid new email address")
                    false
                }

                else -> true
            }
        }
        return false
    }


    private fun changeMobileOrEmail(){
        val map = HashMap<String,Any>()
        map["for"] = from //mobile,email
        if (from=="email"){
            map["email"] = binding?.etEmail?.text?.trim().toString()
        }
        else{
            map["country_code"] = binding?.tvCountryPicker?.text?.trim().toString()
            map["mobile"] = binding?.etMobile?.text?.trim().toString()
        }

        map["type"] = 1 //0-old,1-new
        viewModel.changeNumber(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun setCountry() {
        val data = SharedPreference.get(requireActivity()).getCountryList()
        countryList.clear()
        if (data != null) {
            countryList.addAll(data)
        }
    }

}
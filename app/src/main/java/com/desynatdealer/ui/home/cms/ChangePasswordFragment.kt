package com.desynatdealer.ui.home.cms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentChangePasswordBinding
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.SharedPreference
import com.desynatdealer.utils.showToast
import com.desynatdealer.utils.togglePasswordVisibility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordFragment : BaseFragment() {
    private var binding: FragmentChangePasswordBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    var isObserver = false
    var from = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChangePasswordBinding.inflate(layoutInflater,container,false)
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

            from = arguments?.getString("from","").toString()

            toolbar.tvToolbar.text = getString(R.string.change_password)
            CommonMethod.removeAllTextSpaces(etPassword)
            CommonMethod.removeAllTextSpaces(etConfirmPassword)
            CommonMethod.removeAllTextSpaces(etOldPassword)

            etOldPassword.togglePasswordVisibility(ivOldPasswordVisible)
            etPassword.togglePasswordVisibility(ivPasswordVisible)
            etConfirmPassword.togglePasswordVisibility(ivConfirmPasswordVisible)
        }
    }

    override fun initControl() {

        binding?.apply {

                toolbar.tvToolbar.setOnClickListener{
                    findNavController().popBackStack()
                }

            btnChange.setOnClickListener {
                    if (validation()) {
                        val map = HashMap<String, String>()
                        map["old_password"] = binding?.etOldPassword?.text.toString().trim()
                        map["new_password"] = binding?.etPassword?.text.toString().trim()
                        map["confirm_password"] = binding?.etConfirmPassword?.text.toString().trim()
                        viewModel.changePassword(map)
                        ProgressDialogUtils.getInstance().showProgress(requireActivity())
                        isObserver = true
                    }
                }
            }
    }

    override fun observe() {

        viewModel.changePasswordResponse.observe(viewLifecycleOwner){result->
            when(result){
                ApiResult.Loading -> {

                }
                is ApiResult.Success -> {
                    CommonMethod.showSuccessDialog(requireActivity(),
                        getString(R.string.password_changed_successfully)){
                        findNavController().popBackStack()
                    }
                    viewModel.changePasswordResponse.value = null
                }
                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.changePasswordResponse.value = null
                }
                else ->{

                }
            }
        }
    }

    private fun validation():Boolean{
        val oldPassword = binding?.etOldPassword?.text.toString().trim()
        val password = binding?.etPassword?.text.toString().trim()
        val confirmPassword = binding?.etConfirmPassword?.text.toString().trim()

        if (oldPassword.isEmpty()) {
            showToast(getString(R.string.please_enter_old_password))
            return false
        }
        else if (password.isEmpty()) {
            showToast(getString(R.string.please_enter_new_password))
            return false
        }
        else if (confirmPassword.isEmpty() ) {
            showToast(getString(R.string.please_enter_confirm_password))
            return false
        }
        else if (password!=confirmPassword) {
            showToast(getString(R.string.new_password_and_confirm_password_didn_t_match))
            return false
        }
        return true
    }
}
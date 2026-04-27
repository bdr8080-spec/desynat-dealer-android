package com.desynatdealer.ui.auth.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.AuthViewModel
import com.desynatdealer.databinding.FragmentResetPasswordBinding
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.SharedPreference
import com.desynatdealer.utils.showToast
import com.desynatdealer.utils.togglePasswordVisibility

class ResetPasswordFragment : BaseFragment() {

    private var binding: FragmentResetPasswordBinding? = null
    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentResetPasswordBinding.inflate(layoutInflater, container, false)
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
            toolbar.tvToolbar.text = "Reset Password"
            CommonMethod.removeAllTextSpaces(etPassword)
            CommonMethod.removeAllTextSpaces(etConfirmPassword)

            etPassword.togglePasswordVisibility(ivPasswordVisible)
            etConfirmPassword.togglePasswordVisibility(ivConfirmPasswordVisible)
        }
    }

    override fun initControl() {
        binding?.apply {

            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            btnDone.setOnClickListener {
                val token = if (SharedPreference.get(requireActivity()).userType=="1") SharedPreference.get(requireActivity()).staffToken else SharedPreference.get(requireActivity()).accessToken

                if (validation()) {
                    val map = HashMap<String, String>()
                    map["password"] = binding?.etPassword?.text.toString().trim()
                    map["confirm_password"] = binding?.etConfirmPassword?.text.toString().trim()
                    map["id"] = arguments?.getString("id").toString()

                    viewModel.changePassword(map)
                }
            }
        }

    }

    override fun observe() {

        viewModel.changePasswordResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    CommonMethod.showSuccessDialog(
                        requireActivity(),
                        getString(R.string.password_reset_successfully), false
                    ) {
                        findNavController().popBackStack(R.id.login, false)
                    }

                    viewModel.changePasswordResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.changePasswordResponse.value = null
                }

                else -> {}
            }
        }

    }

    private fun validation(): Boolean {
        val password = binding?.etPassword?.text.toString().trim()
        val confirmPassword = binding?.etConfirmPassword?.text.toString().trim()

        if (password.isEmpty()) {
            showToast("Please enter new password")
            return false
        }
         else if (!CommonMethod.isValidPassword(password)) {
            CommonMethod.showAlertDialog(requireActivity(),"Password must be at least 8 characters long and include uppercase, lowercase, number, and special symbol")
           // showToast("Password must be at least 8 characters long and include uppercase, lowercase, number, and special symbol")
            return false
        }
        else if (confirmPassword.isEmpty()) {
            showToast("Please enter confirm password")
            return false
        } else if (password != confirmPassword) {
            showToast("New password and confirm password didn't match")
            return false
        }
        return true
    }

}
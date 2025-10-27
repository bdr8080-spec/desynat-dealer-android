package com.desynatdealer.ui.auth.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.data.view_model.AuthViewModel
import com.desynatdealer.databinding.FragmentResetPasswordBinding
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.showToast
import com.desynatdealer.utils.togglePasswordVisibility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResetPasswordFragment : BaseFragment() {
    private var binding: FragmentResetPasswordBinding? = null
    private val viewModel: AuthViewModel by viewModels()
    var isObserver = false
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

            toolbar.tvToolbar.setOnClickListener{
                findNavController().popBackStack()
            }

            btnDone.setOnClickListener {
                if (validation()) {
                    val map = HashMap<String,String>()
                    map["password"] = binding?.etPassword?.text.toString().trim()
                    map["confirmPassword"] = binding?.etConfirmPassword?.text.toString().trim()
                    CommonMethod.showSuccessDialog(
                        requireActivity(),
                        getString(R.string.password_reset_successfully), false
                    ) {
                        findNavController().popBackStack(R.id.login, false)
                    }
                }
            }
        }

    }

    override fun observe() {
    }

    private fun validation():Boolean{
        val password = binding?.etPassword?.text.toString().trim()
        val confirmPassword = binding?.etConfirmPassword?.text.toString().trim()

        if (password.isEmpty()) {
            showToast("Please enter password")
            return false
        }
        else if (!CommonMethod.isValidPassword(password)) {
            showToast("Password must be at least 8 characters long and include one uppercase letter, one lowercase letter, one number, and one special character.")
            return false
        }
        else if (confirmPassword.isEmpty() ) {
            showToast("Please enter confirm password")
            return false
        }
        else if (password!=confirmPassword) {
            showToast("Password and confirm password didn't match")
            return false
        }
        return true
    }

}
package com.desynatdealer.ui.auth.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.AuthViewModel
import com.desynatdealer.databinding.FragmentLoginBinding
import com.desynatdealer.ui.home.HomeActivity
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.showToast
import com.desynatdealer.utils.togglePasswordVisibility

class LoginFragment : BaseFragment() {

    private var binding: FragmentLoginBinding? = null

    private val viewModel: AuthViewModel by activityViewModels()

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
        }
    }

    override fun initControl() {
        binding?.apply {
            tvForgotPassword.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_forgotFragment)
            }

            btnLogin.setOnClickListener {
                callLogin()
            }

        }
    }

    private fun callLogin() {

        if (binding?.etEmailMobile?.text?.isBlank() == true) {
            showToast("Please enter email or mobile number")
            return
        }

        if (binding?.etPassword?.text?.isBlank() == true) {
            showToast("Please enter password")
            return
        }

        viewModel.login(
            binding?.etEmailMobile?.text.toString(),
            binding?.etPassword?.text.toString()
        )

    }

    override fun observe() {

        viewModel.loginResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
//                    binding.loader.setVisible()
                }

                is ApiResult.Success -> {
                    requireActivity().startActivity(
                        Intent(
                            requireActivity(),
                            HomeActivity::class.java
                        )
                    )
                    requireActivity().finish()
                    viewModel.loginResponse.value = null
                }

                is ApiResult.Error -> {
//                    binding.loader.setGone()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.loginResponse.value = null
                }

                else -> {}
            }
        }

    }

}
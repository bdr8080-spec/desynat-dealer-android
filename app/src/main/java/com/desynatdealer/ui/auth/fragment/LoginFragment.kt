package com.desynatdealer.ui.auth.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.data.view_model.AuthViewModel
import com.desynatdealer.databinding.FragmentLoginBinding
import com.desynatdealer.ui.home.HomeActivity
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.togglePasswordVisibility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment() {
    private var binding: FragmentLoginBinding? = null
    private val viewModel: AuthViewModel by viewModels()
    var isObserver = false
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
            tvCreateNewAccount.text = CommonMethod.spannableText(requireActivity(),requireActivity().getString(
                R.string.don_t_have_an_account_create_new),requireActivity().getString(R.string.signUp), onClick = {
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

                    requireActivity().startActivity(
                        Intent(requireActivity(),
                            HomeActivity::class.java)
                    )
                    requireActivity().finish()


            }

        }
    }

    override fun observe() {
    }
}
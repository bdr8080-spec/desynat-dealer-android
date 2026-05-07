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
import com.desynatdealer.databinding.FragmentAddCardBinding
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddCardFragment : BaseFragment() {
    private var binding: FragmentAddCardBinding? = null
    private val viewModel: AuthViewModel by viewModels()
    var isObserver = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddCardBinding.inflate(layoutInflater, container, false)
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
            toolbar.tvToolbar.text = getString(R.string.add_card)
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            etExpiryDate.setOnClickListener {
                CommonMethod.openDatePicker(requireActivity(),etExpiryDate,"dd-MM-yyyy",false,true){

                }
            }

            btnPay.setOnClickListener {
                if (isValidate()){
                    CommonMethod.showSuccessDialog(requireActivity(),
                        getString(R.string.plan_purchased_successfully)){
                        findNavController().navigate(R.id.action_addCardFragment_to_HomeFragment)
                    }
                }
            }
        }
    }

    override fun observe() {
    }

    private fun isValidate(): Boolean {
        val cardHolderName = binding?.etCardHolderName?.text.toString().trim()
        val cardNumber = binding?.etCardNumber?.text.toString().trim()
        val expiryDate = binding?.etExpiryDate?.text.toString().trim()
        val cvv = binding?.etCvv?.text.toString().trim()

        if (cardHolderName.isBlank()) {
            showToast(getString(R.string.please_enter_card_holder_name))
            return false
        }
        else if (cardNumber.isEmpty()) {
            showToast(getString(R.string.please_enter_card_number))
            return false
        }
        else if (expiryDate.isEmpty()) {
            showToast(getString(R.string.please_select_expiry_date))
            return false
        }
        else if (cvv.isEmpty()) {
            showToast(getString(R.string.please_enter_cvv))
            return false
        }

        return true
    }



}
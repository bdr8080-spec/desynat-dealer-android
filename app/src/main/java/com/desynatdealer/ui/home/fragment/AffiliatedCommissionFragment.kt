package com.desynatdealer.ui.home.fragment

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.AddAffiliateCommissionDialogBinding
import com.desynatdealer.databinding.FragmentAffiliatedCommissionBinding
import com.desynatdealer.ui.home.response.GetSellerInfo
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.SharedPreference
import com.desynatdealer.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AffiliatedCommissionFragment : BaseFragment() {
    private var binding : FragmentAffiliatedCommissionBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    private var userInfo : GetSellerInfo.Data?=null
    var affiliateCommission= ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAffiliatedCommissionBinding.inflate(layoutInflater, container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        userInfo = SharedPreference.get(requireActivity()).getUser()
        ProgressDialogUtils.getInstance().hideProgress()
        binding?.apply {
            toolbar.tvToolbar.text = "Affiliated Commission"
            setCommissionData()

        }
    }

    override fun initControl() {
        binding?.apply {

            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            ivEdit.setOnClickListener {
                addCommissionDialog()
            }

            btnSaveChanges.setOnClickListener {

            }
        }
    }

    private fun addCommission(commission:String){
        affiliateCommission = commission
        val map = HashMap<String,Any>()
        map["affilate_commission"] = commission
        viewModel.updateAffiliateCommission(map)
    }

    override fun observe() {
        viewModel.updateAffiliateCommissionResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())                  }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                        CommonMethod.showSuccessDialog(requireActivity(), "Affiliated commission updated successfully"){
                            userInfo?.affilateCommission = affiliateCommission
                            userInfo?.let { it1 ->
                                SharedPreference.get(requireActivity()).saveUser(
                                    it1
                                )
                            }
                            setCommissionData()
                        }

                    viewModel.updateAffiliateCommissionResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.updateAffiliateCommissionResponse.value = null
                }

                else -> {}
            }
        }
    }

    private fun setCommissionData() {
        binding?.apply {
            affiliateCommission = CommonMethod.formatPrice(userInfo?.affilateCommission?.toDouble()?:0.0)
            etCommission.setText(affiliateCommission)
        }
    }

    private fun addCommissionDialog() {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = AddAffiliateCommissionDialogBinding.inflate(LayoutInflater.from(context))
        dialogBinding.root.let { dialog.setContentView(it) }

        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = params

        dialogBinding.apply {
            etAmount.setText(affiliateCommission)

            var isEditing = false
            var previousValue = ""

            etAmount.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    previousValue = s.toString()
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if (isEditing) return
                    isEditing = true

                    val value = s.toString()

                    if (value.isNotEmpty()) {
                        val num = value.toFloatOrNull() ?: 0f

                        if (num > 100) {
                            etAmount.setText(previousValue)

                            etAmount.setSelection(previousValue.length.coerceAtMost(etAmount.text?.length ?: 0))
                        }
                    }

                    isEditing = false
                }
            })

            tvToolbar.setOnClickListener {
                dialog.dismiss()
            }

            btnSubmit.setOnClickListener {
                if(isInvalid(etAmount, "Commission")) {
                    return@setOnClickListener
                }
                else{
                    addCommission(etAmount.text.toString())
                    dialog.dismiss()
                }
            }

        }

        dialog.show()
    }

    fun isInvalid(et: AppCompatEditText, fieldName: String): Boolean {
        val value = et.text.toString().trim()
        if (value.isEmpty()) {
            showToast("Please enter $fieldName")
            return true
        }
        val num = value.toDoubleOrNull() ?: 0.0
        if (num <= 0.0) {
            showToast("$fieldName should be greater than 0")
            return true
        }
        return false
    }



}
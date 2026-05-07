package com.desynatdealer.ui.auth.fragment

import android.annotation.SuppressLint
import android.os.Bundle
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
import com.desynatdealer.data.view_model.DealerRegistrationViewModel
import com.desynatdealer.databinding.FragmentPreviewBinding
import com.desynatdealer.model.storeDocumentAndBankDetails.StoreDocumentAndBankDetailsReq
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.SharedPreference
import com.desynatdealer.utils.setImage
import com.desynatdealer.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PreviewFragment : BaseFragment() {
    private val sharedViewModel: DealerRegistrationViewModel by activityViewModels()
    var userId = ""

    companion object {
        lateinit var createAccountReq: HashMap<String, String>
        lateinit var storeInformationReq: HashMap<String, String>
        lateinit var storeDocumentAndBankDetailsReq: StoreDocumentAndBankDetailsReq
    }

    private var binding: FragmentPreviewBinding? = null

    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPreviewBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        binding?.apply {
            toolbar.tvToolbar.text = getString(R.string.summary)

            userId = storeInformationReq.get("user_id").toString()
            ivProfile.setImage(createAccountReq.get("profile_pic"))
            tvFirstName.text =
                createAccountReq.get("first_name") + " " + createAccountReq.get("last_name")
            tvMobileNumber.text =
                createAccountReq.get("country_code") + "-" + createAccountReq.get("mobile")
            tvEmailAddress.text = createAccountReq.get("email")

            ivStore.setImage(storeInformationReq.get("store_image"))
            tvStoreName.text = storeInformationReq.get("store_name")
            tvAddress.text = storeInformationReq.get("store_address")
            tvStoreMobileNumber.text = storeInformationReq.get("country_code")+"-"+storeInformationReq.get("store_phone")
            tvStoreEmailAddress.text = storeInformationReq.get("store_mail_id")
            tvBio.text = storeInformationReq.get("bio")

            ownerDoc1.setImage(storeDocumentAndBankDetailsReq.ownerDocument.getOrNull(0))
            ownerDoc2.setImage(storeDocumentAndBankDetailsReq.ownerDocument.getOrNull(1))
            storeDoc1.setImage(storeDocumentAndBankDetailsReq.storeDocument.getOrNull(0))
            storeDoc2.setImage(storeDocumentAndBankDetailsReq.storeDocument.getOrNull(1))

            tvAccountType.text =
                if (storeDocumentAndBankDetailsReq.accountType == "0") getString(R.string.personal) else getString(R.string.business)

            if (storeDocumentAndBankDetailsReq.iban!=null&&storeDocumentAndBankDetailsReq.iban!="null"&&storeDocumentAndBankDetailsReq.iban!=""){
                tvIban.text = storeDocumentAndBankDetailsReq.iban
                tvIban.isVisible = true
                tvIbanTitle.isVisible = true
            }
            if (storeDocumentAndBankDetailsReq.iban!=null&&storeDocumentAndBankDetailsReq.iban!="null"&&storeDocumentAndBankDetailsReq.iban!=""){
                tvSwift.text = storeDocumentAndBankDetailsReq.swift
                tvSwift.isVisible = true
                tvSwiftTitle.isVisible = true
            }
            tvBankName.text = storeDocumentAndBankDetailsReq.bankName
            tvAccountHolderName.text = storeDocumentAndBankDetailsReq.accountHolderName
            ivBankAccount.setImage(storeDocumentAndBankDetailsReq.uploadProof)
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            btnSubmit.setOnClickListener {
                viewModel.storeInformation(storeInformationReq)
            }
        }
    }

    override fun observe() {

        viewModel.storeInformationResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    viewModel.storeDocumentAndBankDetails(storeDocumentAndBankDetailsReq)
                    viewModel.storeInformationResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.storeInformationResponse.value = null
                }

                else -> {}
            }
        }

        viewModel.storeDocumentAndBankDetailsResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    viewModel.storeDocumentAndBankDetailsResponse.value = null
                    val map = HashMap<String, String>()
                        map["user_id"] = userId
                        map["fcm_token"] = SharedPreference.get(requireActivity()).deviceToken
                    viewModel.sellerStoreSubmitRequest(map)
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.storeDocumentAndBankDetailsResponse.value = null
                }

                else -> {}
            }
        }

        viewModel.sellerStoreSubmitInfoResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    findNavController().navigate(R.id.action_previewFragment_to_underReviewFragment)
                    viewModel.sellerStoreSubmitInfoResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.sellerStoreSubmitInfoResponse.value = null
                }

                else -> {}
            }
        }


    }

}
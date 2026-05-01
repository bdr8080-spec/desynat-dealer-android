package com.desynatdealer.ui.home.chat

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.adapter.ChatAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentChatBinding
import com.desynatdealer.databinding.SendQuotationDialogBinding
import com.desynatdealer.databinding.ViewQuotationRequestDialogBinding
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.showToast
import com.desynatdealer.ui.home.chat.response.ChatMessage
import com.desynatdealer.ui.home.chat.response.GetChatListResponse
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : BaseFragment() {
    private var binding : FragmentChatBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    private var chatAdapter: ChatAdapter?=null
    private var chatList = ArrayList<ChatMessage>()
    var isObserver = false
    var from = ""
    var productId = ""
    var storeName = ""
    var productName = ""
    var chatData: GetChatListResponse.Data?=null

    private var uniqueId = ""
    var isFirstMessageLoaded = false
    private var quotationDialog: Dialog?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(layoutInflater, container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        ProgressDialogUtils.getInstance().hideProgress()
        binding?.apply {
            toolbar.tvToolbar.text = getString(R.string.chat)

            chatAdapter = ChatAdapter(requireActivity(),chatList,object : ChatAdapter.ChatClick{
                override fun onChatClick(position: Int) {

                }

            })
            rvChat.adapter = chatAdapter

        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            ivSend.setOnClickListener {
                if (etMessage.text?.trim()?.isEmpty() == true){
                    showToast(getString(R.string.please_enter_message))
                }
                else{

                }
            }

            ivAttachment.setOnClickListener {

            }

            btnSendQuotation.setOnClickListener {
                sendQuotationDialog()
            }

            ivViewRequest.setOnClickListener {
                viewQuotationRequestDialog()
            }
        }
    }

    override fun observe() {
        viewModel.modifySingleChatResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())                  }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()

                    viewModel.modifySingleChatResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.modifySingleChatResponse.value = null
                }

                else -> {}
            }
        }
    }

    private fun sendQuotationDialog(isModified:Boolean = false) {
        quotationDialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = SendQuotationDialogBinding.inflate(LayoutInflater.from(context))
        dialogBinding.root.let { quotationDialog?.setContentView(it) }

        val window: Window = quotationDialog?.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        quotationDialog?.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = params

        dialogBinding.apply {

            if (isModified){
                tvToolbar.text = getString(R.string.modify_quotation)
                tvSend.text = getString(R.string.confirm_modify)
                ivViewRequest.isVisible = true
            }
            else{
                tvToolbar.text = getString(R.string.send_quotation)
                tvSend.text = getString(R.string.confirm_send)
                ivViewRequest.isVisible = false
            }

            tvToolbar.setOnClickListener {
                quotationDialog?.dismiss()
            }

            ivViewRequest.setOnClickListener {
                viewQuotationRequestDialog()
            }

             tvSend.setOnClickListener {
                 val map = HashMap<String, Any>()
                 map["unique_id"] = uniqueId.toString()
                 map["price"] = etPerItemPrice.text?.trim().toString()
                 map["quantity"] = etShippingFee.text?.trim().toString()
                 map["tax"] = etTax.text?.trim().toString()
                 map["total_amount"] = etTotalAmount.text?.trim().toString()

                 sendQuotation(map)
                 if (isModified){
                     CommonMethod.showSuccessDialog(requireActivity(),
                         getString(R.string.modified_successfully)){
                         quotationDialog?.dismiss()
                     }
                 }
                 else {
                     CommonMethod.showSuccessDialog(
                         requireActivity(),
                         getString(R.string.request_sent_successfully)
                     ) {
                         quotationDialog?.dismiss()
                     }
                 }

            }
        }
        quotationDialog?.show()
    }

    private fun sendQuotation(map: HashMap<String, Any>) {
        viewModel.modifySingleChat(map)
    }

    private fun viewQuotationRequestDialog() {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = ViewQuotationRequestDialogBinding.inflate(LayoutInflater.from(context))
        dialogBinding.root.let { dialog?.setContentView(it) }

        val window: Window = dialog?.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.setCancelable(true)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.CENTER
        window.attributes = params

        dialogBinding.apply {

        }
        dialog?.show()
    }

    private fun setData(){
        binding?.apply {
            var request = 1 //1-new,2-send,3-approved
            when (request) {
                1 -> {
                    tvStatus.isVisible = false
                    ivViewRequest.isVisible = false
                    clCustomerRequest.isVisible = true
                    clQuotationDetail.isVisible = false
                    btnSendQuotation.text = getString(R.string.send_quotation)
                }
                2 -> {
                    tvStatus.isVisible = true
                    ivViewRequest.isVisible = false
                    clCustomerRequest.isVisible = false
                    clQuotationDetail.isVisible = true
                    tvStatus.text = getString(R.string.pending)
                    tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                    btnSendQuotation.text = getString(R.string.modify_quotation)
                }
                3 -> {
                    tvStatus.isVisible = true
                    ivViewRequest.isVisible = true
                    clCustomerRequest.isVisible = false
                    clQuotationDetail.isVisible = true
                    btnSendQuotation.isVisible = false
                    tvStatus.text = getString(R.string.accepted)
                    tvStatus.setTextColor(Color.parseColor("#68CA44"))
                }
            }
        }
    }


}
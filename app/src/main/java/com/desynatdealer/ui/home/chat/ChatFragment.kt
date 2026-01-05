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
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentChatBinding
import com.desynatdealer.databinding.SendQuotationDialogBinding
import com.desynatdealer.databinding.ViewQuotationRequestDialogBinding
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : BaseFragment() {
    private var binding : FragmentChatBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    private var chatAdapter: ChatAdapter?=null
    private var chatList = ArrayList<String>()
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
            toolbar.tvToolbar.text = "Chat"

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
                    showToast("Please enter message")
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

    }

    private fun getChat(){

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
                tvToolbar.text = "Modify Quotation"
                tvSend.text = "Confirm & Modify"
                ivViewRequest.isVisible = true
            }
            else{
                tvToolbar.text = "Send Quotation"
                tvSend.text = "Confirm & Send"
                ivViewRequest.isVisible = false
            }

            tvToolbar.setOnClickListener {
                quotationDialog?.dismiss()
            }

            ivViewRequest.setOnClickListener {
                viewQuotationRequestDialog()
            }

             tvSend.setOnClickListener {
                 if (isModified){
                     CommonMethod.showSuccessDialog(requireActivity(),"Modified successfully!"){
                         quotationDialog?.dismiss()
                     }
                 }
                 else {
                     CommonMethod.showSuccessDialog(
                         requireActivity(),
                         "Request Sent successfully!"
                     ) {
                         quotationDialog?.dismiss()
                     }
                 }

            }
        }
        quotationDialog?.show()
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
                    btnSendQuotation.text = "Send Quotation"
                }
                2 -> {
                    tvStatus.isVisible = true
                    ivViewRequest.isVisible = false
                    clCustomerRequest.isVisible = false
                    clQuotationDetail.isVisible = true
                    tvStatus.text = "Pending"
                    tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                    btnSendQuotation.text = "Modify Quotation"
                }
                3 -> {
                    tvStatus.isVisible = true
                    ivViewRequest.isVisible = true
                    clCustomerRequest.isVisible = false
                    clQuotationDetail.isVisible = true
                    btnSendQuotation.isVisible = false
                    tvStatus.text = "Accepted"
                    tvStatus.setTextColor(Color.parseColor("#68CA44"))
                }
            }
        }
    }

}
package com.desynatdealer.ui.home.cms

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.DeleteAccountDialogBinding
import com.desynatdealer.databinding.FragmentNotificationSettingBinding
import com.desynatdealer.ui.home.response.GetSellerInfo
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.SharedPreference
import com.desynatdealer.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class NotificationSettingFragment : BaseFragment() {
    private var binding: FragmentNotificationSettingBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    var isObserver = false
    var sellerInfo: GetSellerInfo.Data?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationSettingBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        sellerInfo = SharedPreference.get(requireActivity()).getUser()
        setData()
        binding?.apply {
            toolbar.tvToolbar.text = "Notificatio"
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            btnDeleteAccount.setOnClickListener {
                deleteAlertDialog()
            }

            tvInAppNotification.setOnClickListener {
                appNotificationOnOff()
            }

            tvPushNotification.setOnClickListener {
                notificationOnOff()
            }

        }

    }

    override fun observe() {
        viewModel.notificationOnOffResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    sellerInfo?.notification = result.data?.data?.notification?:0
                    sellerInfo?.let { SharedPreference.get(requireActivity()).saveUser(it) }
                    setData()
                    CommonMethod.showSuccessDialog(requireActivity(),result.data?.message.toString())
                    viewModel.notificationOnOffResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.notificationOnOffResponse.value = null
                }

                else -> {}
            }
        }

        viewModel.appNotificationOnOffResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    sellerInfo?.appNotification = result.data?.data?.appNotification?:0
                    setData()
                    sellerInfo?.let { SharedPreference.get(requireActivity()).saveUser(it) }
                    CommonMethod.showSuccessDialog(requireActivity(),result.data?.message.toString())
                    viewModel.notificationOnOffResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.appNotificationOnOffResponse.value = null
                }

                else -> {}
            }
        }
    }

    fun deleteAlertDialog(
    ) {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = DeleteAccountDialogBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(dialogBinding.root)

        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)


        dialogBinding.btnNo.setOnClickListener {
            dialog.dismiss()
        }
        dialogBinding.btnYes.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


    fun notificationOnOff(){
        val map = HashMap<String, Any>()
        viewModel.notificationOnOff(map)
    }

    fun appNotificationOnOff(){
        val map = HashMap<String, Any>()
        viewModel.appNotificationOnOff(map)
    }

    fun setData(){
        binding?.apply {
            if(sellerInfo?.appNotification==1){
                tvInAppNotification.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_in_app_notification, 0,R.drawable.switch_on, 0 )
            }
            else{
                tvInAppNotification.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_in_app_notification, 0,R.drawable.switch_off,0 )
            }

            if(sellerInfo?.notification==1){
                tvPushNotification.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_push_notification, 0,R.drawable.switch_on,0 )
            }
            else{
                tvPushNotification.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_push_notification, 0,R.drawable.switch_off,0 )
            }
        }
    }

}

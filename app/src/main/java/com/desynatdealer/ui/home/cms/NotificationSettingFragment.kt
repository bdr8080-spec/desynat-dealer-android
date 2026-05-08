package com.desynatdealer.ui.home.cms

import android.app.Dialog
import android.content.Intent
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
import com.desynatdealer.ui.auth.AuthActivity
import com.desynatdealer.ui.home.response.GetSellerInfo
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.Constants
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
            toolbar.tvToolbar.text = getString(R.string.notification)
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
                    if (result.data?.data?.notification==1){
                        CommonMethod.showSuccessDialog(requireActivity(),
                            getString(R.string.notification_is_set_to_on))
                    }
                    else{
                        CommonMethod.showSuccessDialog(requireActivity(),
                            getString(R.string.notification_is_set_to_off))
                    }
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
                    if (result.data?.data?.appNotification==1){
                        CommonMethod.showSuccessDialog(requireActivity(),
                            getString(R.string.in_app_notification_is_set_to_on))
                    }
                    else{
                        CommonMethod.showSuccessDialog(requireActivity(),
                            getString(R.string.in_app_notification_is_set_to_off))
                    }
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

        viewModel.deleteAccountResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    CommonMethod.showSuccessDialog(requireActivity(),
                        getString(R.string.account_deleted_successfully)){
                        Constants.isLogout = true
                        SharedPreference.get(requireActivity()).isSubscription = false
                        SharedPreference.get(requireActivity()).accessToken = ""
                        SharedPreference.get(requireActivity()).staffToken = ""
                        SharedPreference.get(requireActivity()).userType = ""
                        CommonMethod.clearData(requireActivity())
                        requireActivity().startActivity(
                            Intent(requireActivity(),
                                AuthActivity::class.java)
                        )
                        requireActivity().finish()
                    }
                    viewModel.deleteAccountResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.deleteAccountResponse.value = null
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
            deleteAccount()
            dialog.dismiss()
        }

        dialog.show()
    }


    fun notificationOnOff(){
        val map = HashMap<String, Any>()
        viewModel.notificationOnOff(map)
    }

    fun deleteAccount(){
        viewModel.deleteAccount()
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

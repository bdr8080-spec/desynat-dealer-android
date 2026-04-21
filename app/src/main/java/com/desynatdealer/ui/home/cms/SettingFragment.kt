package com.desynatdealer.ui.home.cms

import android.app.Dialog
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
import com.desynatdealer.databinding.FragmentSettingBinding
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class SettingFragment : BaseFragment() {
    private var binding: FragmentSettingBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    var isObserver = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(layoutInflater, container, false)
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
            toolbar.tvToolbar.text = "Settings"
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

            tvPushNotification.setOnClickListener {
                findNavController().navigate(R.id.action_settingFragment_to_notificationSettingFragment)
            }

            tvChangePassword.setOnClickListener {
                findNavController().navigate(R.id.action_settingFragment_to_changePasswordFragment)
            }

        }

    }

    override fun observe() {
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


}

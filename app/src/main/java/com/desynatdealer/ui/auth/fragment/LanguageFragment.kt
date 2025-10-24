package com.desynatdealer.ui.auth.fragment

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.databinding.FragmentLanguageBinding
import com.desynatdealer.utils.Constants
import com.desynatdealer.utils.SharedPreference
import com.desynatdealer.utils.showToast

class LanguageFragment : BaseFragment() {
    private var binding: FragmentLanguageBinding?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLanguageBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        requestPermissions()
    }

    override fun initControl() {
        binding?.apply {
            cbArabic.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    cbEnglish.isChecked = false
                    Constants.language = Constants.ARABIC
                }
                else {
                    if (!cbEnglish.isChecked) {
                        cbArabic.isChecked = true
                    }
                }
            }

            cbEnglish.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    cbArabic.isChecked = false
                    Constants.language = Constants.ENGLISH
                }
                else {
                    if (!cbArabic.isChecked) {
                        cbEnglish.isChecked = true
                    }
                }
            }

            btnContinue.setOnClickListener {
                if (Constants.language==""){
                    showToast("Please select language")
                }
                else{
                    SharedPreference.get(requireActivity()).language = Constants.language
                    findNavController().navigate(R.id.action_languageFragment_to_onBoardingFragment)
                }
            }
        }
    }

    override fun observe() {
    }

    private val requestAllPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val notificationGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissions[Manifest.permission.POST_NOTIFICATIONS] ?: false
            } else {
                true
            }
            val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

            if (notificationGranted && (fineLocationGranted || coarseLocationGranted)) {

            } else {

            }
        }

    private fun requestPermissions() {
        val permissionsList = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsList.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        requestAllPermissions.launch(permissionsList.toTypedArray())
    }

}
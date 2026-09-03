package com.desynatdealer.ui.auth.fragment

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.databinding.FragmentLanguageBinding
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.Constants
import com.desynatdealer.utils.SharedPreference
import com.desynatdealer.utils.showToast

class LanguageFragment : BaseFragment() {
    private var binding: FragmentLanguageBinding? = null
    private var from = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLanguageBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        from = arguments?.getString("from").toString()
        requestPermissions()
        binding?.apply {
            if (SharedPreference.get(requireActivity()).language != "") {
                Constants.language = SharedPreference.get(requireActivity()).language
            } else {
                Constants.language = "en"
            }
            if (Constants.language == "ar") {
                cbEnglish.isChecked = false
                cbArabic.isChecked = true
            } else {
                cbEnglish.isChecked = true
                cbArabic.isChecked = false
            }

            if (from == "profile") {
                btnContinue.text = getString(R.string.save)
                tvToolbar.isVisible = true
                tvSelectLanguage.isVisible = false
                tvInfo.isVisible = false
            } else {
                btnContinue.text = getString(R.string.continue_text)
                tvToolbar.isVisible = false
                tvSelectLanguage.isVisible = true
                tvInfo.isVisible = true
            }
        }
    }

    override fun initControl() {
        binding?.apply {

            tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            cbArabic.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    cbEnglish.isChecked = false
                    Constants.language = Constants.ARABIC
                    cbArabic.text = "العربية"
                    cbEnglish.text = "الإنجليزية"
                    tvInfo.text = "اختر اللغة واستمر للحصول على تجربة التطبيق بلغتك المفضلة."
                    tvSelectLanguage.text = "اختر اللغة"
                    btnContinue.text = "متابعة"
                    root.layoutDirection = View.LAYOUT_DIRECTION_RTL

                } else if (!cbEnglish.isChecked) {
                    cbArabic.isChecked = true
                }
            }

            cbEnglish.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    cbArabic.isChecked = false
                    Constants.language = Constants.ENGLISH
                    root.layoutDirection = View.LAYOUT_DIRECTION_LTR
                    cbArabic.text = "Arabic"
                    cbEnglish.text = "English"
                    tvInfo.text =
                        "Choose the language and continue to get the experience the app in your preferred language."
                    tvSelectLanguage.text = "Select language"
                    btnContinue.text = "Continue"


                } else if (!cbArabic.isChecked) {
                    cbEnglish.isChecked = true
                }
            }

            // Auto select Arabic
            if (from != "profile")
                cbArabic.performClick()

            btnContinue.setOnClickListener {
                if (Constants.language == "") {
                    showToast(getString(R.string.please_select_language))
                } else {
                    SharedPreference.get(requireActivity()).language = Constants.language
                    CommonMethod.setLocale(requireActivity(), Constants.language)

                    val intent = requireActivity().intent
                    intent.putExtra("open_screen", from)

                    requireActivity().finish()
                    startActivity(intent)
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
            val coarseLocationGranted =
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

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
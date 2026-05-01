package com.desynatdealer.ui.home.cms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdriver.ui.home.cms.response.GetCmsResponse
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentCmsContentBinding
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.Constants
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class CmsContentFragment : BaseFragment() {

    private var binding: FragmentCmsContentBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var type = 1 //1-terms,2-privacy,3-about,4-return,5-exchange,6-cancellation

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCmsContentBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        type = arguments?.getInt("type")?: 1
        binding?.apply {
            toolbar.tvToolbar.text =  when(type){
                1-> getString(R.string.terms_conditions)
                2-> getString(R.string.privacy_policy)
                3-> getString(R.string.about_us)
                4-> getString(R.string.return_policy)
                5-> getString(R.string.exchange_policy)
                6-> getString(R.string.cancellation_policy)
                else -> getString(R.string.terms_conditions)
            }

            getContent()

        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
               findNavController().popBackStack()
            }
        }
    }

    override fun observe() {

        viewModel.termsAndConditionResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                      ProgressDialogUtils.getInstance().hideProgress()
                    setContent(result?.data?.data)
                    viewModel.termsAndConditionResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.termsAndConditionResponse.value = null
                }

                else -> {}
            }
        }

        viewModel.privacyPolicyResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    setContent(result?.data?.data)
                    viewModel.privacyPolicyResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.privacyPolicyResponse.value = null
                }

                else -> {}
            }
        }

        viewModel.aboutUsResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    setContent(result?.data?.data)
                    viewModel.aboutUsResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.aboutUsResponse.value = null
                }

                else -> {}
            }
        }

        viewModel.cancellationPolicyResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    setContent(result?.data?.data)
                    viewModel.cancellationPolicyResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.cancellationPolicyResponse.value = null
                }

                else -> {}
            }
        }

        viewModel.exchangePolicyResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    setContent(result?.data?.data)
                    viewModel.exchangePolicyResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.exchangePolicyResponse.value = null
                }

                else -> {}
            }
        }

        viewModel.returnPolicyResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    setContent(result?.data?.data)
                    viewModel.returnPolicyResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.returnPolicyResponse.value = null
                }

                else -> {}
            }
        }

    }

    private fun setContent(content: GetCmsResponse.Data?){
        binding?.apply {
            tvContent.text = if (Constants.language=="ar") CommonMethod.setHtmlText(content?.contentArabic.toString()) else CommonMethod.setHtmlText(content?.contentEnglish.toString())
        }
    }

    fun getContent(){
        val map = HashMap<String, Any>()
        map["type"] = 2

        when(type){
            1-> viewModel.termsAndCondition(map)
            2-> viewModel.privacyPolicy(map)
            3-> viewModel.aboutUs(map)
            4-> viewModel.returnPolicy(map)
            5-> viewModel.exchangePolicy(map)
            6-> viewModel.cancellationPolicy(map)
        }

    }
}
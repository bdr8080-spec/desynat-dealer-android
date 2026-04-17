package com.desynatdealer.ui.home.cms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.adapter.FaqAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentFaqBinding
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.showToast
import com.desynatdriver.ui.home.cms.response.GetFaqResponse
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList
import kotlin.getValue

@AndroidEntryPoint
class FaqFragment : BaseFragment() {
    private var binding: FragmentFaqBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = false
    private var faqAdapter: FaqAdapter?=null
    private var faqList = ArrayList<GetFaqResponse.Data>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFaqBinding.inflate(layoutInflater, container, false)
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
            toolbar.tvToolbar.text = "FAQ's"

            faqAdapter = FaqAdapter(requireActivity(),faqList,object : FaqAdapter.ClickOnItem{
                override fun onClickItem(position: Int) {

                }

            })
            rvFaq.adapter = faqAdapter
        }

        getFaq()
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }
        }

    }

    override fun observe() {
        viewModel.faqResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    faqList.clear()
                    result?.data?.data?.let { faqList.addAll(it) }
                    faqAdapter?.notifyDataSetChanged()
                    viewModel.faqResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.faqResponse.value = null
                }

                else -> {}
            }
        }
    }

    fun getFaq(){
        val map = HashMap<String, Any>()
        map["type"] = 3
        viewModel.faq(map)
    }
}
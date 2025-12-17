package com.desynatdealer.ui.home.bannerManagement

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.adapter.BannerAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentBannerManagementBinding
import com.desynatdealer.utils.ProgressDialogUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BannerManagementFragment : BaseFragment() {
    private var binding : FragmentBannerManagementBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()

    private var search: Job? = null
    private var lastQuery: String? = null

    private var bannerList = ArrayList<String>()
    private var bannerAdapter : BannerAdapter?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBannerManagementBinding.inflate(layoutInflater, container,false)
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
            toolbar.tvToolbar.text = "Banner Management"

            etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                }

                override fun afterTextChanged(s: Editable?) {
                    val query = s.toString().trim()
                    if (query == lastQuery) return
                    lastQuery = query
                    search?.cancel()
                    search = lifecycleScope.launch {
                        delay(600)

                    }
                }
            })

            bannerList.clear()
            bannerAdapter = BannerAdapter(requireActivity(),bannerList,object :BannerAdapter.OnClick{
                override fun onItemClick(position: Int, type: Int) {
                    when(type){
                        1->{
                            findNavController().navigate(R.id.action_bannerManagementFragment_to_bannerDetailFragment)
                        }
                        2->{

                        }
                    }
                }

            })
            rvBanner.adapter = bannerAdapter


        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            ivAdd.setOnClickListener {
                findNavController().navigate(R.id.action_bannerManagementFragment_to_addBannerFragment)
            }
        }
    }

    override fun observe() {

    }

}
package com.desynatdealer.ui.home.bannerManagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentBannerDetailBinding
import com.desynatdealer.utils.ProgressDialogUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BannerDetailFragment : BaseFragment() {
    private var binding: FragmentBannerDetailBinding? = null
    private val viewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBannerDetailBinding.inflate(layoutInflater, container, false)
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
            toolbar.tvToolbar.text = "Details"


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

    }
}
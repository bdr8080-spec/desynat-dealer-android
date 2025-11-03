package com.desynatdealer.ui.auth.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.adapter.BannerViewPagerAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.databinding.FragmentOnBoardingBinding
import com.desynatdealer.utils.SharedPreference

class OnBoardingFragment : BaseFragment() {
    private var binding: FragmentOnBoardingBinding? = null
    private var bannerAdapter : BannerViewPagerAdapter? =null
    private var bannerList = ArrayList<BannerData>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOnBoardingBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {

        bannerList.clear()
        bannerList.add(BannerData(R.drawable.order_item,"Order Item","Lorem ipsum dolor sit amet consectetur. Mi ultrices diam sociis volutpat orci non. Commodo pretium urna ut augue ac amet sed."))
        bannerList.add(BannerData(R.drawable.product_delivery,"Product Delivery","Lorem ipsum dolor sit amet consectetur. Mi ultrices diam sociis volutpat orci non. Commodo pretium urna ut augue ac amet sed."))
        bannerList.add(BannerData(R.drawable.item_tracking,"Item Tracking","Lorem ipsum dolor sit amet consectetur. Mi ultrices diam sociis volutpat orci non. Commodo pretium urna ut augue ac amet sed."))
        bannerAdapter = BannerViewPagerAdapter(bannerList)
        binding?.viewPager?.adapter = bannerAdapter
        binding?.dotsIndicator?.attachTo(binding?.viewPager!!)

        binding?.viewPager?.registerOnPageChangeCallback(object :
            androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateUI(position)
            }
        })

        updateUI(0)
        
    }

    private fun updateUI(position: Int) {
        val totalPages = bannerList.size
        when (position) {
            0 -> {
                binding?.icBackArrow?.visibility = View.INVISIBLE
                binding?.icForwardArrow?.visibility = View.VISIBLE
                binding?.tvSkip?.visibility = View.VISIBLE
            }
            totalPages - 1 -> {
                binding?.icBackArrow?.visibility = View.INVISIBLE
                binding?.icForwardArrow?.visibility = View.VISIBLE
                binding?.tvSkip?.visibility = View.GONE
            }
            else -> {
                binding?.icBackArrow?.visibility = View.VISIBLE
                binding?.icForwardArrow?.visibility = View.VISIBLE
                binding?.tvSkip?.visibility = View.VISIBLE
            }
        }
    }

    override fun initControl() {
        binding?.icForwardArrow?.setOnClickListener {
            val current = binding?.viewPager?.currentItem ?: 0
            if (current < bannerList.size - 1) {
                binding?.viewPager?.currentItem = current + 1
            } else {
                SharedPreference.get(requireActivity()).isTutorial = true
                findNavController().navigate(
                    R.id.action_onBoardingFragment_to_LoginFragment,
                    null,
                    NavOptions.Builder()
                        .setPopUpTo(findNavController().graph.startDestinationId, true)
                        .build()
                )
            }
        }

        binding?.icBackArrow?.setOnClickListener {
            val current = binding?.viewPager?.currentItem ?: 0
            if (current > 0) {
                binding?.viewPager?.currentItem = current - 1
            }
        }

        binding?.tvSkip?.setOnClickListener {
            SharedPreference.get(requireActivity()).isTutorial = true
            findNavController().navigate(
                R.id.action_onBoardingFragment_to_LoginFragment,
                null,
                NavOptions.Builder()
                    .setPopUpTo(findNavController().graph.startDestinationId, true)
                    .build()
            )
        }

    }

    override fun observe() {
    }
}

data class BannerData(
    var image: Int,
    var title:String,
    var description:String
)

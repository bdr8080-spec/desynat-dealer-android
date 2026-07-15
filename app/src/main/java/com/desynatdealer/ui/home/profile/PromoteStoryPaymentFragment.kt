package com.desynatdealer.ui.home.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.databinding.FragmentPromoteStoryPaymentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PromoteStoryPaymentFragment : BaseFragment() {

    private var binding: FragmentPromoteStoryPaymentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPromoteStoryPaymentBinding.inflate(inflater, container, false)
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
            toolbar.tvToolbar.text = getString(R.string.promote_story)
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            clProfileStory.setOnClickListener {
                rbProfileStory.isChecked = true
                rbFeaturedStory.isChecked = false
                clProfileStory.setBackgroundResource(R.drawable.selected_card_bg)
                clFeaturedStory.setBackgroundResource(R.drawable.search_bg)
            }

            clFeaturedStory.setOnClickListener {
                rbFeaturedStory.isChecked = true
                rbProfileStory.isChecked = false
                clFeaturedStory.setBackgroundResource(R.drawable.selected_card_bg)
                clProfileStory.setBackgroundResource(R.drawable.search_bg)
            }

            rbProfileStory.setOnClickListener { clProfileStory.performClick() }
            rbFeaturedStory.setOnClickListener { clFeaturedStory.performClick() }

            btnSendRequest.setOnClickListener {
                // Handle send request
            }
        }
    }

    override fun observe() {
        // Observe ViewModel responses here
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
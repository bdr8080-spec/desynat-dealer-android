package com.desynatdealer.ui.auth.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.databinding.FragmentUnderReviewAccountBinding

class UnderReviewAccountFragment : BaseFragment() {
    private var binding: FragmentUnderReviewAccountBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUnderReviewAccountBinding.inflate(layoutInflater, container, false)
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


        }
    }

    override fun initControl() {
        binding?.apply {
            btnLogout.setOnClickListener {
                findNavController().popBackStack(R.id.login, false)
            }
        }
    }

    override fun observe() {
    }


}
package com.desynatdealer.ui.home.cms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatdealer.adapter.FaqAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentFaqBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList
import kotlin.getValue

@AndroidEntryPoint
class FaqFragment : BaseFragment() {
    private var binding: FragmentFaqBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = false
    private var faqAdapter: FaqAdapter?=null
    private var faqList = ArrayList<String>()

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
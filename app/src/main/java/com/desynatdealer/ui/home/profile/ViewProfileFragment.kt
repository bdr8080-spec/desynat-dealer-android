package com.desynatdealer.ui.home.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.desynatdealer.R
import com.desynatdealer.adapter.HighlightsAdapter
import com.desynatdealer.adapter.ReelAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentViewProfileBinding
import com.desynatdealer.ui.home.profile.response.GetStoryResponse
import com.desynatdealer.ui.home.profile.response.GetStoryResponseNew
import com.desynatdealer.ui.home.response.GetSellerInfo
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.SharedPreference
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewProfileFragment : BaseFragment() {
    private var binding: FragmentViewProfileBinding? = null
    private val viewModel: HomeViewModel by activityViewModels()
    private var userInfo: GetSellerInfo.Data? = null
    private var storyList = ArrayList<GetStoryResponse.Data>()
    private var reelList = ArrayList<GetStoryResponse.Data>()
    private var originalReelList = ArrayList<GetStoryResponse.Data>()
    private var reelAdapter: ReelAdapter? = null
    private var originalHighlightList = ArrayList<GetStoryResponseNew.Data>()
    private var highlightList = ArrayList<GetStoryResponseNew.Data>()
    private var highlightsAdapter: HighlightsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentViewProfileBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ProgressDialogUtils.getInstance().hideProgress()
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        userInfo = SharedPreference.get(requireActivity()).getUser()
        getStory()
        getReel()
        getHighlights()
        setData()

        binding?.apply {
            reelList.clear()
            originalReelList.clear()
            reelList.add(GetStoryResponse.Data())
            reelAdapter = ReelAdapter(requireActivity(), reelList, object : ReelAdapter.OnClick {
                override fun onItemClick(position: Int, type: Int) {
                    when (type) {
                        1 -> {
                            //  findNavController().navigate(R.id.action_viewProfileFragment_to_AddReelFragment)
                            findNavController().navigate(R.id.action_viewProfileFragment_to_AddNewReelFragment)
                        }

                        2 -> {
                            if (originalReelList.size != 0) {
                                val bundle = Bundle().apply {
                                    putString("from", "reel")
                                    putString("storyData", Gson().toJson(reelList[position]))
                                }
                                findNavController().navigate(
                                    R.id.action_viewProfileFragment_to_viewReelFragment, bundle
                                )
                            }
                        }
                    }
                }

            })
            rvReel.adapter = reelAdapter

            highlightList.clear()
            originalHighlightList.clear()
            highlightList.add(GetStoryResponseNew.Data())
            highlightsAdapter = HighlightsAdapter(
                context = requireActivity(),
                highlightList = highlightList,
                object : HighlightsAdapter.OnClick {
                    override fun onItemClick(position: Int, type: Int) {
                        when (type) {
                            1 -> {
                                if (originalHighlightList.isNotEmpty()) {
                                    val bundle = Bundle().apply {
                                        putString("from", "highlights")
                                        putString(
                                            "highlightsData", Gson().toJson(originalHighlightList)
                                        )
                                    }
                                    findNavController().navigate(
                                        R.id.action_viewProfileFragment_to_addHighlightsFragment,
                                        bundle
                                    )
                                } else {
                                    findNavController().navigate(R.id.action_viewProfileFragment_to_addHighlightsFragment)

                                }
                            }

                            2 -> {
                                if (position < highlightList.size) {
                                    val clickedHighlight = highlightList[position]
                                    if (clickedHighlight.highlights?.isNotEmpty() == true) {
                                        val bundle = Bundle().apply {
                                            putString("from", "highlights")
                                            val singleHighlightList = arrayListOf(clickedHighlight)
                                            putString(
                                                "storyData", Gson().toJson(singleHighlightList)
                                            )
                                        }
                                        findNavController().navigate(
                                            R.id.action_viewProfileFragment_to_viewStoryFragment,
                                            bundle
                                        )
                                    }
                                }
                            }
                        }
                    }

                })
            rvHighlights.adapter = highlightsAdapter
        }

    }

    private fun setData() {
        binding?.apply {
            val rating = userInfo?.storeInfo?.storeRating?.toFloatOrNull() ?: 0f
            ratingBar.rating = rating
            tvRating.text = "(${String.format("%.1f", rating)})"
            ivProfileImage.load(userInfo?.profilePic) {
                crossfade(false)
                placeholder(R.drawable.ic_user)
                error(R.drawable.ic_user)
            }

            ivStore.load(userInfo?.storeInfo?.storeImage) {
                crossfade(false)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

            ivBankAccount.load(userInfo?.sellerBankDetails?.uploadProof) {
                crossfade(false)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

            ownerDoc1.load(userInfo?.ownerDocument?.getOrNull(0)?.documentPath) {
                crossfade(false)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

            ownerDoc2.load(userInfo?.ownerDocument?.getOrNull(1)?.documentPath) {
                crossfade(false)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

            storeDoc1.load(userInfo?.storeDocument?.getOrNull(0)?.documentPath) {
                crossfade(false)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

            storeDoc2.load(userInfo?.storeDocument?.getOrNull(0)?.documentPath) {
                crossfade(false)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

            tvName.text = "${userInfo?.firstName} ${userInfo?.lastName}"
            tvMobile.text = "${userInfo?.countryCode} ${userInfo?.mobile}"
            sharedPreference.countryId = userInfo?.storeInfo?.country.toString()
            tvEmail.text = userInfo?.email
            tvStore.text = userInfo?.storeInfo?.storeName
            tvBio.text = userInfo?.storeInfo?.bio
            tvStoreAddress.text = userInfo?.storeInfo?.storeAddress
            tvStoreEmail.text = userInfo?.storeInfo?.storeMailId
            tvStoreMobile.text =
                "${userInfo?.storeInfo?.countryCode} ${userInfo?.storeInfo?.storePhone}"

            tvIban.text = userInfo?.sellerBankDetails?.iban
            tvSwift.text = userInfo?.sellerBankDetails?.swift
            tvAccountHolderName.text = userInfo?.sellerBankDetails?.accountHolderName
            tvBankName.text = userInfo?.sellerBankDetails?.bankName
            tvAccountType.text = if (userInfo?.sellerBankDetails?.accountType == "0") {
                getString(R.string.personal)
            } else {
                getString(R.string.business)
            }

        }
    }

    override fun initControl() {
        binding?.apply {

            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            ivProfileImage.setOnClickListener {
                if (storyList.isNotEmpty()) {
                    val bundle = Bundle().apply {
                        putString("from", "story")
                        putString("storyData", Gson().toJson(storyList))
                    }
                    findNavController().navigate(
                        R.id.action_viewProfileFragment_to_viewStoryFragment, bundle
                    )
                }

            }

            ivAddStory.setOnClickListener {
                findNavController().navigate(R.id.action_viewProfileFragment_to_addStoryFragment)

            }

            ivEdit.setOnClickListener {
                findNavController().navigate(R.id.action_viewProfileFragment_to_editProfileFragment)
            }

            tvReel.setOnClickListener {
                tvReel.setTextColor(requireActivity().getColor(R.color.white))
                tvBank.setTextColor(requireActivity().getColor(R.color.black))
                tvReel.setBackgroundResource(R.drawable.selected_left_tab)
                tvBank.background = null
                rvReel.isVisible = true
                llBankInfo.isVisible = false
            }

            tvBank.setOnClickListener {
                tvReel.setTextColor(requireActivity().getColor(R.color.black))
                tvBank.setTextColor(requireActivity().getColor(R.color.white))
                tvBank.setBackgroundResource(R.drawable.selected_right_tab)
                tvReel.background = null
                llBankInfo.isVisible = true
                rvReel.isVisible = false
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun observe() {
        viewModel.getStoryResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    storyList.clear()
                    result?.data?.data?.let { storyList.addAll(it) }
                    setStory()
                    viewModel.getStoryResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    //       showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.getStoryResponse.value = null
                }

                else -> {}
            }
        }

        viewModel.getReelResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    reelList.clear()
                    originalReelList.clear()
                    reelList.add(GetStoryResponse.Data())
                    result?.data?.data?.let { reelList.addAll(it) }
                    result?.data?.data?.let { originalReelList.addAll(it) }
                    reelAdapter?.notifyDataSetChanged()
                    viewModel.getReelResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    //  showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.getReelResponse.value = null
                }

                else -> {}
            }
        }

        viewModel.getHighlightsResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {

                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    highlightList.clear()
                    originalHighlightList.clear()
                    highlightList.add(GetStoryResponseNew.Data())
                    result?.data?.data?.let { highlightList.addAll(it) }
                    result?.data?.data?.let { originalHighlightList.addAll(it) }
                    highlightsAdapter?.notifyDataSetChanged()
                    viewModel.getHighlightsResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    // showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.getHighlightsResponse.value = null
                }

                else -> {}
            }
        }
    }

    private fun getStory() {
        val map = HashMap<String, Any>()

        viewModel.getStory(map)
    }

    private fun getReel() {
        val map = HashMap<String, Any>()

        viewModel.getReel(map)
    }

    private fun getHighlights() {
        val map = HashMap<String, Any>()

        viewModel.getHighlights(map)
    }

    private fun setStory() {/*  binding?.storyRing?.apply {
              segmentCount = storyList.size
              invalidate()
           //   ivHighlightStory?.isVisible = storyList.isNotEmpty()
          }*/
        binding?.apply {
            if (storyList.size != 0) {
                binding?.ivHighlightStory?.isVisible = true
            } else {
                binding?.ivHighlightStory?.isInvisible = true
            }
        }
    }

}
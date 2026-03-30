package com.desynatdealer.ui.home.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.desynatdealer.R
import com.desynatdealer.adapter.HighlightsAdapter
import com.desynatdealer.adapter.ReelAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentViewProfileBinding
import com.desynatdealer.ui.home.response.GetSellerInfo
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.SharedPreference
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewProfileFragment : BaseFragment() {
    private var binding : FragmentViewProfileBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    private var userInfo : GetSellerInfo.Data?=null
    private var reelList = ArrayList<String>()
    private var reelAdapter : ReelAdapter?=null
    private var highlightList = ArrayList<String>()
    private var highlightsAdapter : HighlightsAdapter?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentViewProfileBinding.inflate(layoutInflater, container,false)
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
        setData()

        binding?.apply {
            reelList.clear()
            reelList.addAll(arrayListOf("","","","","","",""))
            reelAdapter = ReelAdapter(requireActivity(),reelList,object: ReelAdapter.OnClick{
                override fun onItemClick(position: Int, type: Int) {
                    when(type){
                        1->{

                        }
                        2->{

                        }
                    }
                }

            })
            rvReel.adapter = reelAdapter

            highlightList.clear()
            highlightList.addAll(arrayListOf("","","","","","",""))
            highlightsAdapter = HighlightsAdapter(requireActivity(),highlightList,object: HighlightsAdapter.OnClick{
                override fun onItemClick(position: Int, type: Int) {
                    when(type){
                        1->{

                        }
                        2->{

                        }
                    }
                }

            })
            rvHighlights.adapter = highlightsAdapter
        }

    }

    private fun setData() {
        binding?.apply {
            ivProfileImage.load(userInfo?.profilePic){
                crossfade(false)
                placeholder(R.drawable.ic_user)
                error(R.drawable.ic_user)
            }

            ivStore.load(userInfo?.storeInfo?.storeImage){
                crossfade(false)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

            ivBankAccount.load(userInfo?.sellerBankDetails?.uploadProof){
                crossfade(false)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

            ownerDoc1.load(userInfo?.ownerDocument?.get(0)?.documentPath){
                crossfade(false)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

            ownerDoc2.load(userInfo?.ownerDocument?.get(1)?.documentPath){
                crossfade(false)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

            storeDoc1.load(userInfo?.storeDocument?.get(0)?.documentPath){
                crossfade(false)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

            storeDoc2.load(userInfo?.storeDocument?.get(0)?.documentPath){
                crossfade(false)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

            tvName.text = "${userInfo?.firstName} ${userInfo?.lastName}"
            tvMobile.text = "${userInfo?.countryCode} ${userInfo?.mobile}"
            tvEmail.text = userInfo?.email
            tvStore.text = userInfo?.storeInfo?.storeName
            tvBio.text = userInfo?.storeInfo?.bio
            tvStoreAddress.text = userInfo?.storeInfo?.storeAddress
            tvStoreEmail.text = userInfo?.storeInfo?.storeMailId
            tvStoreMobile.text = "${userInfo?.storeInfo?.countryCode} ${userInfo?.storeInfo?.storePhone}"

            tvIban.text = userInfo?.sellerBankDetails?.iban
            tvSwift.text = userInfo?.sellerBankDetails?.swift
            tvAccountHolderName.text = userInfo?.sellerBankDetails?.accountHolderName
            tvBankName.text = userInfo?.sellerBankDetails?.bankName
            tvAccountType.text = if(userInfo?.sellerBankDetails?.accountType=="0") {"Personal"}else {"Business"}

        }
    }

    override fun initControl() {
        binding?.apply {

            ivBack.setOnClickListener {
                findNavController().popBackStack()
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

    override fun observe() {
    }


}
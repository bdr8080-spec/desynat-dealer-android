package com.desynatdealer.ui.home.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.desynatdealer.R
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentHomeBinding
import com.desynatdealer.model.login.req.LoginResponse
import com.desynatdealer.ui.auth.AuthActivity
import com.desynatdealer.ui.home.response.GetSellerInfo
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.Constants
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.SharedPreference
import com.desynatdealer.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment() {
    private var binding : FragmentHomeBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    private var status = 1
    private var userInfo : GetSellerInfo.Data?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        binding = FragmentHomeBinding.inflate(layoutInflater, container,false)
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
        binding?.apply {
            profilePic.load(userInfo?.profilePic){
                crossfade(true)
                placeholder(R.drawable.ic_user)
                error(R.drawable.ic_user)
            }
        }
    }

    override fun initControl() {
        binding?.apply {
            tvSwitch.setOnClickListener {
                status = if (status==0) 1 else 0
                updateStatus()
            }

            ivLogout.setOnClickListener {
                logout()
            }
        }
    }

    override fun observe() {
        viewModel.logoutResponse.observe(viewLifecycleOwner){result->
            when(result){
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }
                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    Constants.isLogout = true
                    CommonMethod.clearData(requireActivity())
                    requireActivity().startActivity(
                        Intent(requireActivity(),
                            AuthActivity::class.java)
                    )
                    requireActivity().finish()
                    viewModel.logoutResponse.value = null
                }
                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.logoutResponse.value = null
                }
                else ->{

                }
            }
        }
    }

    private fun updateStatus(){
        binding?.apply {
            if(status==1){
                tvSwitch.text = "Busy/On"
                tvSwitch.setTextColor(Color.parseColor("#8B5E3C"))
                tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.switch_on,0, 0 )
            }
            else{
                tvSwitch.text = "Busy/Off"
                tvSwitch.setTextColor(Color.parseColor("#666666"))
                tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.switch_off,0,0 )

            }

        }
    }

    private fun logout() {
        viewModel.logout()
    }
}

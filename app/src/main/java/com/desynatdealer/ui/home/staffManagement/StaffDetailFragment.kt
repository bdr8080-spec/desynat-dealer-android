package com.desynatdealer.ui.home.staffManagement

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.desynatdealer.R
import com.desynatdealer.adapter.AccessModuleAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentStaffDetailBinding
import com.desynatdealer.ui.home.staffManagement.response.GetAllStaffResponse
import com.desynatdealer.ui.home.staffManagement.response.ViewStaffDetailResponse
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.showToast
import com.desynatdealer.utils.togglePasswordVisibility
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class StaffDetailFragment : BaseFragment() {
    private var binding : FragmentStaffDetailBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()

    private var staffId = ""

    private var staffDetailData: ViewStaffDetailResponse.Data?=null
    private var staffData: GetAllStaffResponse.Data?=null

    private var accessModuleAdapter: AccessModuleAdapter?=null
    private var accessModuleList = ArrayList<GetAllStaffResponse.Data.RoleAction>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStaffDetailBinding.inflate(layoutInflater, container,false)
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
        staffId = arguments?.getString("staffId").toString()
        val itemJson = arguments?.getString("staffData")
        if (!itemJson.isNullOrEmpty()) {
            staffData = Gson().fromJson(itemJson, GetAllStaffResponse.Data::class.java)
            setData()
        }
        binding?.apply {

            tvPasswordValue.togglePasswordVisibility(ivPasswordVisible)

            accessModuleAdapter = AccessModuleAdapter(requireActivity(),accessModuleList,object :AccessModuleAdapter.OnClick{
                override fun ClickOnItem(position: Int, type: String) {

                }

            })
            rvAccessModule.adapter = accessModuleAdapter

        }
    }

    override fun initControl() {
        binding?.apply {

            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            ivEdit.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("staffData", Gson().toJson(staffData))
                }
               findNavController().navigate(R.id.action_staffDetailFragment_to_addStaffFragment,bundle)
            }

            tvSwitch.setOnClickListener {
                updateStaffStatus()
            }

            ivDelete.setOnClickListener {
                CommonMethod.deleteAlertDialog(
                    requireActivity(),
                    "Are you sure you want to delete staff?"
                ) { action ->
                    when (action) {
                        CommonMethod.DialogAction.DELETE -> {
                            deleteStaff()
                        }

                        CommonMethod.DialogAction.DISCARD -> {

                        }
                    }
                }
            }

        }
    }

    override fun observe() {
        viewModel.getStaffDetailResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    staffDetailData = result.data?.data?.get(0)
                    setData()
                    viewModel.getStaffDetailResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.getStaffDetailResponse.value = null
                }

                else -> {}
            }
        }

        viewModel.updateStaffStatusResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    CommonMethod.showSuccessDialog(requireActivity(),result?.data?.message.toString())
                   // staffDetailData = result.data?.data?.get(0)
                    updateStatus()
                    viewModel.updateStaffStatusResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.updateStaffStatusResponse.value = null
                }

                else -> {}
            }
        }

        viewModel.deleteStaffResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    CommonMethod.showSuccessDialog(requireActivity(),"Staff deleted successfully !"){
                        findNavController().popBackStack()
                    }
                    viewModel.deleteStaffResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.deleteStaffResponse.value = null
                }

                else -> {}
            }
        }
    }

    private fun getStaffDetail(){
        val map = HashMap<String, Any>()
        map["id"] = staffId
        viewModel.getStaffDetail(map)
    }

    private fun deleteStaff(){
        val map = HashMap<String, Any>()
        map["id"] = staffId
        viewModel.deleteStaff(map)
    }

    private fun updateStaffStatus(){
        val map = HashMap<String, Any>()
        map["id"] = staffId
        viewModel.updateStaffStatus(map)
    }

    private fun setData(){
        binding?.apply {
            tvName.text = staffData?.firstName
         //   tvName.text = staffData?.firstName+" "+staffData?.lastName
            tvEmail.text = staffData?.email
            tvMobile.text = staffData?.countryCode+""+staffData?.mobile
            tvRole.text = staffData?.sellerCategory?.nameEn
            tvPasswordValue.text = staffData?.password
            Glide.with(requireActivity()).load(staffData?.profilePic).placeholder(R.drawable.ic_user).into(ivProfile)

            accessModuleList.clear()
            staffData?.roleAction?.let { accessModuleList.addAll(it) }
            accessModuleAdapter?.notifyDataSetChanged()

            updateStatus()
        }
    }

    private fun updateStatus(){
        binding?.apply {
          /*  if(staffData.status==1){
                tvSwitch.text = "Active"
                tvSwitch.setTextColor(Color.parseColor("#1A900D"))
                tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.switch_on,0, 0 )
            }
            else{
                tvSwitch.text = "Inactive"
                tvSwitch.setTextColor(Color.parseColor("#875C3A"))
                tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.switch_off,0,0 )
            }*/
        }
    }

}
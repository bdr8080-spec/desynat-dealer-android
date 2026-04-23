package com.desynatdealer.ui.home.staffManagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.adapter.StaffAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentStaffManagementBinding
import com.desynatdealer.ui.home.staffManagement.response.GetAllStaffResponse
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class StaffManagementFragment : BaseFragment() {
    private var binding : FragmentStaffManagementBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    private var staffAdapter: StaffAdapter?=null
    private var staffList = ArrayList<GetAllStaffResponse.Data>()
    private var selectedPos = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStaffManagementBinding.inflate(layoutInflater, container,false)
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
            toolbar.tvToolbar.text = "Staff Management"

            staffAdapter = StaffAdapter(requireActivity(),staffList,object : StaffAdapter.OnClick{
                override fun ClickOnItem(position: Int, type: String) {
                    when(type){
                        "1"->{
                            val bundle = Bundle().apply {
                                putString("staffData", Gson().toJson(staffList[position]))
                                putString("staffId", staffList[position].id.toString())
                            }
                            findNavController().navigate(R.id.action_staffManagementFragment_to_staffDetailFragment,bundle)
                        }
                        "2"->{
                            val bundle = Bundle().apply {
                                putString("staffData", Gson().toJson(staffList[position]))
                            }
                            findNavController().navigate(R.id.action_staffManagementFragment_to_addStaffFragment,bundle)
                        }
                        "3"->{
                            selectedPos = position
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
                        "4"->{
                            selectedPos = position
                            updateStaffStatus()
                        }
                    }
                }

            })
            rvStaff.adapter = staffAdapter

        }

        getAllStaff()
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            ivAdd.setOnClickListener {
                findNavController().navigate(R.id.action_staffManagementFragment_to_addStaffFragment)
            }
        }

    }

    override fun observe() {

        viewModel.getAllStaffResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    staffList.clear()
                    result?.data?.data?.let { staffList.addAll(it) }
                    staffAdapter?.notifyDataSetChanged()
                    checkData()

                    viewModel.getAllStaffResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.getAllStaffResponse.value = null
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
                    selectedPos = -1
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
                    staffList.removeAt(selectedPos)
                    staffAdapter?.notifyDataSetChanged()
                    selectedPos = -1
                    CommonMethod.showSuccessDialog(requireActivity(),"Staff deleted successfully !"){

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

    private fun checkData() {
        binding?.tvNoDataFound?.isVisible = staffList.isEmpty()
    }

    private fun getAllStaff(){
        val map = HashMap<String, Any>()
        viewModel.getAllStaff(map)
    }

    private fun deleteStaff(){
        val map = HashMap<String, Any>()
        map["id"] = staffList[selectedPos].id
        viewModel.deleteStaff(map)
    }

    private fun updateStaffStatus(){
        val map = HashMap<String, Any>()
        map["id"] = staffList[selectedPos].id
        viewModel.updateStaffStatus(map)
    }

}
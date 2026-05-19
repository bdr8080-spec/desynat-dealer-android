package com.desynatdealer.ui.home.staffManagement

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.adapter.StaffAdapter
import com.desynatdealer.adapter.StaffRoleAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentStaffManagementBinding
import com.desynatdealer.databinding.StaffFilterDialogBinding
import com.desynatdealer.model.country.GetCountryResponse
import com.desynatdealer.ui.home.staffManagement.response.GetAllStaffResponse
import com.desynatdealer.ui.home.staffManagement.response.GetStaffCategoryResponse
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.SharedPreference
import com.desynatdealer.utils.openCustomDatePicker
import com.desynatdealer.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue
import kotlin.toString

@AndroidEntryPoint
class StaffManagementFragment : BaseFragment() {
    private var binding : FragmentStaffManagementBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    private var staffAdapter: StaffAdapter?=null
    private var staffList = ArrayList<GetAllStaffResponse.Data.Data>()
    private var selectedPos = -1

    private var fromTimestamp: Long? = null
    private var toTimestamp: Long? = null
    private var fromDateText: String? = null
    private var toDateText: String? = null
    private var isFromSwipe = false
    var roleFilter:String?=null
    var statusFilter:String?=null


    private var staffRoleAdapter: StaffRoleAdapter?=null
    private var staffRoleList = ArrayList<GetStaffCategoryResponse.Data>()
    var canAction = true
    var canView = true


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
            toolbar.tvToolbar.text = getString(R.string.staff_management)
            if (SharedPreference.get(requireActivity()).userType == "1") {
                canAction = CommonMethod.getPermission(requireActivity(),"seller.staff-management")?.canAction == 1
                canView = CommonMethod.getPermission(requireActivity(),"seller.staff-management")?.canView == 1
                ivAdd.isVisible = canAction
            }
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
                                getString(R.string.are_you_sure_you_want_to_delete_staff)
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
        viewModel.getStaffRole()
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            ivAdd.setOnClickListener {
                findNavController().navigate(R.id.action_staffManagementFragment_to_addStaffFragment)
            }

            ivFilter.setOnClickListener {
                staffFilterDialog()
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
                    result?.data?.data?.data?.let { staffList.addAll(it) }
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
                    staffList[selectedPos].isBlock = result.data?.data?.isBlock?:0
                    staffAdapter?.notifyItemChanged(selectedPos)
                    if (result.data?.data?.isBlock==1){
                        CommonMethod.showSuccessDialog(requireActivity(),
                            getString(R.string.staff_member_block_successfully))
                    }
                    else{
                        CommonMethod.showSuccessDialog(requireActivity(),
                            getString(R.string.staff_member_unblock_successfully))
                    }
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

        viewModel.getStaffRoleResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    staffRoleList.clear()
                    result?.data?.data?.let { staffRoleList.addAll(it) }
                    staffRoleAdapter?.notifyDataSetChanged()
                    viewModel.getStaffRoleResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.getStaffRoleResponse.value = null
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
                    CommonMethod.showSuccessDialog(requireActivity(),
                        getString(R.string.staff_deleted_successfully)){

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
        if (fromDateText!=null){
            map["from"] = fromDateText.toString()
        }
        if (toDateText!=null){
            map["to"] = toDateText.toString()
        }
        if (statusFilter!=null){
            map["status_filter"] = statusFilter.toString()
        }
        if (roleFilter!=null){
            map["role_filter"] = roleFilter.toString()
        }

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

    private fun staffFilterDialog() {
        var tempRoleFilter:String? = null
        var tempStatusFilter:String? = null
        var tempFromTimestamp: Long? = null
        var tempToTimestamp: Long? = null
        var tempFromDateText: String? = null
        var tempToDateText: String? = null
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = StaffFilterDialogBinding.inflate(LayoutInflater.from(context))
        dialogBinding.root.let { dialog.setContentView(it) }

        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = params

        dialogBinding.apply {
            staffRoleAdapter = StaffRoleAdapter(requireActivity(),staffRoleList,object :
                StaffRoleAdapter.OnClick{
                override fun ClickOnItem(position: Int, type: String) {
                    tempRoleFilter = staffRoleList[position].id
                }

            })
            rvRole.adapter = staffRoleAdapter

            if (roleFilter!=null){
                tempRoleFilter = roleFilter
                for (i in 0 until staffRoleList.size){
                    if (staffRoleList[i].id==tempRoleFilter){
                        staffRoleAdapter?.setSelected(i)
                    }
                }
            }

            tempFromTimestamp = fromTimestamp
            tempToTimestamp = toTimestamp
            tempFromDateText = fromDateText
            tempToDateText = toDateText
            tempRoleFilter = roleFilter
            tempStatusFilter = statusFilter

            tempFromDateText?.let { etFrom.text = it }
            tempToDateText?.let { etTo.text = it }

            tvToolbar.setOnClickListener {
                dialog.dismiss()
            }
            if (roleFilter!=null){
                tempRoleFilter = roleFilter
            }
            if (statusFilter!=null){
                tempStatusFilter = statusFilter
                when(tempStatusFilter){
                    "0"->{
                        rbActive.isChecked = true
                    }
                    "1"->{
                        rbInactive.isChecked = true
                    }
                }
            }
            if (fromDateText!=null){
                tempFromDateText = fromDateText
                tempFromTimestamp = fromTimestamp
                etFrom.text= CommonMethod.formatDate(tempFromDateText.toString(),"yyyy-MM-dd","dd/MM/yyyy")
            }
            if (toDateText!=null){
                tempToDateText = toDateText
                tempToTimestamp = toTimestamp
                etTo.text= CommonMethod.formatDate(tempToDateText.toString(),"yyyy-MM-dd","dd/MM/yyyy")
            }

            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                tempStatusFilter = when (checkedId) {
                    R.id.rbActive -> "0"
                    R.id.rbInactive -> "1"
                    else -> null
                }
            }

            etFrom.setOnClickListener {
                openCustomDatePicker(requireActivity()) { date, timestamp ->
                    tempFromTimestamp = timestamp
                    tempFromDateText = CommonMethod.formatDate(date,"dd/MM/yyyy","yyyy-MM-dd")
                    etFrom.text = date

                    tempToTimestamp = null
                    tempToDateText = null
                    etTo.text = ""
                }
            }

            etTo.setOnClickListener {
                if (tempFromTimestamp == null) {
                    showToast(getString(R.string.please_select_from_date_first))
                    return@setOnClickListener
                }

                openCustomDatePicker(
                    requireActivity(),
                    minDate = tempFromTimestamp
                ) { date, timestamp ->
                    tempToTimestamp = timestamp
                    tempToDateText =  CommonMethod.formatDate(date,"dd/MM/yyyy","yyyy-MM-dd")
                    etTo.text = date
                }
            }

            tvClear.setOnClickListener {
                fromTimestamp = null
                toTimestamp = null
                fromDateText = null
                toDateText = null
                roleFilter = null
                statusFilter = null

                tempFromTimestamp = null
                tempToTimestamp = null
                tempFromDateText = null
                tempToDateText = null
                tempStatusFilter = null
                tempRoleFilter = null

                getAllStaff()
                dialog.dismiss()
            }

            tvApply.setOnClickListener {

                fromTimestamp = tempFromTimestamp
                toTimestamp = tempToTimestamp
                fromDateText = tempFromDateText
                toDateText = tempToDateText
                statusFilter = tempStatusFilter
                roleFilter = tempRoleFilter

                if (statusFilter==null&&roleFilter==null&&fromDateText==null&&toDateText==null) {
                    showToast(getString(R.string.please_select_filter))
                    return@setOnClickListener
                }
                else{
                    getAllStaff()
                    dialog.dismiss()
                }

            }

        }

        dialog.show()
    }

}
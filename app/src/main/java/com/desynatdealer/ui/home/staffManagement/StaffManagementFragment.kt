package com.desynatdealer.ui.home.staffManagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.adapter.StaffAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentStaffManagementBinding
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.ProgressDialogUtils
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class StaffManagementFragment : BaseFragment() {
    private var binding : FragmentStaffManagementBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    private var staffAdapter: StaffAdapter?=null
    private var staffList = ArrayList<String>()

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
                            CommonMethod.deleteAlertDialog(
                                requireActivity(),
                                "Are you sure you want to delete staff?"
                            ) { action ->
                                when (action) {
                                    CommonMethod.DialogAction.DELETE -> {

                                    }

                                    CommonMethod.DialogAction.DISCARD -> {

                                    }
                                }
                            }

                        }
                    }
                }

            })
            rvStaff.adapter = staffAdapter

        }
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
    }

}
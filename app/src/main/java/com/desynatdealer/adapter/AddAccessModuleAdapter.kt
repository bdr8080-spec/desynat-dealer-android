package com.desynatdealer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatdealer.R
import com.desynatdealer.databinding.ItemAccessModuleBinding
import com.desynatdealer.ui.home.staffManagement.response.AddNewStaffRequest
import com.desynatdealer.ui.home.staffManagement.response.GetSellerAllMenuResponse
import com.desynatdealer.utils.Constants
import com.desynatdealer.utils.SharedPreference

class AddAccessModuleAdapter(var context:Context, var moduleList: ArrayList<AddNewStaffRequest.MenuId>, var onClick:OnClick) : RecyclerView.Adapter<AddAccessModuleAdapter.ViewHolder>() {

    interface OnClick{
        fun ClickOnItem(position: Int,type: String)
    }

    inner class ViewHolder(val binding: ItemAccessModuleBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            var moduleData = moduleList[position]
            tvModule.text = if (Constants.language=="ar")  moduleData?.titleArabic else moduleData?.title
            if (moduleData.isSelected){
                tvModule.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_radio_selected,0, 0 ,0)
            }
            else{
                tvModule.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_radio_unselected,0, 0 ,0)
            }
            rbView.setCompoundDrawablesWithIntrinsicBounds(
                if (moduleData.canView==1) R.drawable.ic_radio_selected
                else R.drawable.ic_radio_unselected,
                0, 0, 0
            )

            rbAction.setCompoundDrawablesWithIntrinsicBounds(
                if (moduleData.canAction==1) R.drawable.ic_radio_selected
                else R.drawable.ic_radio_unselected,
                0, 0, 0
            )
            tvModule.setOnClickListener {
                if (SharedPreference.get(context).userType=="0") {
                    onClick.ClickOnItem(position, "module")
                }

            }

            rbView.setOnClickListener {
                if (SharedPreference.get(context).userType=="0") {
                    onClick.ClickOnItem(position, "view")
                }
            }

            rbAction.setOnClickListener {
                if (SharedPreference.get(context).userType=="0") {
                    onClick.ClickOnItem(position, "action")
                }
            }
        }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAccessModuleBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return moduleList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

}

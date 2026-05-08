package com.desynatdealer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatdealer.R
import com.desynatdealer.databinding.ItemAccessModuleBinding
import com.desynatdealer.ui.home.staffManagement.response.GetAllStaffResponse
import com.desynatdealer.ui.home.staffManagement.response.GetSellerAllMenuResponse
import com.desynatdealer.ui.home.staffManagement.response.ViewStaffDetailResponse
import com.desynatdealer.utils.Constants

class AccessModuleAdapter(var context:Context, var moduleList: ArrayList<GetAllStaffResponse.Data.Data.RoleAction>, var onClick:OnClick) : RecyclerView.Adapter<AccessModuleAdapter.ViewHolder>() {

    interface OnClick{
        fun ClickOnItem(position: Int,type: String)
    }

    inner class ViewHolder(val binding: ItemAccessModuleBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            var moduleData = moduleList[position]
            tvModule.text = if (Constants.language=="ar")  moduleData?.sellermenu?.get(0)?.titleArabic else moduleData?.sellermenu?.get(0)?.title
            tvModule.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_radio_selected,0, 0 ,0)
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

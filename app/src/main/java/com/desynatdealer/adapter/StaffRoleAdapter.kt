package com.desynatdealer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatdealer.R
import com.desynatdealer.databinding.ItemStaffRoleBinding
import com.desynatdealer.ui.home.staffManagement.response.GetStaffCategoryResponse

class StaffRoleAdapter(var context:Context, var roleList: ArrayList<GetStaffCategoryResponse.Data>, var onClick:OnClick) : RecyclerView.Adapter<StaffRoleAdapter.ViewHolder>() {
    var selectedPosition = -1
    interface OnClick{
        fun ClickOnItem(position: Int,type: String)
    }

    inner class ViewHolder(val binding: ItemStaffRoleBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            var data = roleList[position]
            tvRole.text = data.nameEn
            if (position == selectedPosition){
                tvRole.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_radio_selected,0,0,0)
            } else {
                tvRole.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_radio_unselected,0,0,0)
            }
            root.setOnClickListener {

                val previousPosition = selectedPosition
                selectedPosition = position

                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)

                onClick.ClickOnItem(position, roleList[position].nameEn ?: "")
            }

        }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemStaffRoleBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return roleList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    fun setSelected(position:Int){
        selectedPosition = position
        notifyDataSetChanged()
    }
}

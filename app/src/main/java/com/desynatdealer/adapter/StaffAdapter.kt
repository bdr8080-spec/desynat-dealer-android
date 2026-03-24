package com.desynatdealer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatdealer.databinding.ItemStaffBinding

class StaffAdapter(var context:Context, var staffList: ArrayList<String>, var onClick:OnClick) : RecyclerView.Adapter<StaffAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemStaffBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            var staffData = staffList[position]


            ivEdit.setOnClickListener {
                onClick.ClickOnItem(position,"2")
            }

            ivDelete.setOnClickListener {
                onClick.ClickOnItem(position,"3")
            }

            root.setOnClickListener {
                onClick.ClickOnItem(position,"1")
            }
        }
        }

    interface OnClick{
        fun ClickOnItem(position: Int,type: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemStaffBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return staffList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }
}

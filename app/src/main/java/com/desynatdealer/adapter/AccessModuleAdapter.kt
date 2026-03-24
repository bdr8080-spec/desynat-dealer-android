package com.desynatdealer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatdealer.databinding.ItemAccessModuleBinding

class AccessModuleAdapter(var context:Context, var moduleList: ArrayList<String>, var onClick:OnClick) : RecyclerView.Adapter<AccessModuleAdapter.ViewHolder>() {

    interface OnClick{
        fun ClickOnItem(position: Int,type: String)
    }

    inner class ViewHolder(val binding: ItemAccessModuleBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            var moduleData = moduleList[position]

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

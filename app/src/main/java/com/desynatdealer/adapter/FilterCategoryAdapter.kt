package com.desynatdealer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatdealer.R
import com.desynatdealer.databinding.ItemBrandBinding
import com.desynatdealer.ui.home.productManagement.response.GetCategoryResponse

class FilterCategoryAdapter(var context:Context, var categoryList: ArrayList<GetCategoryResponse.Data>, var onClick: OnClick) : RecyclerView.Adapter<FilterCategoryAdapter.ViewHolder>() {

    var selectedPos = -1
    interface OnClick{
        fun onClickItem(position: Int)
    }
    inner class ViewHolder(val binding: ItemBrandBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){

                tvBrand.text = categoryList[position].name

                if (selectedPos==position){
                    ivImage.setImageResource(R.drawable.ic_checkbox)
                }
                else{
                    ivImage.setImageResource(R.drawable.ic_checkbox_unselected)
                }

            root.setOnClickListener {
                selectedPos = position
                onClick.onClickItem(position)
                notifyDataSetChanged()
            }

        }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemBrandBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    fun setSelectedCategoryId(id: Int?) {
        selectedPos = categoryList.indexOfFirst { it.id == id }
        notifyDataSetChanged()
    }

}

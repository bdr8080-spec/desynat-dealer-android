package com.desynatdealer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatdealer.R
import com.desynatdealer.databinding.ItemBrandBinding
import com.desynatdealer.ui.home.productManagement.response.GetSellerBrandResponse
import com.desynatdealer.utils.Constants

class FilterBrandAdapter(var context:Context, var brandList: ArrayList<GetSellerBrandResponse.Data>, var onClick: OnClick) : RecyclerView.Adapter<FilterBrandAdapter.ViewHolder>() {

    interface OnClick{
        fun onClickItem(position: Int)
    }
    inner class ViewHolder(val binding: ItemBrandBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            tvBrand.text = if (Constants.language=="ar") brandList[position].nameArabic else brandList[position].nameEnglish

            if (brandList[position].isSelected){
                ivImage.setImageResource(R.drawable.ic_checkbox)
            }
            else{
                ivImage.setImageResource(R.drawable.ic_checkbox_unselected)
            }

            root.setOnClickListener {
                brandList[position].isSelected = !brandList[position].isSelected
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
        return brandList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.bind(position)

    }


}

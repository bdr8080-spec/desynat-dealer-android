package com.desynatdealer.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatdealer.databinding.ItemSizeBinding
import com.desynatdealer.ui.home.productManagement.response.AddProductRequest
import com.desynatdealer.ui.home.productManagement.response.GetSizeResponse
import com.desynatdealer.utils.Constants

class SizeAdapter(var context:Context, var productSizeList: ArrayList<AddProductRequest.Product.ProductVariation>, var sizeList:ArrayList<GetSizeResponse.Data>,var onClick:OnClick) : RecyclerView.Adapter<SizeAdapter.ViewHolder>() {
    var selectedPos = 0
    interface OnClick{
        fun onClickItem(position: Int)
    }
    inner class ViewHolder(val binding: ItemSizeBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
                val productSize = productSizeList[position].sizeId
                val matchedSize = sizeList.find { it.id.toString() == productSize }

                tvSize.text = if (Constants.language=="ar") matchedSize?.nameArabic else matchedSize?.name

            if (selectedPos==position){
                ivColorSelected.isVisible = true
                ivColor.isVisible = false
                tvSize.setTextColor(Color.parseColor("#000000"))
            }
            else{
                ivColorSelected.isVisible = false
                ivColor.isVisible = true
                tvSize.setTextColor(Color.parseColor("#E6E6E6"))
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
            ItemSizeBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return productSizeList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    fun updateData(pos:Int){
        selectedPos = pos
        notifyDataSetChanged()
    }


}

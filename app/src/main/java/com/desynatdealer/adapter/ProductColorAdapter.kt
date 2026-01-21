package com.desynatdealer.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatdealer.databinding.ItemColorBinding
import com.desynatdealer.ui.home.productManagement.response.GetAllProductResponse
import com.desynatdealer.ui.home.productManagement.response.GetColorResponse

class ProductColorAdapter(var context:Context, var productColorList: List<GetAllProductResponse.Data.AllProductData.ProductVariant>,var colorList:ArrayList<GetColorResponse.Data>,var isRectangle:Boolean=false) : RecyclerView.Adapter<ProductColorAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemColorBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val productColor = productColorList[position].colorId
            val matchedColor = colorList.find { it.id.toString() == productColor }

            val colorCode = matchedColor?.hexCode ?: "#000000"

            if (isRectangle){
                ivColor.isVisible = false
                ivRectangleColor.isVisible = true
            }
            else{
                ivColor.isVisible = true
                ivRectangleColor.isVisible = false
            }
            ivColor.setColorFilter(Color.parseColor(colorCode))
            ivRectangleColor.setColorFilter(Color.parseColor(colorCode))
        }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemColorBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return productColorList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

}

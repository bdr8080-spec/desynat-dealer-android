package com.desynatdealer.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatdealer.R
import com.desynatdealer.databinding.ItemColorBinding
import com.desynatdealer.databinding.ItemProductBinding
import com.desynatdealer.databinding.ItemVariantColorBinding
import com.desynatdealer.ui.home.productManagement.response.AddProductRequest
import com.desynatdealer.ui.home.productManagement.response.GetAllProductResponse
import com.desynatdealer.ui.home.productManagement.response.GetColorResponse

class VariantColorAdapter(var context:Context, var productColorList: ArrayList<AddProductRequest.Product>, var colorList:ArrayList<GetColorResponse.Data>,var onClick:OnClick) : RecyclerView.Adapter<VariantColorAdapter.ViewHolder>() {
    var selectedPos = 0

    interface OnClick{
        fun onClickItem(position: Int)
    }
    inner class ViewHolder(val binding: ItemVariantColorBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemVariantColorBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return productColorList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {


            val productColor = productColorList[position].colorId
            val matchedColor = colorList.find { it.id.toString() == productColor }

            val colorCode = matchedColor?.hexCode ?: "#000000"
            cbColor.text = matchedColor?.name

            if(selectedPos==position){
                cbColor.isChecked = true
            }
            else{
                cbColor.isChecked = false
            }
        }

        holder.itemView.setOnClickListener {
            selectedPos = position
            onClick.onClickItem(position)
            notifyDataSetChanged()
        }


    }


}

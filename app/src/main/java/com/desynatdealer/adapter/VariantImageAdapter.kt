package com.desynatdealer.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatdealer.R
import com.desynatdealer.databinding.ItemProductBinding
import com.desynatdealer.databinding.ItemProductImageBinding
import com.desynatdealer.databinding.ItemVariantImgBinding
import com.desynatdealer.ui.home.productManagement.response.GetAllProductResponse
import com.desynatdealer.ui.home.productManagement.response.GetColorResponse
import com.desynatdealer.utils.CommonMethod

class VariantImageAdapter(var context:Context, var productImageList: ArrayList<String>,var clickOnItem:ClickOnItem) : RecyclerView.Adapter<VariantImageAdapter.ViewHolder>() {

    interface ClickOnItem{
        fun onClickItem(position: Int)
    }
    inner class ViewHolder(val binding: ItemVariantImgBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemVariantImgBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return productImageList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val productImageData = productImageList[position]
        holder.binding.apply {
            ivProduct.load(productImageData) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

            ivCross.setOnClickListener {
                clickOnItem.onClickItem(position)
            }

        }

    }


}

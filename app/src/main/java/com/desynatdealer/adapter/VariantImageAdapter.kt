package com.desynatdealer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatdealer.R
import com.desynatdealer.databinding.ItemVariantImgBinding

class VariantImageAdapter(var context:Context, var productImageList: ArrayList<String>,var clickOnItem:ClickOnItem) : RecyclerView.Adapter<VariantImageAdapter.ViewHolder>() {

    interface ClickOnItem{
        fun onClickItem(position: Int)
    }
    inner class ViewHolder(val binding: ItemVariantImgBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val productImageData = productImageList[position]
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
        holder.bind(position)
    }
}

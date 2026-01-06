package com.desynatdealer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatdealer.R
import com.desynatdealer.databinding.ItemProductImageBinding
class ProductImageAdapter(var context:Context, var productList: ArrayList<String>,var click:OnClick) : RecyclerView.Adapter<ProductImageAdapter.ViewHolder>() {

    interface OnClick{
        fun onClick(position: Int)
    }

    inner class ViewHolder(val binding: ItemProductImageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemProductImageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val productData = productList[position]
        holder.binding.apply {
            ivProduct.load(productData) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

            ivProduct.setOnClickListener {
                click.onClick(position)
            }

        }

    }


}

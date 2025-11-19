package com.desynatdealer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatdealer.R
import com.desynatdealer.databinding.ItemProductBinding

class ProductAdapter(var context:Context, var productList: ArrayList<SubscriptionAdapter.FeatureList>,var productClick:ProductClick) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    interface ProductClick{
        fun onProductClick(position: Int,type:Int)
    }
    inner class ViewHolder(val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemProductBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            tvBrand.text = productList[position].feature
            tvName.text = productList[position].feature
            tvRating.text = productList[position].feature
            tvPrice.text = productList[position].feature
            tvDiscountedPrice.text = productList[position].feature
            tvDate.text = productList[position].feature

            ivProduct.load(productList[position].feature) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

            ivSwitch.setOnClickListener {
                productClick.onProductClick(position,2)
            }
        }

        holder.itemView.setOnClickListener {
            productClick.onProductClick(position,1)
        }

    }


}

package com.desynatdealer.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatdealer.R
import com.desynatdealer.databinding.ItemColorBinding
import com.desynatdealer.databinding.ItemProductBinding

class ProductColorAdapter(var context:Context, var productColorList: ArrayList<SubscriptionAdapter.FeatureList>) : RecyclerView.Adapter<ProductColorAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemColorBinding) :
        RecyclerView.ViewHolder(binding.root)

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
        holder.binding.apply {

            ivColor.setColorFilter(Color.parseColor("#FFFF00"))

        }

    }


}

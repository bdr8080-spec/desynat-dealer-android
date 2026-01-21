package com.desynatdealer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatdealer.R
import com.desynatdealer.databinding.ItemDealProductBinding
import com.desynatdealer.ui.home.productManagement.response.GetAllProductResponse

class DealProductAdapter(
    var context: Context,
    var dealProductList: ArrayList<GetAllProductResponse.Data.AllProductData>,
    var dealType:String,
    var onClick: OnClick
) : RecyclerView.Adapter<DealProductAdapter.ViewHolder>() {


    interface OnClick {
        fun onItemClick(position: Int, type: Int)
    }

    inner class ViewHolder(val binding: ItemDealProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) = with(binding) {
            val dealProductData = dealProductList[position]

            when(dealType){
                "1" ->{
                    tvSoldOutValue.isVisible = false
                    ivCheckBox.isVisible = true
                    ivDelete.isVisible = false
                }
                "2" ->{
                    tvSoldOutValue.isVisible = false
                    ivCheckBox.isVisible = false
                    ivDelete.isVisible = true
                }
                "3" ->{
                    tvSoldOutValue.isVisible = true
                    ivCheckBox.isVisible = false
                    ivDelete.isVisible = true
                    tvSoldOutValue.text = "Total Sold : 30"
                }
            }

            ivProduct.load(dealProductData?.productVariants?.get(0)?.variantImage?.get(0)) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }
            tvProductName.text = dealProductData?.productNameEn
            tvRating.text = ""

            tvPrice.text = "${(dealProductData?.productVariants?.get(0)?.actualPrice?:0)} KD"
            tvDiscountedPrice.text = "${(dealProductData?.productVariants?.get(0)?.finalPrice?:0)} KD"

            val variant = dealProductData?.productVariants?.getOrNull(0)
            val discountValue = variant?.discountValue ?: 0
            val discountType = if (variant?.discountType == "1")"% off" else " KD off"
            tvOffer.text = "$discountValue$discountType"

            ivCheckBox.setOnClickListener {
                onClick.onItemClick(position, 1)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemDealProductBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return dealProductList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

}

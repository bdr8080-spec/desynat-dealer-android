package com.desynatdealer.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatdealer.R
import com.desynatdealer.databinding.ItemAddVariantBinding
import com.desynatdealer.databinding.ItemProductBinding
import com.desynatdealer.ui.home.productManagement.response.GetAllProductResponse
import com.desynatdealer.ui.home.productManagement.response.GetColorResponse
import com.desynatdealer.ui.home.productManagement.response.GetSizeResponse
import com.desynatdealer.utils.CommonMethod

class VariantAdapter(var context:Context, var variantList: ArrayList<GetAllProductResponse.Data.AllProductData.ProductVariant>, var colorList:ArrayList<GetColorResponse.Data>, var sizeList:ArrayList<GetSizeResponse.Data>,var from:Boolean=false var variantClick:VariantClick) : RecyclerView.Adapter<VariantAdapter.ViewHolder>() {

    interface VariantClick{
        fun onVariantClick(position: Int,type:Int)
    }
    inner class ViewHolder(val binding: ItemAddVariantBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAddVariantBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return variantList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val variantData = variantList[position]
        holder.binding.apply {

            if (from){
                llPrice.backgroundTintList = null
            }
            if (!from&&position==0){
                tvVariant.isVisible = false
            }
            etColour.setText(colorList.find { it.id.toString() == variantData.colorId }?.name)
            etSize.setText(sizeList.find { it.id.toString() == variantData.sizeId }?.name)
            tvVariant.text = "Variant $position"
            etBust.setText(variantData.bust)
            etWaist.setText(variantData.waist)
            etHip.setText(variantData.hip)
            etShoulder.setText(variantData.shoulder)
            etSleeveLength.setText(variantData.sleeveLength)
            etLength.setText(variantData.length)

            etSku.setText(variantData.sku)
            etThreshold.setText(variantData.skuThresholdValue)
            etTotalStocks.setText(variantData.totalStock)

            etActualPrice.setText(variantData.actualPrice)
            etFinalPrice.setText(variantData.finalPrice)
            setYesNoCheckbox(variantData.discountType,cbPercentage,cbFixedPrice)
            etDiscountedValue.setText(variantData.discountValue)
        }

        holder.itemView.setOnClickListener {
            variantClick.onVariantClick(position,1)
        }

    }

    private fun setYesNoCheckbox(value: String?, yesCB: RadioButton, noCB: RadioButton) {
        val isYes = value == "1"
        yesCB.isChecked = isYes
        noCB.isChecked = !isYes
    }

}

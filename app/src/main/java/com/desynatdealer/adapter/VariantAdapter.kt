package com.desynatdealer.adapter

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatdealer.R
import com.desynatdealer.databinding.ItemAddVariantBinding
import com.desynatdealer.databinding.ItemProductBinding
import com.desynatdealer.ui.home.productManagement.response.GetAllProductResponse
import com.desynatdealer.ui.home.productManagement.response.GetCategoryResponse
import com.desynatdealer.ui.home.productManagement.response.GetColorResponse
import com.desynatdealer.ui.home.productManagement.response.GetSizeResponse
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.showToast

class VariantAdapter(var context:Context, var isWomen: Boolean=false,var variantList: ArrayList<GetAllProductResponse.Data.AllProductData.ProductVariant>, var colorList:ArrayList<GetColorResponse.Data>, var sizeList:ArrayList<GetSizeResponse.Data>,var from:Boolean=false, var variantClick:VariantClick) : RecyclerView.Adapter<VariantAdapter.ViewHolder>() {

    interface VariantClick{
        fun onVariantClick(position: Int,type:Int,imagePos:Int=-1)
    }
    inner class ViewHolder(val binding: ItemAddVariantBinding) :
        RecyclerView.ViewHolder(binding.root){
        var actualPriceWatcher: TextWatcher? = null
        var finalPriceWatcher: TextWatcher? = null
        var discountValueWatcher: TextWatcher? = null
        var skuWatcher: TextWatcher? = null

        var totalStockWatcher: TextWatcher? = null
        var thresholdWatcher: TextWatcher? = null

        var bustWatcher: TextWatcher? = null
        var waistWatcher: TextWatcher? = null
        var hipWatcher: TextWatcher? = null
        var shoulderWatcher: TextWatcher? = null
        var sleeveLengthWatcher: TextWatcher? = null
        var lengthWatcher: TextWatcher? = null
        }

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

            ivDelete.isVisible = variantList.size > 1

            clWomenSize.isVisible = isWomen

         /*   if (variantData?.variantImage?.isNotEmpty() == true) {
                ivImage?.load(variantData?.variantImage?.get(0)) {
                    crossfade(true)
                    placeholder(R.drawable.ic_upload_product_img)
                    error(R.drawable.ic_upload_product_img)
                }
            }
            else{
                ivImage.setImageResource(R.drawable.ic_upload_product_img)
            }*/

            etColour.setText(colorList.find { it.id.toString() == variantData.colorId }?.name)
            etSize.setText(sizeList.find { it.id.toString() == variantData.sizeId }?.name)
            tvVariant.text = "Variant ${position+1}"
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

            cbPercentage.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    cbFixedPrice.isChecked = false
                    variantData.discountType = "1"

                    calculateFinalPrice(variantData)
                    if (variantData.finalPrice!="0") {
                        etFinalPrice.setText(variantData.finalPrice)
                    }
                }
            }

            cbFixedPrice.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    cbPercentage.isChecked = false
                    variantData.discountType = "2"

                    calculateFinalPrice(variantData)
                    if (variantData.finalPrice!="0") {
                        etFinalPrice.setText(variantData.finalPrice)
                    }
                }
            }

        }

        holder.binding.etColour.setOnClickListener {
            showColorDropdown(holder.binding.etColour,position)
        }

        holder.binding.etSize.setOnClickListener {
            showSizeDropdown(holder.binding.etSize,position)
        }

        holder.actualPriceWatcher?.let { holder.binding.etActualPrice.removeTextChangedListener(it) }
        holder.finalPriceWatcher?.let { holder.binding.etFinalPrice.removeTextChangedListener(it) }
        holder.discountValueWatcher?.let { holder.binding.etDiscountedValue.removeTextChangedListener(it) }
        holder.skuWatcher?.let { holder.binding.etSku.removeTextChangedListener(it) }
        holder.thresholdWatcher?.let { holder.binding.etThreshold.removeTextChangedListener(it) }
        holder.totalStockWatcher?.let { holder.binding.etTotalStocks.removeTextChangedListener(it) }

        holder.bustWatcher?.let { holder.binding.etBust.removeTextChangedListener(it) }
        holder.hipWatcher?.let { holder.binding.etHip.removeTextChangedListener(it) }
        holder.waistWatcher?.let { holder.binding.etWaist.removeTextChangedListener(it) }
        holder.sleeveLengthWatcher?.let { holder.binding.etSleeveLength.removeTextChangedListener(it) }
        holder.lengthWatcher?.let { holder.binding.etLength.removeTextChangedListener(it) }
        holder.shoulderWatcher?.let { holder.binding.etShoulder.removeTextChangedListener(it) }

       /* holder.actualPriceWatcher = CommonMethod.getWatcher(position) { pos, value ->
            variantList[pos].actualPrice = value

        }*/

        holder.actualPriceWatcher = CommonMethod.getWatcher(position,true) { pos, value ->
            variantList[pos].actualPrice = value
                calculateFinalPrice(variantList[pos])
                if (variantData.finalPrice!= "0"&&variantData.finalPrice!=variantData.actualPrice) {
                    holder.binding.etFinalPrice.setText(variantList[pos].finalPrice)
                }
            else{
                    variantData.finalPrice = ""

                    holder.binding.etFinalPrice.setText("")
                }

        }

        holder.binding.etActualPrice.addTextChangedListener(holder.actualPriceWatcher)

        holder.finalPriceWatcher = CommonMethod.getWatcher(position,true) { pos, value ->
            variantList[pos].finalPrice = value

        }
        holder.binding.etFinalPrice.addTextChangedListener(holder.finalPriceWatcher)


     /*   holder.discountValueWatcher = CommonMethod.getWatcher(position) { pos, value ->
            variantList[pos].discountValue = value

        }*/

        holder.discountValueWatcher = CommonMethod.getWatcher(position,true) { pos, value ->
            variantList[pos].discountValue = value
            calculateFinalPrice(variantList[pos])
            if (variantData.finalPrice!= "0"&&variantData.finalPrice!=variantData.actualPrice) {
                holder.binding.etFinalPrice.setText(variantList[pos].finalPrice)
            }
            else{
                variantData.finalPrice = ""
                holder.binding.etFinalPrice.setText("")
            }
        }
        holder.binding.etDiscountedValue.addTextChangedListener(holder.discountValueWatcher)


        holder.skuWatcher = CommonMethod.getWatcher(position,true) { pos, value ->
            variantList[pos].sku = value
        }
        holder.binding.etSku.addTextChangedListener(holder.skuWatcher)

        holder.thresholdWatcher = CommonMethod.getWatcher(position,true) { pos, value ->
            variantList[pos].skuThresholdValue = value
        }
        holder.binding.etThreshold.addTextChangedListener(holder.thresholdWatcher)


        holder.totalStockWatcher = CommonMethod.getWatcher(position,true) { pos, value ->
            variantList[pos].totalStock = value
        }
        holder.binding.etTotalStocks.addTextChangedListener(holder.totalStockWatcher)

        holder.bustWatcher = CommonMethod.getWatcher(position,true) { pos, value ->
            variantList[pos].bust = value
        }
        holder.binding.etBust.addTextChangedListener(holder.bustWatcher)

        holder.waistWatcher = CommonMethod.getWatcher(position,true) { pos, value ->
            variantList[pos].waist = value
        }
        holder.binding.etWaist.addTextChangedListener(holder.waistWatcher)

        holder.hipWatcher = CommonMethod.getWatcher(position,true) { pos, value ->
            variantList[pos].hip = value
        }
        holder.binding.etHip.addTextChangedListener(holder.hipWatcher)

        holder.shoulderWatcher = CommonMethod.getWatcher(position,true) { pos, value ->
            variantList[pos].shoulder = value
        }
        holder.binding.etShoulder.addTextChangedListener(holder.shoulderWatcher)

        holder.sleeveLengthWatcher = CommonMethod.getWatcher(position,true) { pos, value ->
            variantList[pos].sleeveLength = value
        }
        holder.binding.etSleeveLength.addTextChangedListener(holder.sleeveLengthWatcher)

        holder.lengthWatcher = CommonMethod.getWatcher(position,true) { pos, value ->
            variantList[pos].length = value
        }
        holder.binding.etLength.addTextChangedListener(holder.lengthWatcher)

        holder.binding.ivImage.setOnClickListener {
            variantClick.onVariantClick(position,2)
        }

holder.binding.ivDelete.setOnClickListener {
            variantClick.onVariantClick(position,3)
        }

        holder.itemView.setOnClickListener {
            variantClick.onVariantClick(position,1)
        }

        holder.binding.rvImageList.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            adapter = VariantImageAdapter(context = context, productImageList = variantList[position].variantImage,object : VariantImageAdapter.ClickOnItem{
                override fun onClickItem(imgPosition: Int) {
                    variantClick.onVariantClick(position,4,imgPosition)
                }

            })
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
        }


    }

    private fun setYesNoCheckbox(value: String?, yesCB: RadioButton, noCB: RadioButton) {
        val isYes = value == "1"
        yesCB.isChecked = isYes
        noCB.isChecked = !isYes
    }

    private fun showColorDropdown(view: AppCompatEditText, position: Int) {
        val list = colorList.map { it.name }
        val popup = androidx.appcompat.widget.ListPopupWindow(context)
        popup.anchorView = view
        popup.setAdapter(ArrayAdapter(context, android.R.layout.simple_list_item_1, list))
        popup.setOnItemClickListener { _, _, pos, _ ->
            val selected = colorList[pos]
            variantList[position].colorId = selected.id.toString()
            view.setText(selected.name)
            popup.dismiss()
        }
        popup.show()
    }

    private fun showSizeDropdown(view: AppCompatEditText, position: Int) {
        val list = sizeList.map { it.name }
        val popup = androidx.appcompat.widget.ListPopupWindow(context)
        popup.anchorView = view
        popup.setAdapter(ArrayAdapter(context, android.R.layout.simple_list_item_1, list))
        popup.setOnItemClickListener { _, _, pos, _ ->
            val selected = sizeList[pos]
            variantList[position].sizeId = selected.id.toString()
            view.setText(selected.name)
            popup.dismiss()
        }
        popup.show()
    }

    fun updateIsWomen(isWomen: Boolean) {
        this.isWomen = isWomen
        notifyDataSetChanged()
    }

    private fun calculateFinalPrice(variant: GetAllProductResponse.Data.AllProductData.ProductVariant) {
        val actual = variant.actualPrice?.toDoubleOrNull() ?: 0.0
        val discount = variant.discountValue?.toDoubleOrNull() ?: 0.0

        variant.finalPrice = when (variant.discountType) {
            "1" -> {   // Percentage
                val final = actual - (actual * discount / 100.0)
                final.toInt().toString()
            }
            "2" -> {   // Fixed price
                val final = actual - discount
                if (final < 0) "0" else final.toInt().toString()
            }
            else -> actual.toString()
        }
    }

}

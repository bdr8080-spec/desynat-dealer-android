package com.desynatdealer.adapter

import android.content.Context
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatdealer.databinding.ItemSubVariantBinding
import com.desynatdealer.ui.home.productManagement.response.GetAllProductResponse
import com.desynatdealer.ui.home.productManagement.response.GetColorResponse
import com.desynatdealer.ui.home.productManagement.response.GetSizeResponse
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.showToast

class SubVariantAdapter(var context:Context, var isWomen: Boolean=false, var variantList: ArrayList<GetAllProductResponse.Data.AllProductData.Product.ProductVariation>, var colorList:ArrayList<GetColorResponse.Data>, var sizeList:ArrayList<GetSizeResponse.Data>, var from:Boolean=false, var subVariantClick:SubVariantClick) : RecyclerView.Adapter<SubVariantAdapter.ViewHolder>() {

    interface SubVariantClick{
        fun onSubVariantClick(position: Int,type:Int)
    }
    inner class ViewHolder(val binding: ItemSubVariantBinding) :
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
            ItemSubVariantBinding.inflate(
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
            clWomenSize.isVisible = isWomen


            etSize.setText(sizeList.find { it.id.toString() == variantData.sizeId }?.name)
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

        holder.binding.ivAdd.setOnClickListener {
            if (validateSubVariants(position)) {
                addEmptySubVariant(position)
            }
        }

    }

    private fun setYesNoCheckbox(value: String?, yesCB: RadioButton, noCB: RadioButton) {
        val isYes = value == "1"
        yesCB.isChecked = isYes
        noCB.isChecked = !isYes
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

    private fun calculateFinalPrice(variant: GetAllProductResponse.Data.AllProductData.Product.ProductVariation) {
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


    private fun addEmptySubVariant(parentPos: Int) {

        val empty = GetAllProductResponse.Data.AllProductData.Product.ProductVariation(
            sizeId = "",
            bust = null,
            waist = null,
            hip = null,
            shoulder = null,
            sleeveLength = null,
            length = null,
            sku = "",
            skuThresholdValue = "",
            totalStock = "",
            actualPrice = "",
            finalPrice = "",
            discountType = "1",
            discountValue = "",
            createdAt = null,
            updatedAt = null,
            id = null,
            productId = null,
            status = null
        )

        variantList.add(empty)

        notifyDataSetChanged()
    }

    private fun validateSubVariants(position: Int): Boolean {

        for ((index, v) in variantList.withIndex()) {

            if (v.sizeId.isEmpty()) {
                showToast("Please select size")
                return false
            }

            if (isWomen) {

                if (v.bust.isNullOrEmpty()) {
                    showToast("Please enter bust size")
                    return false
                }
                if (v.waist.isNullOrEmpty()) {
                    showToast("Please enter waist size")
                    return false
                }
                if (v.hip.isNullOrEmpty()) {
                    showToast("Please enter hip size")
                    return false
                }
                if (v.shoulder.isNullOrEmpty()) {
                    showToast("Please enter shoulder size")
                    return false
                }
                if (v.sleeveLength.isNullOrEmpty()) {
                    showToast("Please enter sleeve length")
                    return false
                }
                if (v.length.isNullOrEmpty()) {
                    showToast("Please enter length")
                    return false
                }
            }

            // Stock
            if (v.totalStock.isEmpty()) {
                showToast("Please enter stock")
                return false
            }

            // SKU
            if (v.sku.isEmpty()) {
                showToast("Please enter SKU limit")
                return false
            }

            val skuVal = v.sku.toIntOrNull()
            val stockVal = v.totalStock.toIntOrNull()

            if (skuVal == null) {
                showToast("Invalid SKU limit")
                return false
            }
            if (stockVal == null) {
                showToast("Invalid stock")
                return false
            }

            if (skuVal >= stockVal) {
                showToast("SKU limit must be smaller than total stock")
                return false
            }

            // Price
            if (v.actualPrice.isNullOrEmpty()) {
                showToast("Please enter actual price")
                return false
            }
            if (v.discountValue.isEmpty()) {
                showToast("Please enter discount")
                return false
            }
        }

        return true
    }

    fun showToast(message: String){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

    }
}

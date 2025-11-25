package com.desynatdealer.adapter

import android.content.Context
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatdealer.databinding.ItemAddSpecificationBinding
import com.desynatdealer.databinding.ItemSpecificationBinding
import com.desynatdealer.ui.home.productManagement.response.GetAllProductResponse
import com.desynatdealer.utils.CommonMethod

class AddSpecificationAdapter(var context:Context, var specificationList: ArrayList<GetAllProductResponse.Data.AllProductData.ProductSpecification>) : RecyclerView.Adapter<AddSpecificationAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemAddSpecificationBinding) :
        RecyclerView.ViewHolder(binding.root){
        var nameEnWatcher: TextWatcher? = null
        var nameArWatcher: TextWatcher? = null
        var featureEnWatcher: TextWatcher? = null
        var featureArWatcher: TextWatcher? = null
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAddSpecificationBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return specificationList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            etName.setText(specificationList[position].nameEn)
            etNameArabic.setText(specificationList[position].nameAr)
            etFeature.setText(specificationList[position].featureEn)
            etFeatureArabic.setText(specificationList[position].featureAr)

        }

        holder.nameEnWatcher?.let { holder.binding.etName.removeTextChangedListener(it) }
        holder.nameEnWatcher = CommonMethod.getWatcher(position) { pos, value ->
            specificationList[pos].nameEn = value
        }
        holder.binding.etName.addTextChangedListener(holder.nameEnWatcher)

        holder.nameArWatcher?.let { holder.binding.etNameArabic.removeTextChangedListener(it) }
        holder.nameArWatcher = CommonMethod.getWatcher(position) { pos, value ->
            specificationList[pos].nameAr = value
        }
        holder.binding.etNameArabic.addTextChangedListener(holder.nameArWatcher)

        holder.featureArWatcher?.let { holder.binding.etFeatureArabic.removeTextChangedListener(it) }
        holder.featureArWatcher = CommonMethod.getWatcher(position) { pos, value ->
            specificationList[pos].featureAr = value
        }
        holder.binding.etFeatureArabic.addTextChangedListener(holder.featureArWatcher)

        holder.featureEnWatcher?.let { holder.binding.etFeature.removeTextChangedListener(it) }
        holder.featureEnWatcher = CommonMethod.getWatcher(position) { pos, value ->
            specificationList[pos].featureEn = value
        }
        holder.binding.etFeature.addTextChangedListener(holder.featureEnWatcher)
    }


}

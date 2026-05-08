package com.desynatdealer.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.desynatdealer.databinding.ItemAddVariantBinding
import com.desynatdealer.ui.home.productManagement.response.AddProductRequest
import com.desynatdealer.ui.home.productManagement.response.GetAllProductResponse
import com.desynatdealer.ui.home.productManagement.response.GetColorResponse
import com.desynatdealer.ui.home.productManagement.response.GetSizeResponse
import com.desynatdealer.utils.Constants

class VariantAdapter(var context:Context, var isWomen: Boolean=false, var variantList: ArrayList<AddProductRequest.Product>, var colorList:ArrayList<GetColorResponse.Data>, var sizeList:ArrayList<GetSizeResponse.Data>, var from:Boolean=false, var variantClick:VariantClick) : RecyclerView.Adapter<VariantAdapter.ViewHolder>() {

    interface VariantClick{
        fun onVariantClick(position: Int,type:Int,imagePos:Int=-1,subPos: Int=-1)
    }
    inner class ViewHolder(val binding: ItemAddVariantBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val variantData = variantList[position]

                if (!from&&position==0){
                    tvVariant.isVisible = false
                }

                ivDelete.isVisible = variantList.size > 1

            etColour.setText(
                        if (Constants.language == "ar") {
                            colorList.find { it.id.toString() == variantData.colorId }?.nameArabic
                        } else {
                            colorList.find { it.id.toString() == variantData.colorId }?.name
                        }
                        )

                tvVariant.text = "Variant ${position+1}"

            etColour.setOnClickListener {
                showColorDropdown(etColour,position)
            }

            ivImage.setOnClickListener {
                variantClick.onVariantClick(position,2)
            }

            ivDelete.setOnClickListener {
                variantClick.onVariantClick(position,3)
            }

            root.setOnClickListener {
                variantClick.onVariantClick(position,1)
            }

            rvImageList.apply {
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                adapter = VariantImageAdapter(context = context, productImageList = variantList[position].variantImage,object : VariantImageAdapter.ClickOnItem{
                    override fun onClickItem(imgPosition: Int) {
                        variantClick.onVariantClick(position,4,imgPosition)
                    }

                })
                setHasFixedSize(true)
                isNestedScrollingEnabled = false
            }

            Log.e("listprint", "bindHolder: ${ variantList[position].productVariation.size}" )

            rvSubVariant.apply {
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                adapter = SubVariantAdapter(context = context,isWomen,variantList[position].productVariation,colorList,sizeList,false,object:SubVariantAdapter.SubVariantClick{
                    override fun onSubVariantClick(subPosition: Int, type: Int) {
                        when(type){

                        }
                    }

                })
                setHasFixedSize(true)
                isNestedScrollingEnabled = false
            }
        }
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
        holder.bind(position)
    }

    private fun showColorDropdown(view: AppCompatEditText, position: Int) {
        val list = colorList.map { if (Constants.language=="ar") it.nameArabic else it.name }
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

    fun updateIsWomen(isWomen: Boolean) {
        this.isWomen = isWomen
        notifyDataSetChanged()
    }
}

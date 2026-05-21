package com.desynatdealer.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatdealer.R
import com.desynatdealer.databinding.ItemProductBinding
import com.desynatdealer.ui.home.productManagement.response.GetAllProductResponse
import com.desynatdealer.ui.home.productManagement.response.GetColorResponse
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.Constants
import com.desynatdealer.utils.SharedPreference
import com.desynatdealer.utils.setImage

class ProductAdapter(var context:Context, var productList: ArrayList<GetAllProductResponse.Data.AllProductData>,var colorList:ArrayList<GetColorResponse.Data>, var productClick:ProductClick) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    interface ProductClick{
        fun onProductClick(position: Int,type:Int)
    }
    inner class ViewHolder(val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val productData = productList[position]
            var canAction = true
            var canView = true
                if (SharedPreference.get(context).userType == "1") {
                    canAction = CommonMethod.getPermission(context,"seller.products")?.canAction == 1
                    canView = CommonMethod.getPermission(context,"seller.products")?.canView == 1
                }

                tvBrand.text = if(productData?.brand?.nameEnglish=="Other"){
                    if (Constants.language=="ar") productData?.otherAr else productData?.otherEn
                }   else {
                    if (Constants.language=="ar") productData?.brand?.nameArabic else  productData?.brand?.nameEnglish
                }
                tvName.text = if (Constants.language=="ar") productData.productNameAr else productData.productNameEn
                if (productData?.productVariants?.get(0)?.totalStock!="0"){
                    tvInStock.text = context.getString(R.string.in_stock)
                }
                else{
                    tvInStock.text = context.getString(R.string.out_of_stock)
                }

                tvDate.text = "${context.getString(R.string.added_on)}- ${CommonMethod.customizeChangeDateFormat(productData?.createdAt.toString(), outputFormat = "dd/MM/yyyy")}"
//                ivProduct.load(productData?.productVariants?.get(0)?.variantImage?.get(0)) {
//                    crossfade(true)
//                    placeholder(R.drawable.ic_placeholder)
//                    error(R.drawable.ic_placeholder)
//                }


            ivProduct.setImage(productData?.productVariants?.get(0)?.variantImage?.get(0),R.drawable.ic_placeholder)

                if(productData.productActiveStatus==1){
                    tvSwitch.text = context.getString(R.string.active)
                    tvSwitch.setTextColor(Color.parseColor("#1A900D"))
                    tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.switch_on,0, 0 )
                }
                else{
                    tvSwitch.text = context.getString(R.string.inactive)
                    tvSwitch.setTextColor(Color.parseColor("#E70000"))
                    tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.switch_off,0,0 )

                }

                tvSwitch.setOnClickListener {
                    if (canAction) {
                        productClick.onProductClick(position, 2)
                    }
                }

                  val rating = productData?.productRating?.toFloatOrNull() ?: 0f
                  ratingBar.rating = rating
                  tvRating.text = "(${String.format("%.1f", rating)})"

                tvPrice.text = "${(productData?.productVariants?.get(0)?.actualPrice?:0)} ${
                    context.getString(
                        R.string.kd
                    )
                }"
                tvDiscountedPrice.text = "${(productData?.productVariants?.get(0)?.finalPrice?:0)} ${
                    context.getString(
                        R.string.kd
                    )
                }"

                val variant = productData?.productVariants?.getOrNull(0)
                val discountValue = variant?.discountValue ?: 0
                val discountType = if (variant?.discountType == "1")"% ${context.getString(R.string.off)}" else " ${context.getString(R.string.kd)} ${
                    context.getString(
                        R.string.off
                    )
                }"
                tvOffer.text = "$discountValue$discountType"


                rvColor.apply {
                    layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)

                    val uniqueColors = productList[position].productVariants
                        ?.distinctBy { it.colorId }

                    adapter = uniqueColors?.let {
                        ProductColorAdapter(
                            context = context,
                            productColorList = it,
                            colorList,
                            false
                        )
                    }

                    setHasFixedSize(true)
                    isNestedScrollingEnabled = false
                }

            root.setOnClickListener {
                productClick.onProductClick(position,1)
            }
        }
        }

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
        holder.bind(position)
    }


}

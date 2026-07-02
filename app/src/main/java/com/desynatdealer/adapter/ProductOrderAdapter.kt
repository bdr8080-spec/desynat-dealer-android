package com.desynatdealer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatdealer.R
import com.desynatdealer.databinding.ItemOrderProductBinding
import com.desynatdealer.ui.home.orderManagement.response.GetSingleOrderResponse
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.Constants

class ProductOrderAdapter(var context:Context, var productList: ArrayList<GetSingleOrderResponse.Data>) : RecyclerView.Adapter<ProductOrderAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemOrderProductBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val productData = productList[position]
                tvCount.text = if((position+1)<10) "0${(position+1)}" else (position+1).toString()
                tvProduct.text = productData?.productDetails?.productNameEn

                tvFabric.text = if (Constants.language=="ar") productData?.productDetails?.brand?.nameArabic else productData?.productDetails?.brand?.nameEnglish
                tvSizeValue.text = if (Constants.language=="ar") productData?.variationDetails?.size?.name else  productData?.variationDetails?.size?.name
                tvColorValue.text = if (Constants.language=="ar")productData?.variationDetails?.color?.nameArabic else  productData?.variationDetails?.color?.name
                tvCategoryValue.text = if (Constants.language=="ar")productData?.productDetails?.category?.nameArabic else  productData?.productDetails?.category?.name
                if (productData?.orderType=="2"){
                    tvPrice.text = "${CommonMethod.formatPrice(productData?.chatRoomrecords?.totalAmount?.toDoubleOrNull()?:0.0) } ${
                        context.getString(
                            R.string.kd
                        )
                    }"
                    tvQuantity.text = context.getString(R.string.per_item_price)
                    tvQuantityValue.text = "${CommonMethod.formatPrice(productData?.amount?:0.0)} ${
                        context.getString(
                            R.string.kd
                        )
                    }"

                }
                else {
                    val itemPrice = productData?.amount ?: 0.0
                    val quantity = productData?.quantity ?: 1
                    val subTotal = itemPrice * quantity
                    tvPrice.text = "${CommonMethod.formatPrice(subTotal)} ${context.getString(R.string.kd)}"
                    tvQuantity.text = context.getString(R.string.quantity)
                    tvQuantityValue.text = "${CommonMethod.formatPrice(productData?.amount?:0.0)} ${
                        context.getString(
                            R.string.kd
                        )
                    } X ${productData?.quantity?.toString()}"

                }

                ivProduct.load(productData?.variationDetails?.variantImage?.get(0)) {
                    crossfade(true)
                    placeholder(R.drawable.ic_placeholder)
                    error(R.drawable.ic_placeholder)
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemOrderProductBinding.inflate(
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

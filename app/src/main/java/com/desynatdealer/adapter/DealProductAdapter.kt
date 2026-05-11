package com.desynatdealer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatdealer.R
import com.desynatdealer.adapter.DealAdapter.OnFilterResult
import com.desynatdealer.databinding.ItemDealProductBinding
import com.desynatdealer.ui.home.dealManagement.response.GetAllDealResponse
import com.desynatdealer.ui.home.dealManagement.response.GetDealAllProductResponse
import com.desynatdealer.ui.home.dealManagement.response.GetDealDetailResponse
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.Constants

class DealProductAdapter(
    var context: Context,
    var dealProductList: ArrayList<GetDealDetailResponse.Data.RemainingProduct>,
    private var dealType:String,
    var onClick: OnClick,var onFilterResult: OnFilterResult
) : RecyclerView.Adapter<DealProductAdapter.ViewHolder>(),Filterable {
    private var filterList = ArrayList<GetDealDetailResponse.Data.RemainingProduct>()

    init {
        filterList.clear()
        filterList.addAll(dealProductList)
    }
    interface OnClick {
        fun onItemClick(position: Int, type: Int)
        fun onSelectionChanged(isAnySelected: Boolean)
    }

    interface OnFilterResult {
        fun onResult(isEmpty: Boolean)
    }

    inner class ViewHolder(val binding: ItemDealProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) = with(binding) {
            if (position >= filterList.size) return
            val dealProductData = filterList[position]

            when(dealType){
                "1" ->{
                    tvSoldOutValue.isVisible = false
                    relCheckBox.isVisible = true
                    ivDelete.isVisible = false
                }
                "2" ->{
                    tvSoldOutValue.isVisible = false
                    relCheckBox.isVisible = false
                    ivDelete.isVisible = true
                }
                "3" ->{
                    tvSoldOutValue.isVisible = true
                    relCheckBox.isVisible = false
                    ivDelete.isVisible = false
                    tvSoldOutValue.text = "${context.getString(R.string.total_sold)} : 30"
                }
            }
            val variant = dealProductData.productVariants?.getOrNull(0)
            ivProduct.load(variant?.variantImage?.get(0)) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }
            tvProductName.text = if (Constants.language=="ar") dealProductData?.productNameAr else dealProductData?.productNameEn
            tvRating.text = "(${CommonMethod.formatPrice((dealProductData?.avgProductRating?:0).toDouble())})"
            ratingBar.rating = dealProductData?.avgProductRating?:0f

            tvPrice.text = "${(dealProductData?.productVariants?.get(0)?.actualPrice?:0)} KD"
            tvDiscountedPrice.text = "${(dealProductData?.productVariants?.get(0)?.finalPrice?:0)} KD"

            val discountValue = variant?.discountValue ?: 0
            val discountType = if (variant?.discountType == "1")"% ${context.getString(R.string.off)}" else " ${
                context.getString(
                    R.string.kd
                )
            } ${
                context.getString(
                    R.string.off
                )
            }"
            tvOffer.text = "$discountValue$discountType"
            if (dealProductData.isAdded){
                ivCheckBox.setImageResource(R.drawable.ic_checkbox)
            }
            else{
                ivCheckBox.setImageResource(R.drawable.ic_checkbox_unselected)
            }

            relCheckBox.setOnClickListener {
                dealProductData.isAdded = !dealProductData.isAdded
                notifyDataSetChanged()
                val isAnySelected = filterList.any { it.isAdded }
                onClick.onSelectionChanged(isAnySelected)
            }

            ivDelete.setOnClickListener {
                if (dealType!="3") {
                    onClick.onItemClick(position, 2)
                }
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
        return filterList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    fun setDealType(dealTypeValue: String){
        dealType = dealTypeValue
        notifyDataSetChanged()

    }

    override fun getFilter(): Filter {
        return object : Filter() {

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val resultList =
                    ArrayList<GetDealDetailResponse.Data.RemainingProduct>()

                if (constraint.isNullOrEmpty()) {
                    resultList.addAll(dealProductList)
                } else {
                    val filterPattern = constraint.toString().trim().lowercase()

                    for (item in dealProductList) {
                        if (
                            item.productNameEn?.lowercase()?.contains(filterPattern) == true ||
                            item.brand?.nameEnglish?.lowercase()?.contains(filterPattern) == true ||
                            item.category?.name?.lowercase()?.contains(filterPattern) == true ||
                            item.subcategory?.nameEnglish?.lowercase()?.contains(filterPattern) == true
                        ) {
                            resultList.add(item)
                        }
                    }
                }

                val results = FilterResults()
                results.values = resultList
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filterList.clear()
                filterList.addAll(
                    results?.values as ArrayList<GetDealDetailResponse.Data.RemainingProduct>
                )
                notifyDataSetChanged()
                onFilterResult.onResult(filterList.isEmpty())
            }
        }
    }

    fun updateList(newList: ArrayList<GetDealDetailResponse.Data.RemainingProduct>) {
        dealProductList = newList
        filterList.clear()
        filterList.addAll(newList)
        notifyDataSetChanged()
    }

}

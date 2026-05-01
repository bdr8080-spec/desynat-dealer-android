package com.desynatdealer.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatdealer.R
import com.desynatdealer.databinding.ItemDealBinding
import com.desynatdealer.ui.home.dealManagement.response.GetAllDealResponse
import com.desynatdealer.utils.CommonMethod
import java.text.SimpleDateFormat
import java.util.Locale

class DealAdapter(
    var context: Context,
    var dealList: ArrayList<GetAllDealResponse.Data.ListedDeal>,
    var onClick: OnClick,
    var onFilterResult: OnFilterResult
) : RecyclerView.Adapter<DealAdapter.ViewHolder>(), Filterable  {

    private var dealType = "1"
    private var filterList = ArrayList<GetAllDealResponse.Data.ListedDeal>()
    private var fromDate: Long? = null
    private var toDate: Long? = null
    private var currentQuery: String? = null


    interface OnFilterResult {
        fun onResult(isEmpty: Boolean)
    }

    interface OnClick {
        fun onItemClick(position: Int, type: Int)
    }

    inner class ViewHolder(val binding: ItemDealBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) = with(binding) {
            val dealData = filterList[position]

            when (dealType) {
                "1" -> {
                    tvStartFrom.isVisible = true
                    clDealInfo.isVisible = false
                }

                "2" -> {
                    tvStartFrom.isVisible = false
                    clDealInfo.isVisible = true
                    when {
                        CommonMethod.isUpcoming(dealData?.from.toString()) -> {
                            tvStatusValue.text = context.getString(R.string.upcoming)
                            tvStatusValue.paint.shader = null
                            tvStatusValue.setTextColor(Color.parseColor("#000000"))
                        }

                        CommonMethod.isActive(dealData?.from.toString(), dealData?.to.toString()) -> {
                            tvStatusValue.text = context.getString(R.string.active)
                            CommonMethod.applyGradientText(tvStatusValue)
                            tvStatusValue.setTextColor(Color.parseColor("#D53B36"))
                        }

                        CommonMethod.isExpired(dealData?.from.toString()) -> {
                            tvStatusValue.paint.shader = null
                            tvStatusValue.text = context.getString(R.string.expired)
                            tvStatusValue.setTextColor(Color.parseColor("#D53B36"))
                        }
                    }
                }

                "3" -> {
                    tvStartFrom.isVisible = false
                    clDealInfo.isVisible = true
                    tvStatusValue.text = context.getString(R.string.expired)
                    tvStatusValue.setTextColor(Color.parseColor("#D53B36"))
                }
            }

            ivDeal.load(dealData?.image) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }
            tvStartFrom.applyVerticalGradient(
                Color.parseColor("#DFBF8F"),
                Color.parseColor("#6E4527")
            )
            tvStartFrom.text = "${context.getString(R.string.starts_from)} - "+CommonMethod.customizeChangeDateFormat(dealData?.from.toString(),"yyyy-MM-dd","dd MMM yyyy")
            tvStartsOnValue.text = CommonMethod.customizeChangeDateFormat(dealData?.from.toString(),"yyyy-MM-dd","dd MMM yyyy")
            tvFromValue.text =  CommonMethod.customizeChangeDateFormat(dealData?.from.toString(),"yyyy-MM-dd","dd/MM/yyyy") +" - "+ CommonMethod.customizeChangeDateFormat(dealData?.to.toString(),"yyyy-MM-dd","dd/MM/yyyy")
            tvProductsAddedValue.text = dealData?.sellerDealCount

            root.setOnClickListener {
                onClick.onItemClick(position, 1)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemDealBinding.inflate(
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

    fun TextView.applyVerticalGradient(
        startColor: Int,
        endColor: Int
    ) {
        post {
            paint.shader = LinearGradient(
                0f,
                0f,
                0f,
                textSize,
                intArrayOf(startColor, endColor),
                null,
                Shader.TileMode.CLAMP
            )
            invalidate()
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        currentQuery = constraint?.toString()
        val resultList = ArrayList<GetAllDealResponse.Data.ListedDeal>()

        for (deal in dealList) {

            val matchesSearch =
                constraint.isNullOrBlank() ||
                        deal.nameEnglish?.lowercase()?.contains(constraint.toString().lowercase()) == true ||
                        deal.discount?.contains(constraint.toString()) == true ||
                        deal.sellerDealCount?.contains(constraint.toString()) == true ||
                        getStatusText(deal.status).contains(constraint.toString().lowercase())

            val dealTime = convertDateToTimestamp(
                deal.from,
                "yyyy-MM-dd"
            )

            val matchesDate =
                (fromDate == null || dealTime >= fromDate!!) &&
                        (toDate == null || dealTime <= toDate!!)

            if (matchesSearch && matchesDate) {
                resultList.add(deal)
            }
        }

        return FilterResults().apply { values = resultList }
    }
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filterList.clear()
                filterList.addAll(
                    results?.values as ArrayList<GetAllDealResponse.Data.ListedDeal>
                )
                notifyDataSetChanged()
                onFilterResult.onResult(filterList.isEmpty())
            }
        }
    }

    private fun getStatusText(status: Int): String {
        return when (status) {
            1 -> "upcoming"
            2 -> "active"
            3 -> "expired"
            else -> ""
        }
    }

    fun updateList(type: String,newList: ArrayList<GetAllDealResponse.Data.ListedDeal>) {
        dealType = type
        dealList = newList
        filterList.clear()
        filterList.addAll(newList)
        notifyDataSetChanged()
    }


    fun setDateFilter(from: Long?, to: Long?) {
        fromDate = from
        toDate = to
        filter.filter(currentQuery)
    }

    fun convertDateToTimestamp(date: String?, format: String): Long {
        if (date.isNullOrEmpty()) return 0L
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        return sdf.parse(date)?.time ?: 0L
    }
}

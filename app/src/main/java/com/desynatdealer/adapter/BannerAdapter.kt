package com.desynatdealer.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bumptech.glide.Glide
import com.desynatdealer.R
import com.desynatdealer.databinding.ItemAddBannerBinding
import com.desynatdealer.ui.home.bannerManagement.response.GetAllBannerResponse
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.Constants

class BannerAdapter(var context:Context, var bannerList: ArrayList<GetAllBannerResponse.Data>, var onClick:OnClick) : RecyclerView.Adapter<BannerAdapter.ViewHolder>() {

    interface OnClick{
        fun onItemClick(position: Int,type:Int)
    }
    inner class ViewHolder(val binding: ItemAddBannerBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val bannerData = bannerList[position]

            Glide.with(context).load(bannerData?.uploadBanner).placeholder(R.drawable.ic_placeholder).into(ivBanner)

                tvRequestId.text = "#${bannerData?.orderId}"
                tvTotalAmount.text = "${CommonMethod.formatPrice(bannerData?.price?:0.0)} ${context.getString(R.string.kd)}"
                tvRequestedOnValue.text = CommonMethod.customizeChangeDateFormat(bannerData?.createdAt.toString(), outputFormat = "dd/MM/yyyy 'at' hh:mm a")
                when(bannerData?.adminVerify){
                    "0" ->{
                        tvStatusValue.text = context.getString(R.string.pending)
                        tvStatusValue.paint.shader = null
                        tvStatusValue.setTextColor(Color.parseColor("#E28F12"))
                        btnPay.isVisible = false
                        tvReason.isVisible = false
                        tvRejectedReason.isVisible = false
                    }
                    "1" ->{
                        when(bannerData?.paymentStatus){
                            "0"->{
                                if (CommonMethod.isExpired(bannerData?.endDate.toString())) {
                                    tvStatusValue.text = context.getString(R.string.expired)
                                    tvStatusValue.paint.shader = null
                                    tvStatusValue.setTextColor(Color.parseColor("#D53B36"))
                                    btnPay.isVisible = false
                                }
                                else{
                                    tvStatusValue.text = context.getString(R.string.confirmed)
                                    tvStatusValue.paint.shader = null
                                    tvStatusValue.setTextColor(Color.parseColor("#68CA44"))
                                    btnPay.isVisible = true
                                    tvReason.isVisible = false
                                    tvRejectedReason.isVisible = false
                                }

                            }
                            "1"->{
                                btnPay.isVisible = false
                                tvReason.isVisible = false
                                tvRejectedReason.isVisible = false
                                when {
                                    CommonMethod.isUpcoming(bannerData?.startDate.toString()) -> {
                                        tvStatusValue.text = context.getString(R.string.upcoming)
                                        tvStatusValue.paint.shader = null
                                        tvStatusValue.setTextColor(Color.parseColor("#68CA44"))
                                        CommonMethod.applyGradientText(tvStatusValue)
                                    }

                                    CommonMethod.isActive(bannerData?.startDate.toString(), bannerData?.endDate.toString()) -> {
                                        tvStatusValue.text = context.getString(R.string.active)
                                        CommonMethod.applyGradientText(tvStatusValue)
                                    }

                                    CommonMethod.isExpired(bannerData?.endDate.toString()) -> {
                                        tvStatusValue.paint.shader = null
                                        tvStatusValue.text = context.getString(R.string.expired)
                                        tvStatusValue.setTextColor(Color.parseColor("#D53B36"))
                                    }
                                }
                            }
                            "2"->{
                                tvStatusValue.text = context.getString(R.string.rejected)
                                tvStatusValue.paint.shader = null
                                tvStatusValue.setTextColor(Color.parseColor("#D53B36"))
                                btnPay.isVisible = false
                                tvReason.isVisible = true
                                tvRejectedReason.isVisible = true
                                tvRejectedReason.text = bannerData?.cancelledReason
                            }
                        }
                    }
                    "2"->{
                        tvStatusValue.text = context.getString(R.string.rejected)
                        tvStatusValue.paint.shader = null
                        tvStatusValue.setTextColor(Color.parseColor("#D53B36"))
                        btnPay.isVisible = false
                        if (bannerData?.cancelledReason!=null) {
                            tvReason.isVisible = true
                            tvRejectedReason.isVisible = true
                            tvRejectedReason.text = bannerData?.cancelledReason
                        }
                    }
                }

                when(bannerData?.bannerPosition){
                    "3"->{
                        tvCategoryValue.text = bannerData?.categoryDetails?.name
                        tvSubCategoryValue.text = if (Constants.language=="ar") bannerData?.subcategoryDetails?.nameArabic else bannerData?.subcategoryDetails?.nameEnglish
                        tvCategory.isVisible = false
                        tvCategoryValue.isVisible = false
                        tvSubCategory.isVisible = false
                        tvSubCategoryValue.isVisible = false
                    }
                    else ->{
                        tvCategory.isVisible = false
                        tvCategoryValue.isVisible = false
                        tvSubCategory.isVisible = false
                        tvSubCategoryValue.isVisible = false
                    }
                }

                btnPay.setOnClickListener {
                    onClick.onItemClick(position,2)
                }

            root.setOnClickListener {
                onClick.onItemClick(position,1)
            }

        }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAddBannerBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return bannerList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      holder.bind(position)
    }


}

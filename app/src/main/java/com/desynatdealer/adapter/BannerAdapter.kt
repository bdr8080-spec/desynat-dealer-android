package com.desynatdealer.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatdealer.R
import com.desynatdealer.databinding.ItemAddBannerBinding
import com.desynatdealer.ui.home.bannerManagement.response.GetAllBannerResponse
import com.desynatdealer.utils.CommonMethod

class BannerAdapter(var context:Context, var bannerList: ArrayList<GetAllBannerResponse.Data>, var onClick:OnClick) : RecyclerView.Adapter<BannerAdapter.ViewHolder>() {

    interface OnClick{
        fun onItemClick(position: Int,type:Int)
    }
    inner class ViewHolder(val binding: ItemAddBannerBinding) :
        RecyclerView.ViewHolder(binding.root)

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
        val bannerData = bannerList[position]
        holder.binding.apply {

            ivBanner.load(bannerData?.uploadBanner) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

            tvRequestId.text = "#${bannerData?.orderId}"
            tvTotalAmount.text = "${CommonMethod.formatPrice(bannerData?.price?:0.0)} KD"
            tvRequestedOnValue.text = CommonMethod.customizeChangeDateFormat(bannerData?.createdAt.toString(), outputFormat = "dd/MM/yyyy 'at' hh:mm a")
            when(bannerData?.adminVerify){
                "0" ->{
                    tvStatusValue.text = "Pending"
                    tvStatusValue.setTextColor(Color.parseColor("#E28F12"))
                    btnPay.isVisible = false
                    tvReason.isVisible = false
                    tvRejectedReason.isVisible = false
                }
                "1" ->{
                    when(bannerData?.paymentStatus){
                        "0"->{
                            tvStatusValue.text = "Confirmed"
                            tvStatusValue.setTextColor(Color.parseColor("#68CA44"))
                            btnPay.isVisible = true
                            tvReason.isVisible = false
                            tvRejectedReason.isVisible = false
                        }
                        "1"->{
                            tvStatusValue.text = "Active"
                            CommonMethod.applyGradientText(tvStatusValue)
                            btnPay.isVisible = false
                            tvReason.isVisible = false
                            tvRejectedReason.isVisible = false
                        }
                        "2"->{
                            tvStatusValue.text = "Rejected"
                            tvStatusValue.setTextColor(Color.parseColor("#D53B36"))
                            btnPay.isVisible = false
                            tvReason.isVisible = true
                            tvRejectedReason.isVisible = true
                            tvRejectedReason.text = bannerData?.cancelledReason
                        }
                    }
                }
                "2"->{
                    tvStatusValue.text = "Rejected"
                    tvStatusValue.setTextColor(Color.parseColor("#D53B36"))
                    btnPay.isVisible = false
                    tvReason.isVisible = true
                    tvRejectedReason.isVisible = true
                    tvRejectedReason.text = bannerData?.cancelledReason
                }
            }

            when(bannerData?.bannerPosition){
                "3"->{
                    tvCategoryValue.text = bannerData?.categoryDetails?.name
                    tvSubCategoryValue.text = bannerData?.subcategoryDetails?.nameEnglish
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
        }

        holder.itemView.setOnClickListener {
            onClick.onItemClick(position,1)
        }

    }


}

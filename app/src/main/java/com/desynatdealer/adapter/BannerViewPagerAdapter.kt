package com.desynatdealer.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatdealer.databinding.ItemBannerBinding
import com.desynatdealer.ui.auth.fragment.BannerData

class BannerViewPagerAdapter(private val bannerList: List<BannerData>) :
    RecyclerView.Adapter<BannerViewPagerAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemBannerBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            ivBanner.setImageResource(bannerList[position].image)
            tvTitle.text = bannerList[position].title
            tvDescription.text = bannerList[position].description
        }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemBannerBinding.inflate(
                android.view.LayoutInflater.from(parent.context), parent, false)
        )
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.bind(position)
    }

    override fun getItemCount(): Int = bannerList.size
}

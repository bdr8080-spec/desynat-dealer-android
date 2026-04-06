package com.desynatdealer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatdealer.databinding.ItemCountryBinding
import com.desynatdealer.model.country.GetCountryResponse
import com.desynatdealer.ui.home.profile.response.GetStoryResponse

class HighlightPickerAdapter(var context:Context, var countryList: ArrayList<GetStoryResponse.Data>, var onClick:ClickOnItem) : RecyclerView.Adapter<HighlightPickerAdapter.ViewHolder>() {

    interface ClickOnItem{
        fun onClickItem(position: Int)
    }
    inner class ViewHolder(val binding: ItemCountryBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            tvCountry.text = "${countryList[position].title}"
                ivFlag.isVisible = false
                //    ivFlag.load(countryList[position].flag1)

            root.setOnClickListener {
                onClick.onClickItem(position)
            }
        }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCountryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return countryList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
     holder.bind(position)
    }

}

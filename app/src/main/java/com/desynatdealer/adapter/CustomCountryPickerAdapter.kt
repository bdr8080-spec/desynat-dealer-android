package com.desynatdealer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatdealer.databinding.ItemCountryBinding
import com.desynatdealer.model.country.GetCountryResponse
import com.desynatdealer.utils.Constants

class CustomCountryPickerAdapter(var context:Context, var countryList: ArrayList<GetCountryResponse.Data>, var onClick:ClickOnItem) : RecyclerView.Adapter<CustomCountryPickerAdapter.ViewHolder>() {

    interface ClickOnItem{
        fun onClickItem(position: Int)
    }
    inner class ViewHolder(val binding: ItemCountryBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
                tvCountry.text = "${if (Constants.language=="ar") countryList[position].nameArabic else countryList[position].name} (${countryList[position].dialCode})"
                ivFlag.load(countryList[position].flag1)

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

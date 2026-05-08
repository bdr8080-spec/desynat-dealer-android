package com.desynatdealer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatdealer.databinding.ItemCountryBinding
import com.desynatdealer.model.country.GetCountryResponse
import com.desynatdealer.utils.Constants

class CountryPickerAdapter(var context:Context, var countryList: ArrayList<GetCountryResponse.Data>,var isDial:Boolean=true,var onClick:ClickOnItem) : RecyclerView.Adapter<CountryPickerAdapter.ViewHolder>() {

    interface ClickOnItem{
        fun onClickItem(position: Int)
    }
    inner class ViewHolder(val binding: ItemCountryBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
                if (isDial){
                    tvCountry.text = "${countryList[position].flag}  ${if (Constants.language=="ar") countryList[position].nameArabic else countryList[position].name} (${countryList[position].dialCode})"}
                else{
                    tvCountry.text = "${countryList[position].flag}  ${if (Constants.language=="ar") countryList[position].nameArabic else countryList[position].name}"}

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

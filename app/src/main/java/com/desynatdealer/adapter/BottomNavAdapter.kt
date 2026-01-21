package com.desynatdealer.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatdealer.R
import com.desynatdealer.databinding.ItemBottomNavBinding
import com.desynatdealer.ui.home.BottomNavItem
import com.desynatdealer.utils.CommonMethod

class BottomNavAdapter(var context:Context, var list: ArrayList<BottomNavItem>, var bottomNavItemClick:BottomNavItemClick) : RecyclerView.Adapter<BottomNavAdapter.ViewHolder>() {

    var selected = 0
    inner class ViewHolder(val binding: ItemBottomNavBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            tvName.text = list[position].name
            ivImage.setImageResource(list[position].image)

            if (selected==position&&selected!=2){
                ivImage.setImageResource(list[position].selectedImage)
                // tvName.setTextColor(context.getColor(R.color.brown))
                CommonMethod.applyGradientText(tvName)
                tvName.typeface = ResourcesCompat.getFont(context, R.font.poppins_medium)
                ivImage.isVisible = true
                tvName.isVisible = true
                ivScanner.isVisible = false
            }
            else if (position==2){
                ivScanner.setImageResource(list[position].image)
                ivImage.isVisible = false
                tvName.isVisible = false
                ivScanner.isVisible = true
            }
            else{
                tvName.paint.shader = null
                ivImage.setImageResource(list[position].image)
                tvName.setTextColor(context.getColor(R.color.black))
                tvName.typeface = ResourcesCompat.getFont(context, R.font.poppins_regular)
                ivImage.isVisible = true
                tvName.isVisible = true
                ivScanner.isVisible = false
            }

            root.setOnClickListener {
                if (selected!=position) {
                    bottomNavItemClick.onBottomNavItemClick(position)
                    val previousSelected = selected
                    selected = position
                    notifyItemChanged(previousSelected)
                    notifyItemChanged(selected)
                }
            }
        }
        }

    interface BottomNavItemClick{
        fun onBottomNavItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemBottomNavBinding.inflate(
                android.view.LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    fun setSelectedItem(position: Int) {
        val oldPos = selected
        selected = position
        notifyItemChanged(oldPos)
        notifyItemChanged(selected)
    }

}

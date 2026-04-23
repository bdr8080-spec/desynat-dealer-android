package com.desynatdealer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.desynatdealer.R
import com.desynatdealer.databinding.ItemStaffBinding
import com.desynatdealer.ui.home.staffManagement.response.GetAllStaffResponse
import com.desynatdealer.utils.CommonMethod

class StaffAdapter(var context:Context, var staffList: ArrayList<GetAllStaffResponse.Data>, var onClick:OnClick) : RecyclerView.Adapter<StaffAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemStaffBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            var staffData = staffList[position]
            Glide.with(context).load(staffData?.profilePic).placeholder(R.drawable.ic_placeholder).into(ivProfile)
            tvName.text = staffData?.firstName
          //  tvName.text = staffData?.firstName+" "+staffData?.lastName
            tvMobile.text = staffData?.countryCode+""+staffData?.mobile
            tvPost.text = staffData?.sellerCategory?.nameEn
            tvAddOn.text = "Add on : ${CommonMethod.customizeChangeDateFormat(staffData?.createdAt.toString(), outputFormat = "dd/MM/yyyy")}"

            ivEdit.setOnClickListener {
                onClick.ClickOnItem(position,"2")
            }

            ivDelete.setOnClickListener {
                onClick.ClickOnItem(position,"3")
            }

            tvSwitch.setOnClickListener {
                onClick.ClickOnItem(position,"4")
            }

            root.setOnClickListener {
                onClick.ClickOnItem(position,"1")
            }
        }
        }

    interface OnClick{
        fun ClickOnItem(position: Int,type: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemStaffBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return staffList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }
}

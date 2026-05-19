package com.desynatdealer.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.desynatdealer.R
import com.desynatdealer.databinding.ItemStaffBinding
import com.desynatdealer.ui.home.staffManagement.response.GetAllStaffResponse
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.Constants

class StaffAdapter(var context:Context, var staffList: ArrayList<GetAllStaffResponse.Data.Data>, var onClick:OnClick) : RecyclerView.Adapter<StaffAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemStaffBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            var staffData = staffList[position]
            Glide.with(context).load(staffData?.profilePic).placeholder(R.drawable.ic_placeholder).into(ivProfile)
            tvName.text = staffData?.firstName
          //  tvName.text = staffData?.firstName+" "+staffData?.lastName
            tvMobile.text = staffData?.countryCode+"-"+staffData?.mobile
            tvPost.text = if (Constants.language=="ar") staffData?.sellerCategory?.nameAr else staffData?.sellerCategory?.nameEn
            tvAddOn.text = "${context.getString(R.string.add_on)} : ${CommonMethod.customizeChangeDateFormat(staffData?.createdAt.toString(), outputFormat = "dd/MM/yyyy")}"

            if(staffData?.isBlock==1){
                tvSwitch.text = context.getString(R.string.inactive)
                //   tvSwitch.setTextColor(Color.parseColor("#875C3A"))
                tvSwitch.setTextColor(Color.parseColor("#FF3131"))
                tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.switch_off,0,0 )
            }
            else{
                tvSwitch.text = context.getString(R.string.active)
                tvSwitch.setTextColor(Color.parseColor("#1A900D"))
                tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.switch_on,0, 0 )


            }
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

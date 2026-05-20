package com.desynatdealer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.desynatdealer.databinding.ItemRatingBinding
import com.desynatdealer.ui.home.productManagement.response.GetAllProductResponse
import com.desynatdealer.utils.CommonMethod

class ReviewAdapter(var context:Context, var reviewList: ArrayList<GetAllProductResponse.Data.AllProductData.Review>) : RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemRatingBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val data = reviewList[position]
            Glide.with(context).load(data?.userDetails?.profileImage).into(ivUser)
            tvName.text = data?.userDetails?.fullName
            tvReviews.text = data?.productReview
            ratingBar.rating = data?.productRating?.toFloatOrNull()?:0f
            tvDate.text = CommonMethod.customizeChangeDateFormat(data?.createdAt.toString(), outputFormat = "dd MMM yyyy")
            if (data.productImage.isNullOrEmpty()) {
                rvReview.visibility = ViewGroup.GONE
            } else {

                rvReview.visibility = ViewGroup.VISIBLE

                rvReview.layoutManager =
                    LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)

                rvReview.adapter = ReviewImageAdapter(
                    context,
                    ArrayList(data.productImage)
                )
            }
        }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemRatingBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return reviewList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }


}

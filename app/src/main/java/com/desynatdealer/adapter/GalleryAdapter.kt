package com.desynatdealer.adapter

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bumptech.glide.Glide
import com.desynatdealer.R
import com.desynatdealer.databinding.ItemGalleryImgBinding
import com.desynatdealer.ui.home.profile.response.GalleryMedia

class GalleryAdapter(var context:Context, private val items: MutableList<GalleryMedia>, private val listener: (GalleryMedia) -> Unit) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {
    inner class ViewHolder(private val binding: ItemGalleryImgBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GalleryMedia) = with(binding) {
            ivGalleryImage.setImageDrawable(null)
            ivPlay.isVisible = false
            when (item) {
                is GalleryMedia.Image -> {
                    ivPlay.isVisible = false
                    Glide.with(context)
                        .load(item.uri)
                        .placeholder(R.drawable.ic_placeholder)
                        .into(ivGalleryImage)
                }

                is GalleryMedia.Video -> {
                    ivPlay.isVisible = true
                    Glide.with(context)
                        .load(item.uri)
                 //       .frame(1000000)
                        .placeholder(R.drawable.ic_placeholder)
                        .into(ivGalleryImage)

                }
            }

            root.setOnClickListener {
                listener(item)
            }
        }
    }
    interface OnClick{
        fun onClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemGalleryImgBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun addItems(newItems: List<GalleryMedia>) {
        val start = items.size
        items.addAll(newItems)
        notifyItemRangeInserted(start, newItems.size)
    }
}

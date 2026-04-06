package com.desynatdealer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.desynatdealer.R
import com.desynatdealer.databinding.ItemGalleryImgBinding
import com.desynatdealer.ui.home.profile.response.GetStoryResponse

class PreviousHighlightsAdapter(
    val context: Context,
    private val list: List<GetStoryResponse.Data>,
    private val onClick: (GetStoryResponse.Data) -> Unit
) : RecyclerView.Adapter<PreviousHighlightsAdapter.VH>() {

    inner class VH(val binding: ItemGalleryImgBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            ItemGalleryImgBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = list[position]
        val url = item.file ?: ""

            Glide.with(context)
                .load(url)
                .into(holder.binding.ivGalleryImage)

        val isVideo = url.endsWith(".mp4") || url.endsWith(".mov") ||url.endsWith(".avi") || url.endsWith(".mkv")
        val isGif = url.endsWith(".gif")

            holder.binding.ivPlay.isVisible = isVideo

        holder.itemView.setOnClickListener {
            onClick(item)
        }
    }
}
package com.desynatdealer.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.desynatdealer.R
import com.desynatdealer.databinding.ItemPreviewImageBinding
import kotlin.div
import kotlin.text.compareTo
import kotlin.text.toFloat
import kotlin.text.toInt
import kotlin.times

class ImagePreviewAdapter(var context:Context, var productList: ArrayList<String>) : RecyclerView.Adapter<ImagePreviewAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemPreviewImageBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val productData = productList[position]

            ivPlay.setOnClickListener {
                ivPlay.isVisible = false
                videoView.seekTo(0)
                videoView.start()
            }
            val url = productList[position] ?: ""
            if (url.endsWith(".mp4") || url.endsWith(".mov") ||url.endsWith(".avi") || url.endsWith(".mkv") || url.endsWith(".gif")) {
                ivProduct.isVisible = false
                videoView.isVisible = true
                progressBar.isVisible = true
                videoView.setVideoURI(Uri.parse(productData))
                videoView.requestFocus()

                videoView.setOnPreparedListener { mp ->

                    val videoWidth = mp.videoWidth
                    val videoHeight = mp.videoHeight

                    val viewWidth = videoView.width
                    val viewHeight = videoView.height

                    val videoRatio = videoWidth.toFloat() / videoHeight
                    val viewRatio = viewWidth.toFloat() / viewHeight

                    val layoutParams = videoView.layoutParams

                    if (videoRatio > viewRatio) {
                        layoutParams.height = viewHeight
                        layoutParams.width = (viewHeight * videoRatio).toInt()
                    } else {
                        layoutParams.width = viewWidth
                        layoutParams.height = (viewWidth / videoRatio).toInt()
                    }

                    videoView.layoutParams = layoutParams

                    progressBar.isVisible = false
                    mp.isLooping = false
                    videoView.start()
                }

                videoView.setOnCompletionListener {
                    ivPlay.isVisible = true
                }

                videoView.setOnInfoListener { mp, what, extra ->
                    when (what) {
                        android.media.MediaPlayer.MEDIA_INFO_BUFFERING_START -> progressBar.isVisible = true
                        android.media.MediaPlayer.MEDIA_INFO_BUFFERING_END -> progressBar.isVisible = false
                    }
                    false
                }
            }

            else{
                ivProduct.isVisible = true
                videoView.isVisible = false
                ivPlay.isVisible = false
                Glide.with(context).load(productData).placeholder(R.drawable.ic_placeholder).into(ivProduct)
            }
        }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPreviewImageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)

    }


}

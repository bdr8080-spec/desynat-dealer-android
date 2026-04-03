package com.desynatdealer.adapter

import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.desynatdealer.ui.home.profile.response.GetStoryResponse
import com.desynatdealer.databinding.ItemStoryBinding
import kotlin.div
import kotlin.text.toFloat
import kotlin.text.toInt
import kotlin.times

class StoryPagerAdapter(
    private val stories: List<GetStoryResponse.Data>,private val click:OnClick,private val onVideoReady: (position: Int, duration: Long) -> Unit
    , private val onVideoFinished: (Int) -> Unit
) : RecyclerView.Adapter<StoryPagerAdapter.StoryViewHolder>() {

    private var pressStartTime = 0L
    private val LONG_PRESS_THRESHOLD = 200L
    interface OnClick {
        fun clickOnItem(position: Int)
        fun onNext(position: Int)
        fun onPrevious(position: Int)
        fun onPause(position: Int)
        fun onResume(position: Int)
    }

    inner class StoryViewHolder(val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):StoryViewHolder {
        return StoryViewHolder(
            ItemStoryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int = stories.size

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.binding.videoStory.stopPlayback()
        val story = stories[position]
        val url = story.file ?: ""

        val isVideo = url.endsWith(".mp4") || url.endsWith(".mov") ||url.endsWith(".avi") || url.endsWith(".mkv")
        val isGif = url.endsWith(".gif")

        if (isVideo) {
            holder.binding.videoLoader.isVisible = true
            holder.binding.ivStory.isVisible = false
            holder.binding.videoStory.isVisible = true

            holder.binding.videoStory.setVideoPath(url)

            holder.binding.videoStory.setOnPreparedListener { mp ->
                val videoWidth = mp.videoWidth
                val videoHeight = mp.videoHeight

                val viewWidth = holder.binding.videoStory.width
                val viewHeight = holder.binding.videoStory.height

                val videoProportion = videoWidth.toFloat() / videoHeight.toFloat()
                val viewProportion = viewWidth.toFloat() / viewHeight.toFloat()

                val layoutParams = holder.binding.videoStory.layoutParams

                if (videoProportion > viewProportion) {
                    layoutParams.width = viewWidth
                    layoutParams.height = (viewWidth / videoProportion).toInt()
                } else {
                    layoutParams.height = viewHeight
                    layoutParams.width = (viewHeight * videoProportion).toInt()
                }

                holder.binding.videoStory.layoutParams = layoutParams


                mp.isLooping = false
                holder.binding.videoLoader.isVisible = false
                val duration = mp.duration.toLong()

                onVideoReady(position, duration)
                holder.binding.videoStory.start()
            }

            holder.binding.videoStory.setOnInfoListener { _, what, _ ->

                when (what) {

                    MediaPlayer.MEDIA_INFO_BUFFERING_START -> {
                        holder.binding.videoLoader.isVisible = true
                    }

                    MediaPlayer.MEDIA_INFO_BUFFERING_END -> {
                        holder.binding.videoLoader.isVisible = false
                    }
                }

                false
            }

        } else {
            holder.binding.videoLoader.isVisible = false
            holder.binding.ivStory.isVisible = true
            holder.binding.videoStory.isVisible = false

            if (isGif) {
                Glide.with(holder.itemView.context)
                    .asGif()
                    .load(url)
                    .into(holder.binding.ivStory)
            } else {
                Glide.with(holder.itemView.context)
                    .load(url)
                    .into(holder.binding.ivStory)
            }
        }

        if (position + 1 < stories.size) {

            val nextUrl = stories[position + 1].file ?: ""

            Glide.with(holder.itemView.context)
                .load(nextUrl)
                .preload()
        }

        if (!story.title.isNullOrEmpty() && story.title != "null") {
            holder.binding.tvTitle.isVisible = true
            holder.binding.tvTitle.text = story.title
        } else {
            holder.binding.tvTitle.isVisible = false
        }

        holder.binding.videoStory.setOnCompletionListener {
            onVideoFinished(position)
        }



        holder.binding.rightClick.setOnTouchListener { _, event ->
            when (event.action) {

                MotionEvent.ACTION_DOWN -> {
                    pressStartTime = System.currentTimeMillis()
                    holder.binding.videoStory.pause()
                    click.onPause(position)
                    true
                }

                MotionEvent.ACTION_UP -> {
                    val pressDuration = System.currentTimeMillis() - pressStartTime

                    holder.binding.videoStory.start()
                    click.onResume(position)

                    // ✅ ONLY SHORT TAP triggers next
                    if (pressDuration < LONG_PRESS_THRESHOLD) {
                        click.onNext(position)
                    }

                    true
                }

                MotionEvent.ACTION_CANCEL -> {
                    holder.binding.videoStory.start()
                    click.onResume(position)
                    true
                }

                else -> false
            }
        }

        holder.binding.leftClick.setOnTouchListener { _, event ->
            when (event.action) {

                MotionEvent.ACTION_DOWN -> {
                    pressStartTime = System.currentTimeMillis()
                    holder.binding.videoStory.pause()
                    click.onPause(position)
                    true
                }

                MotionEvent.ACTION_UP -> {
                    val pressDuration = System.currentTimeMillis() - pressStartTime

                    holder.binding.videoStory.start()
                    click.onResume(position)

                    // ✅ ONLY SHORT TAP triggers previous
                    if (pressDuration < LONG_PRESS_THRESHOLD) {
                        click.onPrevious(position)
                    }

                    true
                }

                MotionEvent.ACTION_CANCEL -> {
                    holder.binding.videoStory.start()
                    click.onResume(position)
                    true
                }

                else -> false
            }
        }

//        holder.binding.leftClick.setOnClickListener {
//            click.onPrevious(position)
//        }

        holder.binding.ivDelete.setOnClickListener{
            click.clickOnItem(position)
        }
    }

    override fun onViewRecycled(holder: StoryViewHolder) {
        super.onViewRecycled(holder)
        holder.binding.videoStory.stopPlayback()
    }
}
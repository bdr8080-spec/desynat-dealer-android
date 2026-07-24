package com.desynatdealer.ui.home.profile

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.desynatdealer.R
import com.desynatdealer.adapter.StoryPagerAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentViewStoryBinding
import com.desynatdealer.ui.home.profile.response.GetStoryResponse
import com.desynatdealer.ui.home.profile.response.GetStoryResponseNew
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.SharedPreference
import com.desynatdealer.utils.showToast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue
import kotlin.text.get

@AndroidEntryPoint
class ViewStoryFragment : BaseFragment() {

    private var binding: FragmentViewStoryBinding? = null
    private val viewModel: HomeViewModel by activityViewModels()

    private val myStories = ArrayList<GetStoryResponseNew.Data.SellerHighlight>()
    private val progressBars = ArrayList<ProgressBar>()

    private var storyPagerAdapter: StoryPagerAdapter? = null

    private var progressAnimator: ValueAnimator? = null
    private var isManualNavigation = false
    private var from = "admin"
    private var sellerId: String=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewStoryBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
        initControl()
        observe()
    }

    override fun initView() {


        from = arguments?.getString("from").toString()

        val json = arguments?.getString("storyData")

        if (!json.isNullOrEmpty()) {
            myStories.clear()
            try {
                if (from == "highlights") {
                    val type = object : TypeToken<ArrayList<GetStoryResponseNew.Data>>() {}.type
                    val dataList: ArrayList<GetStoryResponseNew.Data> = Gson().fromJson(json, type)
                    dataList.forEach { data ->
                        data.highlights?.forEach { highlight ->
                            myStories.add(highlight.copy(title = data.title ?: highlight.title))
                        }
                    }
                } else {
                    val type =
                        object : TypeToken<ArrayList<GetStoryResponseNew.Data.SellerHighlight>>() {}.type
                    val storyList: ArrayList<GetStoryResponseNew.Data.SellerHighlight> =
                        Gson().fromJson(json, type)
                    myStories.addAll(storyList)
                }
            } catch (e: Exception) {
                try {
                    val typeOld = object : TypeToken<ArrayList<GetStoryResponse.Data>>() {}.type
                    val oldStoryList: ArrayList<GetStoryResponse.Data> = Gson().fromJson(json, typeOld)
                    oldStoryList.forEach { old ->
                        myStories.add(
                            GetStoryResponseNew.Data.SellerHighlight(
                                id = old.id?.toIntOrNull() ?: 0,
                                file = old.file ?: "",
                                title = old.title ?: "",
                                type = old.type?.toIntOrNull() ?: 1,
                                sellerId = old.sellerId?.toIntOrNull() ?: 0,
                                status = old.status?.toIntOrNull() ?: 1,
                                thumbnail = old.thumbnail,
                                createdAt = old.createdAt ?: "",
                                updatedAt = old.updatedAt ?: "",
                                isSeen = old.isSeen,
                                caption = old.caption ?: ""
                            )
                        )
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }

        setupAdapter()
        setupPagerCallback()
        setData()
    }

    override fun initControl() {
        binding?.apply {
            ivCancel.setOnClickListener { findNavController().popBackStack() }
            ivDelete.setOnClickListener {
                val position = viewPager.currentItem
                if (position < myStories.size) {
                    progressAnimator?.pause()
                    val recyclerView = viewPager.getChildAt(0) as? RecyclerView
                    val viewHolder =
                        recyclerView?.findViewHolderForAdapterPosition(position) as? StoryPagerAdapter.StoryViewHolder
                    viewHolder?.binding?.videoStory?.pause()

                    when (from) {
                        "story" -> deleteStory(myStories[position].id.toString())
                        "highlights" -> deleteHighlights(myStories[position].id.toString())
                        "reel" -> deleteReel(myStories[position].id.toString())
                    }
                }
            }
        }
    }

    override fun observe() {
        viewModel.deleteStoryResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    CommonMethod.showSuccessDialog(requireActivity(),
                        getString(R.string.story_deleted_successfully)){
                        findNavController().popBackStack()
                    }
                    viewModel.deleteStoryResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.deleteStoryResponse.value = null
                }

                else -> {}
            }
        }

        viewModel.deleteHighlightsResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    CommonMethod.showSuccessDialog(requireActivity(),
                        getString(R.string.highlights_deleted_successfully)){
                        findNavController().popBackStack()
                    }
                    viewModel.deleteHighlightsResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.deleteHighlightsResponse.value = null
                }

                else -> {}
            }
        }
        viewModel.deleteReelResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    CommonMethod.showSuccessDialog(requireActivity(),getString(R.string.reel_deleted_successfully)){
                        findNavController().popBackStack()
                    }
                    viewModel.deleteReelResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.deleteReelResponse.value = null
                }

                else -> {}
            }
        }
    }

    private fun handleStoryDeletion() {

        val position = binding?.viewPager?.currentItem ?: return

        if (position < myStories.size) {
            myStories.removeAt(position)
            storyPagerAdapter?.notifyDataSetChanged()
            setupProgressBars(myStories.size)
        }

        if (myStories.isEmpty()) {
            findNavController().popBackStack()
            return
        }

        val nextPos = if (position < myStories.size) position else myStories.size - 1
        binding?.viewPager?.setCurrentItem(nextPos, false)

        progressBars.forEachIndexed { index, progressBar ->
            progressBar.progress = if (index < nextPos) 100 else 0
        }

        startProgress(nextPos)
    }

    private fun deleteStory(id: String){
        val map = HashMap<String,Any>()
        map["story_id"] = id
        viewModel.deleteStory(map)
    }

    private fun deleteHighlights(id: String){
        val map = HashMap<String,Any>()
        map["highlight_id"] = id
        viewModel.deleteHighlights(map)
    }

    private fun deleteReel(id: String){
        val map = HashMap<String,Any>()
        map["reel_id"] = id
        viewModel.deleteReel(map)
    }

    private fun seeStory(id: String){
        val map = HashMap<String, Any>()
        map["story_id"] = id
        map["type"] = 2
        map["user_id"] = SharedPreference.get(requireActivity()).getUser()?.id.toString()
        viewModel.seeStory(map)
    }

            private fun setupAdapter() {

                storyPagerAdapter = StoryPagerAdapter(requireActivity(),
                    myStories,
                    object : StoryPagerAdapter.OnClick {

                        override fun clickOnItem(position: Int) {
                            // Handled by fragment's ivDelete now
                        }


                        override fun onNext(position: Int) {
                            isManualNavigation = true

                            stopProgress()

                            val current = binding?.viewPager?.currentItem ?: return
                            val next = current + 1

                            if (next < myStories.size) {
                                binding?.viewPager?.currentItem = next
                            } else {
                                requireActivity().onBackPressedDispatcher.onBackPressed()
                            }
                        }

                        override fun onPrevious(position: Int) {
                            isManualNavigation = true

                            stopProgress()

                            val current = binding?.viewPager?.currentItem ?: return
                            val prev = current - 1

                            if (prev >= 0) {
                                binding?.viewPager?.currentItem = prev
                            }
                        }

                        override fun onPause(position: Int) {
                            progressAnimator?.pause()
                        }

                        override fun onResume(position: Int) {
                            progressAnimator?.resume()
                        }
                    },
                    from,

                    onVideoReady = { position, duration ->
                        startVideoProgress(position, duration)
                    },

                    onVideoFinished = { position ->

                        if (isManualNavigation) {
                            isManualNavigation = false
                            return@StoryPagerAdapter
                        }

                        goToNext(position)
                    }
                )

                binding?.viewPager?.adapter = storyPagerAdapter
            }


            private fun setupPagerCallback() {
                binding?.viewPager?.registerOnPageChangeCallback(
                    object : ViewPager2.OnPageChangeCallback() {

                        override fun onPageSelected(position: Int) {
                            super.onPageSelected(position)

                            stopProgress()

                            progressBars.forEachIndexed { index, progressBar ->
                                progressBar.progress = if (index < position) 100 else 0
                            }
                            val story = myStories[position]

                            if ((from == "story" || from == "admin") && story.isSeen == 0) {
                                seeStory(story.id.toString())
                                story.isSeen = 1
                            }
                            startProgress(position)
                        }
                    }
                )
            }


            private fun stopProgress() {
                progressAnimator?.removeAllListeners()
                progressAnimator?.cancel()
            }

            private fun startProgress(index: Int) {

                if (progressBars.isEmpty() || index >= progressBars.size) return
                isManualNavigation = false
                val url = myStories[index].file ?: ""

                // Skip video
                if (url.endsWith(".mp4") || url.endsWith(".mov") || url.endsWith(".avi") || url.endsWith(".mkv")) {
                    return
                }

                val storyDuration = if (url.endsWith(".gif")) 5000L else 7000L

                progressAnimator = ValueAnimator.ofInt(0, 100).apply {
                    duration = storyDuration

                    addUpdateListener {
                        if (index < progressBars.size) {
                            progressBars[index].progress = it.animatedValue as Int
                        }
                    }

                    doOnEnd {

                        if (!isAdded) return@doOnEnd

                        if (isManualNavigation) {
                            isManualNavigation = false
                            return@doOnEnd
                        }

                        goToNext(index)
                    }

                    start()
                }
            }

            private fun startVideoProgress(index: Int, duration: Long) {

                stopProgress()
                isManualNavigation = false
                progressAnimator = ValueAnimator.ofInt(0, 100).apply {
                //    this.duration = duration

                    val safeDuration = if (duration > 0) duration else 10000L

                    this.duration = safeDuration
                    addUpdateListener {
                        progressBars[index].progress = it.animatedValue as Int
                    }

                    doOnEnd {

                        if (!isAdded) return@doOnEnd

                        if (isManualNavigation) {
                            isManualNavigation = false
                            return@doOnEnd
                        }

                        goToNext(index)
                    }

                    start()
                }
            }


            private fun goToNext(position: Int) {
                val next = position + 1

                if (next < myStories.size) {
                    binding?.viewPager?.currentItem = next
                } else {
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }


    private fun setData() {
        binding?.ivDelete?.isVisible = from != "admin"
        if (myStories.isNotEmpty()) {

            setupProgressBars(myStories.size)

            val startPosition = myStories.indexOfFirst { it.isSeen == 0 }
                .takeIf { it != -1 } ?: 0

            binding?.viewPager?.setCurrentItem(startPosition, false)

            val story = myStories[startPosition]

            if ((from == "story" || from == "admin") && story.isSeen == 0) {
                seeStory(story.id.toString())
                story.isSeen = 1
            }

            progressBars.forEachIndexed { index, progressBar ->
                progressBar.progress = if (index < startPosition) 100 else 0
            }

            startProgress(startPosition)
        }
    }

            private fun setupProgressBars(count: Int) {

                binding?.layoutProgress?.removeAllViews()
                progressBars.clear()

                for (i in 0 until count) {

                    val progressBar = ProgressBar(
                        requireContext(),
                        null,
                        android.R.attr.progressBarStyleHorizontal
                    )

                    progressBar.progressDrawable =
                        ContextCompat.getDrawable(requireContext(), R.drawable.story_progress)

                    val params = LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1f
                    )
                    if (i < count - 1) {
                        params.marginEnd = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._5sdp)
                    }
                    progressBar.layoutParams = params

                    progressBar.max = 100
                    progressBar.progress = 0

                    binding?.layoutProgress?.addView(progressBar)
                    progressBars.add(progressBar)
                }
            }

            override fun onDestroyView() {
                super.onDestroyView()
                stopProgress()
                binding = null
            }
        }

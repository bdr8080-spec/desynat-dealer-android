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
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.showToast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class ViewStoryFragment : BaseFragment() {

    private var binding: FragmentViewStoryBinding? = null
    private val viewModel: HomeViewModel by activityViewModels()

    private val myStories = ArrayList<GetStoryResponse.Data>()
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
            val type = object : TypeToken<ArrayList<GetStoryResponse.Data>>() {}.type
            val storyList: ArrayList<GetStoryResponse.Data> =
                Gson().fromJson(json, type)

            myStories.clear()
            myStories.addAll(storyList)
        }

        setupAdapter()
        setData()
        setupPagerCallback()
    }

    override fun initControl() {
        binding?.apply { ivCancel.setOnClickListener { findNavController().popBackStack() } }
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
        }

        if (myStories.isEmpty()) {
            findNavController().popBackStack()
            return
        }

        if (position < myStories.size) {
            binding?.viewPager?.currentItem = position
        } else {
            binding?.viewPager?.currentItem = myStories.size - 1
        }

        startProgress(binding?.viewPager?.currentItem ?: 0)
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

            private fun setupAdapter() {

                storyPagerAdapter = StoryPagerAdapter(requireActivity(),
                    myStories,
                    object : StoryPagerAdapter.OnClick {

                        override fun clickOnItem(position: Int) {
                            progressAnimator?.pause()
                            val recyclerView = binding?.viewPager?.getChildAt(0) as? RecyclerView
                            val viewHolder = recyclerView ?.findViewHolderForAdapterPosition(position) as? StoryPagerAdapter.StoryViewHolder
                            viewHolder?.binding?.videoStory?.pause()
                            when (from) {
                                "story" -> deleteStory(myStories[position].id.toString())
                                "highlights" -> deleteHighlights(myStories[position].id.toString())
                                "reel" -> deleteReel(myStories[position].id.toString()) }
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

                val url = myStories[index].file ?: ""

                // Skip video/gif
                if (url.endsWith(".mp4") || url.endsWith(".mov") ||url.endsWith(".avi") || url.endsWith(".mkv") || url.endsWith(".gif")) {
                    return
                }

                progressAnimator = ValueAnimator.ofInt(0, 100).apply {
                    duration = 10000L

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

            private fun startVideoProgress(index: Int, duration: Long) {

                stopProgress()

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
                if (myStories.isNotEmpty()) {
                    setupProgressBars(myStories.size)
                    startProgress(0)
                }
            }

            private fun setupProgressBars(count: Int) {

                binding?.layoutProgress?.removeAllViews()
                progressBars.clear()

                repeat(count) {

                    val progressBar = ProgressBar(
                        requireContext(),
                        null,
                        android.R.attr.progressBarStyleHorizontal
                    )

                    progressBar.progressDrawable =
                        ContextCompat.getDrawable(requireContext(), R.drawable.story_progress)

                    progressBar.layoutParams = LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1f
                    ).apply {
                        marginEnd = 8
                    }

                    progressBar.max = 100

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

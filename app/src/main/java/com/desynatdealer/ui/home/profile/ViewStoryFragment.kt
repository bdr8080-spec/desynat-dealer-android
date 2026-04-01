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
    private var binding : FragmentViewStoryBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    private var isObserver = false
    private val myStories = ArrayList<GetStoryResponse.Data>()

    private val progressBars = ArrayList<ProgressBar>()
    private var storyPagerAdapter: StoryPagerAdapter?=null
    private var from = "admin"
    private var sellerId: String=""

    private var progressAnimator: ValueAnimator? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentViewStoryBinding.inflate(layoutInflater, container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        if (from!="admin"){
            sellerId = arguments?.getString("sellerId").toString()
        }
//        when(from){
//            "admin"->getAdminStory()
//            "reel"-> getSellerReel()
//            "story"->getSellerStory()
//            "highlights"->getSellerHighlights()
//        }

        storyPagerAdapter = StoryPagerAdapter(
            myStories,object:StoryPagerAdapter.OnClick{
                override fun clickOnItem(position: Int) {
                    progressAnimator?.pause()

                    val recyclerView = binding?.viewPager?.getChildAt(0) as? RecyclerView

                    val viewHolder = recyclerView
                        ?.findViewHolderForAdapterPosition(position) as? StoryPagerAdapter.StoryViewHolder

                    viewHolder?.binding?.videoStory?.pause()

                    when (from) {
                        "story" -> deleteStory(myStories[position].id.toString())
                        "highlights" -> deleteHighlights(myStories[position].id.toString())
                    }
                }
            },
            onVideoReady = { position, duration ->
                startVideoProgress(position, duration)
            },
            onVideoFinished = { position ->
                val next = position + 1
                if (next < myStories.size) {
                    binding?.viewPager?.currentItem = next
                } else {
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        )
        binding?.viewPager?.adapter = storyPagerAdapter

        setData()

        binding?.viewPager?.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    if (progressBars.isEmpty()) return

                    // cancel old animation safely
                    progressAnimator?.removeAllListeners()
                    progressAnimator?.cancel()

                    progressBars.forEachIndexed { index, progressBar ->
                        progressBar.progress = if (index < position) 100 else 0
                    }

                    startProgress(position)
                }
            }
        )


    }

    override fun initControl() {
        binding?.apply {
            ivCancel.setOnClickListener {
                findNavController().popBackStack()
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
                    CommonMethod.showSuccessDialog(requireActivity(),"Story deleted successfully!"){
                        findNavController().popBackStack()
                    }
                  //  handleStoryDeletion()
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
                    CommonMethod.showSuccessDialog(requireActivity(),"Highlights deleted successfully!"){
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

    private fun setupProgressBars(count: Int) {

        binding?.layoutProgress?.removeAllViews()
        progressBars.clear()

        repeat(count) { index ->

            val progressBar = ProgressBar(
                requireContext(),
                null,
                android.R.attr.progressBarStyleHorizontal
            )

            progressBar.progressDrawable =
                ContextCompat.getDrawable(requireContext(), R.drawable.story_progress)

            val params = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT
            )

            params.weight = 1f
            params.marginEnd = 8

            progressBar.layoutParams = params
            progressBar.max = 100

            binding?.layoutProgress?.addView(progressBar)

            progressBars.add(progressBar)
        }
    }


    private fun startProgress(index: Int) {

        if (progressBars.isEmpty() || index >= progressBars.size) return

        val story = myStories[index]
        val url = story.file ?: ""



        if (url.endsWith(".mp4") || url.endsWith(".mov") || url.endsWith(".gif")) {
            return
        }

        val duration = 10000L

        progressAnimator?.cancel()

        if (duration > 0) {

            progressAnimator = ValueAnimator.ofInt(0, 100)
            progressAnimator?.duration = duration

            progressAnimator?.addUpdateListener {
                progressBars[index].progress = it.animatedValue as Int
            }

            progressAnimator?.doOnEnd {

                if (!isAdded) return@doOnEnd

                val nextIndex = index + 1

                if (nextIndex < progressBars.size) {
                    binding?.viewPager?.currentItem = nextIndex
                } else {
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }

            progressAnimator?.start()
        }
    }

    private fun setData(){
        if (myStories.isNotEmpty()) {
            setupProgressBars(myStories.size)
            startProgress(0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        progressAnimator?.cancel()
        binding = null
    }

    private fun startVideoProgress(index: Int, duration: Long) {

        progressAnimator?.cancel()

        progressAnimator = ValueAnimator.ofInt(0, 100)
        progressAnimator?.duration = duration

        progressAnimator?.addUpdateListener {
            progressBars[index].progress = it.animatedValue as Int
        }

        progressAnimator?.doOnEnd {

            if (!isAdded) return@doOnEnd

            val nextIndex = index + 1

            if (nextIndex < progressBars.size) {
                binding?.viewPager?.currentItem = nextIndex
            } else {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        progressAnimator?.start()
    }
}
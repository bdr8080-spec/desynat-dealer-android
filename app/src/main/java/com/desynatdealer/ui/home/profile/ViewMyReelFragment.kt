package com.desynatdealer.ui.home.profile

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.desynatdealer.R
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentViewMyReelBinding
import com.desynatdealer.ui.home.profile.response.GetStoryResponse
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class ViewMyReelFragment : BaseFragment() {
    private var binding : FragmentViewMyReelBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    private var reelData: GetStoryResponse.Data?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentViewMyReelBinding.inflate(layoutInflater, container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        ProgressDialogUtils.getInstance().hideProgress()
        val itemJson = arguments?.getString("storyData")

        if (!itemJson.isNullOrEmpty()) {
            reelData = Gson().fromJson(itemJson, GetStoryResponse.Data::class.java)
        }
        binding?.apply {


            val url = reelData?.file ?: ""

            val isVideo = url.endsWith(".mp4") || url.endsWith(".mov")
            val isGif = url.endsWith(".gif")

            if (isVideo) {
                videoLoader.isVisible = true
                ivStory.isVisible = false
                videoStory.isVisible = true

                videoStory.setVideoPath(url)

                videoStory.setOnPreparedListener { mp ->
                    mp.isLooping = false
                    videoLoader.isVisible = false

                    videoStory.start()
                }

                videoStory.setOnInfoListener { _, what, _ ->

                    when (what) {

                        android.media.MediaPlayer.MEDIA_INFO_BUFFERING_START -> {
                            videoLoader.isVisible = true
                            ivPlay.setImageResource(R.drawable.ic_pause)
                        }

                        android.media.MediaPlayer.MEDIA_INFO_BUFFERING_END -> {
                            videoLoader.isVisible = false
                            ivPlay.setImageResource(R.drawable.ic_pause)
                        }
                    }

                    false
                }

            } else {
                ivPlay.isVisible = false
                videoLoader.isVisible = false
                ivStory.isVisible = true
                videoStory.isVisible = false

                if (isGif) {
                    Glide.with(requireActivity())
                        .asGif()
                        .load(url)
                        .into(ivStory)
                } else {
                    Glide.with(requireActivity())
                        .load(url)
                        .into(ivStory)
                }
            }


            if (!reelData?.title.isNullOrEmpty() && reelData?.title != "null") {
                tvTitle.isVisible = true
                tvTitle.text = reelData?.title
            } else {
                tvTitle.isVisible = true
            }

            tvDate.text = "Added On ${CommonMethod.customizeChangeDateFormat(reelData?.createdAt.toString(), outputFormat = "dd/MM/yyyy")}"

            videoStory.setOnCompletionListener {
                ivPlay.setImageResource(R.drawable.ic_play)

            }
        }
    }

    override fun initControl() {
        binding?.apply {
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            ivDelete.setOnClickListener {
                deleteReel(reelData?.id.toString())
            }

            ivEdit.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("from", "reel")
                    putString("reelData", Gson().toJson(reelData))
                }
                findNavController().navigate(R.id.action_viewReelFragment_to_AddNewReelFragment,bundle)
              //  findNavController().navigate(R.id.action_viewReelFragment_to_AddReelFragment,bundle)
            }

            tvSwitch.setOnClickListener {
                updateReelStatus(reelData?.id.toString())
            }
        }
    }

    override fun observe() {

        viewModel.updateReelStatusResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    reelData?.status = result?.data?.data?.status
                    updateStatus()
                    CommonMethod.showSuccessDialog(requireActivity(),result?.data?.message.toString()){

                    }
                    viewModel.updateReelStatusResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.updateReelStatusResponse.value = null
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
                    CommonMethod.showSuccessDialog(requireActivity(),"Reel deleted successfully!"){
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

    private fun updateStatus(){
        binding?.apply {
            if(reelData?.status=="1"){
                tvSwitch.text = "Active"
                tvSwitch.setTextColor(Color.parseColor("#000000"))
                tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.switch_on,0)
            }
            else{
                tvSwitch.text = "Inactive"
                tvSwitch.setTextColor(Color.parseColor("#875C3A"))
                tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.switch_off,0 )
            }
        }
    }

    private fun updateReelStatus(id: String){
        val map = HashMap<String,Any>()
        map["reel_id"] = id
        viewModel.updateReelStatus(map)
    }

    private fun deleteReel(id: String){
        val map = HashMap<String,Any>()
        map["reel_id"] = id
        viewModel.deleteReel(map)
    }

}
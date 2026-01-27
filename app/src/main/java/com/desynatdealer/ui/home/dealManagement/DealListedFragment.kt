package com.desynatdealer.ui.home.dealManagement

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.adapter.DealAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.DealFilterDialogBinding
import com.desynatdealer.databinding.FragmentDealListedBinding
import com.desynatdealer.ui.home.dealManagement.response.GetAllDealResponse
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.openCustomDatePicker
import com.desynatdealer.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DealListedFragment : BaseFragment() {
    private var binding : FragmentDealListedBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    private var dealAdapter: DealAdapter?=null
    private var dealList = ArrayList<GetAllDealResponse.Data.ListedDeal>()
    private var dealType = "1"

    private var fromTimestamp: Long? = null
    private var toTimestamp: Long? = null
    private var fromDateText: String? = null
    private var toDateText: String? = null
    private var tab: String? = null

    private var search: Job? = null
    private var lastQuery: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDealListedBinding.inflate(layoutInflater, container,false)
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
        binding?.apply {

            findNavController()
                .currentBackStackEntry
                ?.savedStateHandle
                ?.getLiveData<String>("deal_type")
                ?.observe(viewLifecycleOwner) { type ->
                    dealType = type
                    enrolled()
                }

            toolbar.tvToolbar.text = "Listed Deals"

            etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                }

                override fun afterTextChanged(s: Editable?) {
                    val query = s.toString().trim()
                    if (query == lastQuery) return
                    lastQuery = query
                    search?.cancel()
                    search = lifecycleScope.launch {
                        delay(600)
                        getDeal()
                       // dealAdapter?.filter?.filter(lastQuery)
                    }
                }
            })

            dealList.clear()
            dealAdapter = DealAdapter(requireActivity(),dealList,object : DealAdapter.OnClick{
                override fun onItemClick(position: Int, type: Int) {
                    val bundle = Bundle().apply {
                        putString("dealData", Gson().toJson(dealList[position]))
                        putString("dealType", dealType)
                    }
                    findNavController().navigate(R.id.action_dealListedFragment_to_DealDetailFragment,bundle)
                }

            },
                object : DealAdapter.OnFilterResult {
                    override fun onResult(isEmpty: Boolean) {
                        binding?.tvNoDataFound?.isVisible = isEmpty
                    }
                })
            rvDeal.adapter = dealAdapter

            when(dealType){
                "1"->{
                    listedDeal()
                }
                "2"->{
                   enrolled()
                }
                "3"->{
                    expired()
                }
            }

            getDeal()
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            tvListed.setOnClickListener {
               listedDeal()
            }

            tvEnrolled.setOnClickListener {
               enrolled()
            }

            tvExpired.setOnClickListener {
                expired()
            }

            ivFilter.setOnClickListener {
                dealFilterDialog()
            }

        }
    }

    private fun listedDeal() {
        binding?.apply {
            dealType = "1"
            clSearch.isVisible = true
            getDeal()
            tvListed.setTextColor(requireActivity().getColor(R.color.white))
            tvEnrolled.setTextColor(requireActivity().getColor(R.color.black))
            tvExpired.setTextColor(requireActivity().getColor(R.color.black))

            tvListed.setBackgroundResource(R.drawable.selected_left_tab)
            tvEnrolled.background = null
            tvExpired.background = null
        }
    }

    private fun expired() {
        binding?.apply {
            clSearch.isVisible = true
            dealType = "3"
            getDeal()
            tvListed.setTextColor(requireActivity().getColor(R.color.black))
            tvEnrolled.setTextColor(requireActivity().getColor(R.color.black))
            tvExpired.setTextColor(requireActivity().getColor(R.color.white))

            tvExpired.setBackgroundResource(R.drawable.selected_right_tab)
            tvEnrolled.background = null
            tvListed.background = null
        }
    }

    private fun enrolled() {
        binding?.apply {  clSearch.isVisible = true
            dealType = "2"
           getDeal()
            tvListed.setTextColor(requireActivity().getColor(R.color.black))
            tvEnrolled.setTextColor(requireActivity().getColor(R.color.white))
            tvExpired.setTextColor(requireActivity().getColor(R.color.black))

            tvEnrolled.setBackgroundResource(R.drawable.selected_mid_tab)
            tvExpired.background = null
            tvListed.background = null }
    }

    override fun observe() {
        viewModel.getDealResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    dealList.clear()
                    when(dealType){
                        "1"->{
                            result.data?.data?.listedDeal?.let {
                                dealList.addAll(it)
                            }
                        }
                        "2"->{
                            result.data?.data?.enrolled?.let {
                                dealList.addAll(it)
                            }
                        }
                        "3"->{
                            result.data?.data?.expiredDeal?.let {
                                dealList.addAll(it)
                            }
                        }
                    }

                    dealAdapter?.updateList(dealType,dealList)
                    checkData()
                    viewModel.getDealResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.getDealResponse.value = null
                }

                else -> {}
            }
        }
    }

    private fun checkData(){
        binding?.tvNoDataFound?.isVisible = dealList.isEmpty()
    }

    private fun getDeal() {
        val map = HashMap<String,Any>()
        if (lastQuery!=null){
            map["search"] = lastQuery.toString()
        }
        if (tab!=null){
            if (tab==dealType){
                if (fromDateText!=null&&fromDateText!="") {
                    map["from"] = CommonMethod.formatDate(fromDateText.toString(),"dd/MM/yyyy","yyyy-MM-dd")

                }
                if (toDateText!=null&&toDateText!="") {
                    map["to"] = CommonMethod.formatDate(toDateText.toString(),"dd/MM/yyyy","yyyy-MM-dd")

                }
                if (tab!=null) {
                    map["tab"] = tab.toString()
                }
            }
        }

        viewModel.getDeal(map)
    }

    private fun dealFilterDialog() {
        var tempFromTimestamp: Long? = null
        var tempToTimestamp: Long? = null
        var tempFromDateText: String? = null
        var tempToDateText: String? = null
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = DealFilterDialogBinding.inflate(LayoutInflater.from(context))
        dialogBinding.root.let { dialog.setContentView(it) }

        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = params

        dialogBinding.apply {

            if (tab!=null){
                if (tab==dealType){
                    tempFromTimestamp = fromTimestamp
                    tempToTimestamp = toTimestamp
                    tempFromDateText = fromDateText
                    tempToDateText = toDateText

                    tempFromDateText?.let { etFrom.text = it }
                    tempToDateText?.let { etTo.text = it }
                }
            }

            tvToolbar.setOnClickListener {
                dialog.dismiss()
            }

            etFrom.setOnClickListener {
                openCustomDatePicker(requireActivity()) { date, timestamp ->
                    tempFromTimestamp = timestamp
                    tempFromDateText = date
                    etFrom.text = date

                    tempToTimestamp = null
                    tempToDateText = null
                    etTo.text = ""
                }
            }

            etTo.setOnClickListener {
                if (tempFromTimestamp == null) {
                    showToast("Please select From date first")
                    return@setOnClickListener
                }

                openCustomDatePicker(
                    requireActivity(),
                    minDate = tempFromTimestamp
                ) { date, timestamp ->
                    tempToTimestamp = timestamp
                    tempToDateText = date
                    etTo.text = date
                }
            }

            tvClear.setOnClickListener {
                fromTimestamp = null
                toTimestamp = null
                fromDateText = null
                toDateText = null
                tab = null

                tempFromTimestamp = null
                tempToTimestamp = null
                tempFromDateText = null
                tempToDateText = null
               // dealAdapter?.setDateFilter(null, null)
                getDeal()
                dialog.dismiss()
            }

            tvApply.setOnClickListener {

                fromTimestamp = tempFromTimestamp
                toTimestamp = tempToTimestamp
                fromDateText = tempFromDateText
                toDateText = tempToDateText
                tab = dealType
              //  dealAdapter?.setDateFilter(fromTimestamp, toTimestamp)
                getDeal()
                dialog.dismiss()

            }

        }

        dialog.show()
    }

    private fun openSpeechDialog() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
        }

        try {
            speechLauncher.launch(intent)
        } catch (e: Exception) {
            showToast("Speech recognition not supported")
        }
    }

    private val speechLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val data = result.data
                val list = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val text = list?.firstOrNull()

                if (!text.isNullOrEmpty()) {
                    binding?.etSearch?.setText(text)
                    binding?.etSearch?.setSelection(text.length)
                }
            }
        }

}
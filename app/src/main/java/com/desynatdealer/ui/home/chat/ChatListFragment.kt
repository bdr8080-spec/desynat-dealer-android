package com.desynatdealer.ui.home.chat

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.adapter.ChatListAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.ChatFilterDialogBinding
import com.desynatdealer.databinding.FragmentChatListBinding
import com.desynatdealer.databinding.OrderFilterDialogBinding
import com.desynatdealer.ui.home.chat.response.GetChatListResponse
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.SharedPreference
import com.desynatdealer.utils.openCustomDatePicker
import com.desynatdealer.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatListFragment : BaseFragment() {
    private var binding: FragmentChatListBinding? = null
    private val viewModel: HomeViewModel by activityViewModels()
    private var chatAdapter: ChatListAdapter? = null
    private var chatList = ArrayList<GetChatListResponse.Data>()
    private var search: Job? = null
    private var lastQuery: String? = null
    private var chatFilter: String? = null
    private var isFromSwipe = false
    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatListBinding.inflate(layoutInflater, container, false)
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
            toolbar.tvToolbar.text = getString(R.string.chat)

            chatAdapter = ChatListAdapter(requireActivity(), chatList, object : ChatListAdapter.ChatClick {
                    override fun onChatClick(position: Int) {
                        val intent = Intent(requireContext(), ChatActivity::class.java).apply {
                            putExtra("from", "chat")
                            putExtra("chatData", Gson().toJson(chatList[position]))
                        }
                        startActivity(intent)
                    }

                })
            rvChat.adapter = chatAdapter

            swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(requireActivity(), R.color.brown)
            )
            swipeRefreshLayout.setOnRefreshListener {
                isFromSwipe = true
                getChat()
            }

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
                        isLoading = false
                        getChat()

                    }
                }
            })

        }
    }

    override fun onResume() {
        super.onResume()
        isLoading = true
        getChat()
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            ivFilter.setOnClickListener {
                chatFilterDialog()
            }
        }
    }

    override fun observe() {
        viewModel.getAllChatListResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    if (!isFromSwipe&&isLoading) {
                        ProgressDialogUtils.getInstance().showProgress(requireActivity())
                    }
                }

                is ApiResult.Success -> {
                    isLoading = false
                    ProgressDialogUtils.getInstance().hideProgress()
                    if (isFromSwipe) {
                        binding?.swipeRefreshLayout?.isRefreshing = false
                        isFromSwipe = false
                    }
                    chatList.clear()
                    result.data?.data?.let { chatList.addAll(it) }
                    chatAdapter?.notifyDataSetChanged()
                    viewModel.getAllChatListResponse.value = null
                }

                is ApiResult.Error -> {
                    isLoading = false
                    binding?.swipeRefreshLayout?.isRefreshing = false
                    isFromSwipe = false
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.getAllChatListResponse.value = null
                }

                else -> {}
            }
        }

    }

    private fun getChat() {
        val map = HashMap<String, Any>()
        if (lastQuery != null) {
            map["search"] = lastQuery.toString()
        }
        if (chatFilter != null) {
            map["filters"] = chatFilter.toString()
        }
        viewModel.getAllChatList(map)
    }

    private fun chatFilterDialog() {
        var filter: String? = null
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = ChatFilterDialogBinding.inflate(LayoutInflater.from(context))
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

            if (chatFilter != null) {
                filter = chatFilter.toString()
                when (chatFilter) {
                    "1" -> rbAll.isChecked = true
                    "2" -> rbNew.isChecked = true
                    "3" -> rbQuotationSent.isChecked = true
                    "4" -> rbQuotationAccepted.isChecked = true
                }
            }

            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                filter = when (checkedId) {
                    R.id.rbAll -> "1"
                    R.id.rbNew -> "2"
                    R.id.rbQuotationSent -> "3"
                    R.id.rbQuotationAccepted -> "4"
                    else -> null
                }
            }

            tvToolbar.setOnClickListener {
                dialog.dismiss()
            }


            tvClear.setOnClickListener {
                filter = ""
                chatFilter = null
                getChat()
                dialog.dismiss()
            }

            tvApply.setOnClickListener {

                chatFilter = filter
                if (chatFilter == null) {
                    showToast(getString(R.string.please_select_chat_status))
                    return@setOnClickListener
                } else {
                    getChat()
                    dialog.dismiss()
                }

            }

        }

        dialog.show()
    }

}
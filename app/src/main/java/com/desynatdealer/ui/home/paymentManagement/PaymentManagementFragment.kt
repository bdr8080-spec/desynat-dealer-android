package com.desynatdealer.ui.home.paymentManagement

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.animation.LinearInterpolator
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.adapter.PayoutAdapter
import com.desynatdealer.adapter.TransactionAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentPaymentManagementBinding
import com.desynatdealer.databinding.PaymentFilterDialogBinding
import com.desynatdealer.ui.home.paymentManagement.response.GetAllTransactionResponse
import com.desynatdealer.ui.home.paymentManagement.response.GetPayoutResponse
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.SharedPreference
import com.desynatdealer.utils.openCustomDatePicker
import com.desynatdealer.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class PaymentManagementFragment : BaseFragment() {
    private var binding : FragmentPaymentManagementBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    private var transactionList = ArrayList<GetAllTransactionResponse.Data.Transactions.TransactionData>()
    private var transactionAdapter: TransactionAdapter?=null
    private var payoutList = ArrayList<GetPayoutResponse.Data.Payouts.PayoutData>()
    private var payoutAdapter: PayoutAdapter?=null
    private var search: Job? = null
    private var lastQuery: String? = null
    private var isFromSwipe = false
    private var isFromSearch = false
    private var isFromRefreshIcon = false
    private var type = 0

    private var paymentMode: String? = null
    private var fromTimestamp: Long? = null
    private var toTimestamp: Long? = null
    private var fromDateText: String? = null
    private var toDateText: String? = null
    private var animator: ObjectAnimator?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPaymentManagementBinding.inflate(layoutInflater, container,false)
        return binding?.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        ProgressDialogUtils.getInstance().hideProgress()
        binding?.apply {
            ivBack.isVisible = SharedPreference.get(requireActivity()).userType=="1"
            animator = ObjectAnimator.ofFloat(ivRefresh, View.ROTATION, 0f, 360f)
            animator?.duration = 800
            animator?.repeatCount = ValueAnimator.INFINITE
            animator?.interpolator = LinearInterpolator()


            swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(requireActivity(), R.color.brown)
            )
            swipeRefreshLayout.setOnRefreshListener {
                isFromSwipe = true
                getPaymentList()
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
                        isFromSearch = true
                        getPaymentList()
                    }
                }
            })

            transactionAdapter = TransactionAdapter(requireActivity(),transactionList)
            rvTransaction.adapter = transactionAdapter

            payoutAdapter = PayoutAdapter(requireActivity(),payoutList)
            rvPayout.adapter = payoutAdapter

            when(type){
                0, 1->{
                    allTransaction()
                }
                2->{
                    myPayout()
                }
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun initControl() {
        binding?.apply {
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }
            tvTransactions.setOnClickListener {
                allTransaction()
            }

            tvPayouts.setOnClickListener {
                myPayout()
            }

            ivRefresh.setOnClickListener {
                animator?.start()
                isFromRefreshIcon = true
                getPaymentList()
            }


            btnExport.setOnClickListener {
                if (type == 1) {
                    if (transactionList.isEmpty()) {
                        showToast("No transactions to export")
                        return@setOnClickListener
                    }
                    val csv = CommonMethod.listToCsv(transactionList)
                    CommonMethod.saveCsvFile(requireContext(), "transactions_${System.currentTimeMillis()}", csv)
                } else if (type == 2) {
                    if (payoutList.isEmpty()) {
                        showToast("No payouts to export")
                        return@setOnClickListener
                    }
                    val csv = CommonMethod.listToCsv(payoutList)
                    CommonMethod.saveCsvFile(requireContext(), "payouts_${System.currentTimeMillis()}", csv)
                }
            }

            ivFilter.setOnClickListener {
                paymentFilterDialog()
            }
        }
    }

    override fun observe() {
        viewModel.getAllTransactionResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ApiResult.Loading -> {
                    if (!isFromSwipe && !isFromSearch && !isFromRefreshIcon && type == 1) ProgressDialogUtils.getInstance().showProgress(requireActivity(), false)
                }
                is ApiResult.Success -> {
                    if (type == 1) {
                        ProgressDialogUtils.getInstance().hideProgress()
                        animator?.cancel()
                        isFromSwipe = false
                        isFromSearch = false
                        isFromRefreshIcon = false
                        binding?.swipeRefreshLayout?.isRefreshing = false
                        response.data?.let {
                            binding?.tvAvailable?.text = "${it.data?.totalRevenue ?: "0"} ${it.data?.currencyCode ?: ""}"
                            transactionList.clear()
                            val transactions = it.data?.transactions?.data ?: emptyList()
                            transactionList.addAll(transactions)
                            transactionAdapter?.notifyDataSetChanged()
                            checkData()
                        }
                    }
                }
                is ApiResult.Error -> {
                    if (type == 1) {
                        ProgressDialogUtils.getInstance().hideProgress()
                        animator?.cancel()
                        isFromSwipe = false
                        isFromSearch = false
                        isFromRefreshIcon = false
                        binding?.swipeRefreshLayout?.isRefreshing = false
                        showToast(response.message.toString())
                    }
                }
                null -> {}
            }
        }

        viewModel.getPayoutResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ApiResult.Loading -> {
                    if (!isFromSwipe && !isFromSearch && !isFromRefreshIcon && type == 2) ProgressDialogUtils.getInstance().showProgress(requireActivity(), false)
                }
                is ApiResult.Success -> {
                    if (type == 2) {
                        ProgressDialogUtils.getInstance().hideProgress()
                        animator?.cancel()
                        isFromSwipe = false
                        isFromSearch = false
                        isFromRefreshIcon = false
                        binding?.swipeRefreshLayout?.isRefreshing = false
                        response.data?.let {
                            binding?.tvAvailable?.text = "${it.data?.totalRevenue ?: "0"} ${it.data?.currencyCode ?: ""}"
                            payoutList.clear()
                            val payouts = it.data?.payouts?.data ?: emptyList()
                            payoutList.addAll(payouts)
                            payoutAdapter?.notifyDataSetChanged()
                            checkData()
                        }
                    }
                }
                is ApiResult.Error -> {
                    if (type == 2) {
                        ProgressDialogUtils.getInstance().hideProgress()
                        animator?.cancel()
                        isFromSwipe = false
                        isFromSearch = false
                        isFromRefreshIcon = false
                        binding?.swipeRefreshLayout?.isRefreshing = false
                        showToast(response.message.toString())
                    }
                }
                null -> {}
            }
        }
    }

    private fun getPaymentList(){
        if (type == 1) {
            val map = HashMap<String, Any>()
            map["page"] = 1
            map["limit"] = 20
            map["transaction_type"] = 0
            if (!fromDateText.isNullOrEmpty()) {
                map["from_date"] = fromDateText!!
            }
            if (!toDateText.isNullOrEmpty()) {
                map["to_date"] = toDateText!!
            }
            if (!paymentMode.isNullOrEmpty()) {
                map["payment_mode"] = paymentMode!!
            }
            map["search"] = binding?.etSearch?.text?.toString()?.trim() ?: ""
            map["total_revenue"] = true
            map["customer_name"] = true
            map["total_commission"] = true
            
            viewModel.getAllTransactions(map)
        } else if (type == 2) {
            val map = HashMap<String, Any>()
            map["page"] = 1
            map["limit"] = 20
            if (!fromDateText.isNullOrEmpty()) {
                map["from_date"] = fromDateText!!
            }
            if (!toDateText.isNullOrEmpty()) {
                map["to_date"] = toDateText!!
            }
            map["search"] = binding?.etSearch?.text?.toString()?.trim() ?: ""
            
            viewModel.getPayouts(map)
        }
    }

    private fun allTransaction(){
        binding?.apply {
            type = 1
            rvTransaction.isVisible = true
            rvPayout.isVisible = false
            tvPayouts.background = null
            tvPayouts.setTextColor(requireActivity().getColor(R.color.black))
            tvTransactions.setTextColor(requireActivity().getColor(R.color.white))
            tvTransactions.setBackgroundResource(R.drawable.selected_left_tab)
            getPaymentList()
        }
    }

    private fun myPayout(){
        binding?.apply {
            type = 2
            rvTransaction.isVisible = false
            rvPayout.isVisible = true
            tvPayouts.setBackgroundResource(R.drawable.selected_right_tab)
            tvPayouts.setTextColor(requireActivity().getColor(R.color.white))
            tvTransactions.setTextColor(requireActivity().getColor(R.color.black))
            tvTransactions.background = null
            getPaymentList()
        }
    }

    private fun paymentFilterDialog() {
        var filter: String? = null
        var tempFromTimestamp: Long? = null
        var tempToTimestamp: Long? = null
        var tempFromDateText: String? = null
        var tempToDateText: String? = null
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = PaymentFilterDialogBinding.inflate(LayoutInflater.from(context))
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

            if (type == 2) {
                radioGroup.isVisible = false
            }

            if (paymentMode != null) {
                filter = paymentMode.toString()
                when (paymentMode) {
                    "1" -> rbCard.isChecked = true
                    "2" -> rbWallet.isChecked = true
                    "3" -> rbCod.isChecked = true
                    "4" -> rbGoogle.isChecked = true
                    "5" -> rbApple.isChecked = true
                }
            }

            tempFromTimestamp = fromTimestamp
            tempToTimestamp = toTimestamp
            tempFromDateText = fromDateText
            tempToDateText = toDateText

            tempFromDateText?.let { etFrom.text = it }
            tempToDateText?.let { etTo.text = it }

            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                filter = when (checkedId) {
                    R.id.rbCard -> "1"
                    R.id.rbWallet -> "2"
                    R.id.rbCod -> "3"
                    R.id.rbGoogle -> "4"
                    R.id.rbApple -> "5"
                    else -> null
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
                    showToast(getString(R.string.please_select_from_date_first))
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
                filter = ""
                paymentMode = null

                fromTimestamp = null
                toTimestamp = null
                fromDateText = null
                toDateText = null

                tempFromTimestamp = null
                tempToTimestamp = null
                tempFromDateText = null
                tempToDateText = null

                dialog.dismiss()
                getPaymentList()
            }

            tvApply.setOnClickListener {

                paymentMode = filter
                fromTimestamp = tempFromTimestamp
                toTimestamp = tempToTimestamp
                fromDateText = tempFromDateText
                toDateText = tempToDateText

                if (paymentMode == null && fromDateText == null && toDateText == null && type==1) {
                    showToast(getString(R.string.please_select_payment_status))
                    return@setOnClickListener
                }
                else if (fromDateText == null && toDateText == null && type==2) {
                    showToast(getString(R.string.please_select_date))
                    return@setOnClickListener
                }
                else {
                    dialog.dismiss()
                    getPaymentList()
                }

            }

        }

        dialog.show()
    }

    private fun checkData(){
        when(type){
            1->{
                binding?.tvNoDataFound?.isVisible = transactionList.isEmpty()
                binding?.tvNoDataFound?.text = "No transaction found"
            }
            2->{
                binding?.tvNoDataFound?.isVisible = payoutList.isEmpty()
                binding?.tvNoDataFound?.text = "No payout found"
            }
        }

    }

}
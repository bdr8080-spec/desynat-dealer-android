package com.desynatdealer.ui.home.paymentManagement

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Dialog
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
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.adapter.PayoutAdapter
import com.desynatdealer.adapter.TransactionAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentPaymentManagementBinding
import com.desynatdealer.databinding.PaymentFilterDialogBinding
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
    private var transactionList = ArrayList<String>()
    private var transactionAdapter: TransactionAdapter?=null
    private var payoutList = ArrayList<String>()
    private var payoutAdapter: PayoutAdapter?=null
    private var search: Job? = null
    private var lastQuery: String? = null
    private var isFromSwipe = false
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
                        getPaymentList()
                    }
                }
            })

            transactionAdapter = TransactionAdapter(requireActivity(),transactionList)
            rvTransaction.adapter = transactionAdapter

            payoutAdapter = PayoutAdapter(requireActivity(),payoutList)
            rvPayout.adapter = payoutAdapter

            when(type){
                1->{
                    allTransaction()
                }
                2->{
                    myPayout()
                }
            }

        }
    }

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
                animator?.cancel()
            }

            btnExport.setOnClickListener{

            }

            ivFilter.setOnClickListener {
                paymentFilterDialog()
            }
        }
    }

    override fun observe() {
    }

    private fun getPaymentList(){

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
            }

            tvApply.setOnClickListener {

                paymentMode = filter
                fromTimestamp = tempFromTimestamp
                toTimestamp = tempToTimestamp
                fromDateText = tempFromDateText
                toDateText = tempToDateText

                if (paymentMode == null && fromDateText == null && toDateText == null && type==1) {
                    showToast("Please select payment status")
                    return@setOnClickListener
                }
                else if (fromDateText == null && toDateText == null && type==2) {
                    showToast("Please select date")
                    return@setOnClickListener
                }
                else {

                    dialog.dismiss()
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
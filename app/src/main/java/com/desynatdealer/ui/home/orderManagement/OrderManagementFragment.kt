package com.desynatdealer.ui.home.orderManagement

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.adapter.OrderAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentOrderManagementBinding
import com.desynatdealer.databinding.OrderFilterDialogBinding
import com.desynatdealer.ui.home.orderManagement.response.GetAllOrderResponse
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
class OrderManagementFragment : BaseFragment() {
    private var binding : FragmentOrderManagementBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    private var orderAdapter: OrderAdapter?=null
    private var orderList = ArrayList<GetAllOrderResponse.Data.AllOrder>()
    private var orderData : GetAllOrderResponse.Data? = null
    private var type = 1 // 1 all 2 onging 3 Past Order
    private var orderType = 1 // 1 Normal 2 Orders
    private var search: Job? = null
    private var lastQuery: String? = null
    private var orderFilter:String? = null
    private var fromTimestamp: Long? = null
    private var toTimestamp: Long? = null
    private var fromDateText: String? = null
    private var toDateText: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderManagementBinding.inflate(layoutInflater, container,false)
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
            orderAdapter = OrderAdapter(requireActivity(),orderList,object:
                OrderAdapter.OrderClick{
                override fun onOrderClick(position: Int) {
                            val bundle = Bundle().apply {
                                putString("orderData", Gson().toJson(orderList[position]))
                            }
                            findNavController().navigate(R.id.action_orderManagementFragment_to_orderDetailFragment,bundle)
                }

            })
            rvOrder.adapter = orderAdapter

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

                        getOrder()

                    }
                }
            })
        }

     binding?.apply {
         when(orderType){
             1-> {
                 orderType = 1
                 tvNormal.setTextColor(requireActivity().getColor(R.color.white))
                 tvCustomized.setTextColor(requireActivity().getColor(R.color.black))
                 tvNormal.setBackgroundResource(R.drawable.selected_left_tab)
                 tvCustomized.background = null
             }

                     2-> {
                 orderType = 2
                         tvNormal.setTextColor(requireActivity().getColor(R.color.black))
                         tvCustomized.setTextColor(requireActivity().getColor(R.color.white))
                         tvCustomized.setBackgroundResource(R.drawable.selected_right_tab)
                         tvNormal.background = null
             }
         }
         when(type){
             1-> {
                 type=1
                 getOrder()
                 tvAll.setTextColor(Color.parseColor("#875C3A"))
                 viewAll.setBackgroundColor(Color.parseColor("#875C3A"))
                 tvOngoing.setTextColor(Color.parseColor("#B3B3B3"))
                 viewOngoing.setBackgroundColor(Color.parseColor("#B3B3B3"))
                 tvPast.setTextColor(Color.parseColor("#B3B3B3"))
                 viewPast.setBackgroundColor(Color.parseColor("#B3B3B3"))
             }

             2-> {
                 type=2
                 getOrder()
                 tvAll.setTextColor(Color.parseColor("#B3B3B3"))
                 viewAll.setBackgroundColor(Color.parseColor("#B3B3B3"))
                 tvOngoing.setTextColor(Color.parseColor("#875C3A"))
                 viewOngoing.setBackgroundColor(Color.parseColor("#875C3A"))
                 tvPast.setTextColor(Color.parseColor("#B3B3B3"))
                 viewPast.setBackgroundColor(Color.parseColor("#B3B3B3"))
             }

             3-> {
                 type=3
                 getOrder()
                 tvAll.setTextColor(Color.parseColor("#B3B3B3"))
                 viewAll.setBackgroundColor(Color.parseColor("#B3B3B3"))
                 tvOngoing.setTextColor(Color.parseColor("#B3B3B3"))
                 viewOngoing.setBackgroundColor(Color.parseColor("#B3B3B3"))
                 tvPast.setTextColor(Color.parseColor("#875C3A"))
                 viewPast.setBackgroundColor(Color.parseColor("#875C3A"))
             }
         }
     }

    }

    override fun initControl() {
        binding?.apply {
            ivFilter.setOnClickListener {
                orderFilterDialog()
            }

            tvNormal.setOnClickListener {
                orderType = 1
                getOrder()
                tvNormal.setTextColor(requireActivity().getColor(R.color.white))
                tvCustomized.setTextColor(requireActivity().getColor(R.color.black))
                tvNormal.setBackgroundResource(R.drawable.selected_left_tab)
                tvCustomized.background = null
            }

            tvCustomized.setOnClickListener {
                orderType = 2
                getOrder()
                tvNormal.setTextColor(requireActivity().getColor(R.color.black))
                tvCustomized.setTextColor(requireActivity().getColor(R.color.white))
                tvCustomized.setBackgroundResource(R.drawable.selected_right_tab)
                tvNormal.background = null
            }

            tvAll.setOnClickListener {
                type=1
                getOrder()
                tvAll.setTextColor(Color.parseColor("#875C3A"))
                viewAll.setBackgroundColor(Color.parseColor("#875C3A"))
                tvOngoing.setTextColor(Color.parseColor("#B3B3B3"))
                viewOngoing.setBackgroundColor(Color.parseColor("#B3B3B3"))
                tvPast.setTextColor(Color.parseColor("#B3B3B3"))
                viewPast.setBackgroundColor(Color.parseColor("#B3B3B3"))
            }

            tvOngoing.setOnClickListener {
                type=2
                getOrder()
                tvAll.setTextColor(Color.parseColor("#B3B3B3"))
                viewAll.setBackgroundColor(Color.parseColor("#B3B3B3"))
                tvOngoing.setTextColor(Color.parseColor("#875C3A"))
                viewOngoing.setBackgroundColor(Color.parseColor("#875C3A"))
                tvPast.setTextColor(Color.parseColor("#B3B3B3"))
                viewPast.setBackgroundColor(Color.parseColor("#B3B3B3"))
            }

            tvPast.setOnClickListener {
                type=3
                getOrder()
                tvAll.setTextColor(Color.parseColor("#B3B3B3"))
                viewAll.setBackgroundColor(Color.parseColor("#B3B3B3"))
                tvOngoing.setTextColor(Color.parseColor("#B3B3B3"))
                viewOngoing.setBackgroundColor(Color.parseColor("#B3B3B3"))
                tvPast.setTextColor(Color.parseColor("#875C3A"))
                viewPast.setBackgroundColor(Color.parseColor("#875C3A"))
            }
        }
    }

    override fun observe() {
        viewModel.getOrderResponse.observe(viewLifecycleOwner){result->
            when(result){
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }
                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    orderData = result.data?.data
                    orderList.clear()
                    when(type){
                        1-> result.data?.data?.allOrder?.let { orderList.addAll(it) }
                        2-> result.data?.data?.ongoingOrder?.let { orderList.addAll(it) }
                        3-> result.data?.data?.pastOrder?.let { orderList.addAll(it) }
                    }

                    checkData()
                    orderAdapter?.notifyDataSetChanged()
                    viewModel.getOrderResponse.value = null
                }
                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.getOrderResponse.value = null
                }
                else ->{

                }
            }
        }

    }

    private fun checkData() {
        binding?.apply {
            tvNoDataFound.isVisible = orderList.isEmpty()
            tvAll.text = "All(${orderData?.allOrder?.size?:0})"
            tvOngoing.text = "Ongoing(${orderData?.ongoingOrder?.size?:0})"
            tvPast.text = "Past(${orderData?.pastOrder?.size?:0})"
        }
    }

    private fun getOrder(){
        val map = HashMap<String,Any>()
        map["tabs"] = type
        map["type"] = orderType
        if (lastQuery!=null) {
            map["search"] = lastQuery.toString()
        }
        if (orderFilter!=null&&orderFilter!="") {
            map["order_filter"] = orderFilter.toString()
        }
        if (fromDateText!=null&&fromDateText!="") {
            map["from_date"] =
                CommonMethod.formatDate(fromDateText.toString(),"dd/MM/yyyy","yyyy-MM-dd")

        }
        if (toDateText!=null&&toDateText!="") {
            map["to_date"] =
                CommonMethod.formatDate(toDateText.toString(),"dd/MM/yyyy","yyyy-MM-dd")

        }
        viewModel.getOrder(map)
    }

    private fun orderFilterDialog() {
        var filter:String? = null
        var tempFromTimestamp: Long? = null
        var tempToTimestamp: Long? = null
        var tempFromDateText: String? = null
        var tempToDateText: String? = null
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = OrderFilterDialogBinding.inflate(LayoutInflater.from(context))
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

            if (orderType==2){
                rbExchange.isVisible = false
                rbReturn.isVisible = false
            }

            if (orderFilter!=null) {
                filter = orderFilter.toString()
                when (orderFilter) {
                    "1" -> rbConfirmed.isChecked = true
                    "2" -> rbShipped.isChecked = true
                    "3" -> rbCancel.isChecked = true
                    "4" -> rbReturn.isChecked = true
                    "5" -> rbExchange.isChecked = true
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
                    R.id.rbConfirmed -> "1"
                    R.id.rbShipped -> "2"
                    R.id.rbCancel -> "3"
                    R.id.rbReturn -> "3"
                    R.id.rbExchange -> "5"
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
                orderFilter = null

                fromTimestamp = null
                toTimestamp = null
                fromDateText = null
                toDateText = null

                tempFromTimestamp = null
                tempToTimestamp = null
                tempFromDateText = null
                tempToDateText = null

                getOrder()
                dialog.dismiss()
            }

            tvApply.setOnClickListener {

                orderFilter = filter

                fromTimestamp = tempFromTimestamp
                toTimestamp = tempToTimestamp
                fromDateText = tempFromDateText
                toDateText = tempToDateText

                if (orderFilter==null &&fromDateText==null&&toDateText==null) {
                    showToast("Please select banner status")
                    return@setOnClickListener
                }
                else{
                    getOrder()
                    dialog.dismiss()
                }

            }

        }

        dialog.show()
    }
}
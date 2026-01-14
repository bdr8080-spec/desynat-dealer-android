package com.desynatdealer.ui.home.chat

import android.app.Dialog
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import coil.load
import com.desynatdealer.R
import com.desynatdealer.adapter.ChatAdapter
import com.desynatdealer.adapter.ImagePreviewAdapter
import com.desynatdealer.adapter.UploadMsgImageAdapter
import com.desynatdealer.base.BaseActivity
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.ActivityChatBinding
import com.desynatdealer.databinding.ImagePreviewDialogBinding
import com.desynatdealer.databinding.SendQuotationDialogBinding
import com.desynatdealer.databinding.ViewQuotationRequestDialogBinding
import com.desynatdealer.ui.home.chat.response.ChatDetailResponse
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.SharedPreference
import com.desynatdealer.utils.showToast
import com.desynatdealer.ui.home.chat.response.ChatMessage
import com.desynatdealer.ui.home.chat.response.GetChatListResponse
import com.desynatdealer.ui.home.chat.response.SendMessageRequest
import com.desynatdealer.utils.CommonMethod.toMultiPartBody
import com.desynatdealer.utils.ImagePicker
import com.desynatdealer.utils.ProgressDialogUtils
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant

@AndroidEntryPoint
class ChatActivity : BaseActivity() {
    private var binding: ActivityChatBinding? =null
    private val viewModel: HomeViewModel by viewModels()

    private var chatAdapter: ChatAdapter?=null
    private var chatList = ArrayList<ChatMessage>()
    var from = ""
    var productId = ""
    private var isModified = false
    var chatData: GetChatListResponse.Data?=null
    var chatDetailData: ChatDetailResponse.Data.Record?=null

    private var uniqueId = ""
    var isFirstMessageLoaded = false
    private var quotationDialog: Dialog?=null

    private var uploadMsgImageAdapter: UploadMsgImageAdapter?=null
    private var uploadImageList = ArrayList<UploadImageType>()

    private var imageUri: Uri? = null

    private lateinit var imagePicker: ImagePicker
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            imagePicker.onCameraResult(success)
        }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            imagePicker.onGalleryResult(uri)
        }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            imagePicker.onPermissionResult(granted)
        }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        initView()
        initControl()
        observer()
    }
    override fun initView() {

        binding?.apply {
            CommonMethod.detectGestureMode(root, viewBottom)

            val itemJson = intent?.getStringExtra("chatData")
                if (!itemJson.isNullOrEmpty()) {
                    chatData = Gson().fromJson(itemJson, GetChatListResponse.Data::class.java)
                    uniqueId = chatData?.uniqueId.toString()
                    getChat()
                }
            tvToolbar.text = chatData?.userDetails?.fullName.toString()
            chatAdapter = ChatAdapter(this@ChatActivity,chatList,object : ChatAdapter.ChatClick{
                override fun onChatClick(position: Int) {
                    chatList[position].message.toSafeString()?.let { imagePreviewDialog(it) }
                }

            })
            rvChat.adapter = chatAdapter

          /*  listenMessages(uniqueId = uniqueId) { message ->
                if (!isFirstMessageLoaded) {
                    pbLoader.isVisible = false
                    isFirstMessageLoaded = true
                }

                chatList.add(message)
                chatAdapter?.notifyItemInserted(chatList.size - 1)
                rvChat?.scrollToPosition(chatList.size - 1)

                if (message.by != "user" && message.read == 0 && message.id != null) {
                    markMessageRead(uniqueId, message.id!!)
                }
            }*/

            listenMessages(
                uniqueId = uniqueId,
                onMessageAdded = { message ->
                    if (!isFirstMessageLoaded) {
                        pbLoader.isVisible = false
                        isFirstMessageLoaded = true
                    }

                    chatList.add(message)
                    chatAdapter?.notifyItemInserted(chatList.size - 1)
                    rvChat.scrollToPosition(chatList.size - 1)

                    if (message.by.toSafeString() != "seller" && message.read.toSafeInt() == 0 && message.id.toSafeString() != null) {
                        markMessageRead(uniqueId, message.id.toSafeString())
                    }
                },
                onMessageUpdated = { updatedMessage ->
                    Log.e("update", "initView: ${updatedMessage}" )

                    val index = chatList.indexOfFirst { it.id.toSafeString() == updatedMessage.id.toSafeString() }
                    Log.e("update", "id: ${index}" )
                    Log.e("update", "id: ${chatList[index]}" )
                    if (index != -1) {
                        chatList[index] = updatedMessage
                        chatAdapter?.notifyItemChanged(index)
                    }
                }
            )



            uploadImageList.clear()
            uploadMsgImageAdapter = UploadMsgImageAdapter(this@ChatActivity,uploadImageList,object :UploadMsgImageAdapter.Click{
                override fun onClick(position: Int) {
                    uploadImageList.removeAt(position)
                    uploadMsgImageAdapter?.notifyDataSetChanged()
                }

            })
            rvUploadImg.adapter = uploadMsgImageAdapter

            imagePicker = ImagePicker(
                this@ChatActivity,
                cameraLauncher,
                galleryLauncher,
                permissionLauncher
            ) { uri ->
                uri?.let {
                    imageUri = it
                    viewModel.upload(CommonMethod.uriToFile(this@ChatActivity, it).toMultiPartBody("image"))
                }
            }
        }

    }

    private fun getChat() {
        val map = HashMap<String, Any>()
        map["unique_id"] = uniqueId
        viewModel.getChatDetail(map)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initControl() {
        binding?.apply {

            toolbar.setOnClickListener {
                finish()
            }

            tvToolbar.setOnClickListener {
                finish()
            }

            ivSend.setOnClickListener {

                val textMessage = etMessage.text?.trim().toString()
                val hasText = textMessage.isNotEmpty()
                val hasImages = uploadImageList.isNotEmpty()

                if (!hasText && !hasImages) {
                    showToast("Please enter message")
                    return@setOnClickListener
                }

                if (hasText) {
                    sendMessage(
                        uniqueId = uniqueId,
                        message = textMessage,
                        type = 0
                    )
                    etMessage.setText("")
                }

                if (hasImages) {
                    uploadImageList.forEach { image ->
                        sendMessage(
                            uniqueId = uniqueId,
                            message = image.imageUrl,
                            type = 1
                        )
                    }
                    uploadImageList.clear()
                    uploadMsgImageAdapter?.notifyDataSetChanged()
                }
            }

            ivAttachment.setOnClickListener {
                imagePicker.showImageSourceDialog()
            }

            btnSendQuotation.setOnClickListener {
                sendQuotationDialog()
            }

            ivViewRequest.setOnClickListener {
                viewQuotationRequestDialog()
            }

        }
    }

    private val database =
        FirebaseDatabase.getInstance().reference

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendMessage(uniqueId: String, message: String, type: Int) {
        val ref = database.child("chats").child(uniqueId).push()
        val messageId = ref.key ?: return

        val msg = SendMessageRequest(
            id = messageId,
            by = "seller",
            message = message,
            type = type,
            unique_id = uniqueId,
            times = Instant.now().toString(),
            user_id = SharedPreference.get(this).getUser()?.id?.toLong()?:0,
            read = 0
        )

        ref.setValue(msg)
    }

    fun listenMessages(
        uniqueId: String,
        onMessageAdded: (ChatMessage) -> Unit,
        onMessageUpdated: (ChatMessage) -> Unit
    ) {
        database.child("chats")
            .child(uniqueId)
            .addChildEventListener(object : ChildEventListener {

                override fun onChildAdded(snapshot: DataSnapshot, prev: String?) {
                    snapshot.getValue(ChatMessage::class.java)
                        ?.let { onMessageAdded(it) }
                }

                override fun onChildChanged(snapshot: DataSnapshot, prev: String?) {
                    snapshot.getValue(ChatMessage::class.java)
                        ?.let { onMessageUpdated(it) }
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, prev: String?) {}
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    /*   fun listenMessages(
           uniqueId: String,
           onMessage: (ChatMessage) -> Unit
       ) {
           database.child("chats")
               .child(uniqueId)
               .addChildEventListener(object : ChildEventListener {

                   override fun onChildAdded(snapshot: DataSnapshot, prev: String?) {
                       snapshot.getValue(ChatMessage::class.java)
                           ?.let { onMessage(it) }
                   }

                   override fun onChildChanged(snapshot: DataSnapshot, prev: String?) {}
                   override fun onChildRemoved(snapshot: DataSnapshot) {}
                   override fun onChildMoved(snapshot: DataSnapshot, prev: String?) {}
                   override fun onCancelled(error: DatabaseError) {}
               })
       }*/

    fun markMessageRead(uniqueId: String, messageId: String) {
        val map = HashMap<String, Any>()
        map["read"] = 1

        database.child("chats")
            .child(uniqueId)
            .child(messageId)
            .updateChildren(map)
    }

    override fun observer() {

        viewModel.uploadResponse.observe(this) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(this)
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    imageUri?.let { result.data?.data?.url?.let { it1 -> UploadImageType(it, it1) } }
                        ?.let { uploadImageList.add(it) }

                    uploadMsgImageAdapter?.notifyDataSetChanged()

                    imageUri = null
                    viewModel.uploadResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.uploadResponse.value = null
                }

                else -> {}
            }
        }

        viewModel.getChatDetailResponse.observe(this) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(this)
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    chatDetailData = result.data?.data?.record
                    setData()
                    viewModel.getChatDetailResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.getChatDetailResponse.value = null
                }

                else -> {}
            }
        }

        viewModel.modifySingleChatResponse.observe(this) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(this)                  }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    chatDetailData?.price = result.data?.data?.price?:0.0
                    chatDetailData?.tax = result.data?.data?.tax?:0.0
                    chatDetailData?.shipping = result.data?.data?.shipping?:0.0
                    chatDetailData?.totalAmount = result.data?.data?.totalAmount?:0.0
                    chatDetailData?.action = result.data?.data?.action?:"0"
                    setData()
                    if (isModified){
                        CommonMethod.showSuccessDialog(this@ChatActivity,"Quotation Modified successfully!"){
                            isModified = false
                            quotationDialog?.dismiss()
                        }
                    }
                    else {
                        CommonMethod.showSuccessDialog(
                            this@ChatActivity,
                            "Quotation Sent successfully!"
                        ) {
                            quotationDialog?.dismiss()
                        }
                    }
                    viewModel.modifySingleChatResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.modifySingleChatResponse.value = null
                }

                else -> {}
            }
        }
    }

    private fun sendQuotationDialog() {
        quotationDialog = Dialog(this, R.style.CustomDialog)
        val dialogBinding = SendQuotationDialogBinding.inflate(LayoutInflater.from(this))
        dialogBinding.root.let { quotationDialog?.setContentView(it) }

        val window: Window = quotationDialog?.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        quotationDialog?.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = params

        dialogBinding.apply {
            if (chatDetailData?.action=="0"){
                isModified = false
                tvToolbar.text = "Send Quotation"
                tvSend.text = "Confirm & Send"
                ivViewRequest.isVisible = false
            }
            else if (chatDetailData?.action=="1"){
                isModified = true
                tvToolbar.text = "Modify Quotation"
                tvSend.text = "Confirm & Modify"
                ivViewRequest.isVisible = true

                etPerItemPrice.setText(CommonMethod.formatPrice(chatDetailData?.price?:0.0))
                etTax.setText(CommonMethod.formatPrice(chatDetailData?.tax?:0.0))
                etShippingFee.setText(CommonMethod.formatPrice(chatDetailData?.shipping?:0.0))
                etTotalAmount.setText(CommonMethod.formatPrice(chatDetailData?.totalAmount?:0.0))
            }
            else if (chatDetailData?.action=="2"){
                    isModified = true
                    tvToolbar.text = "Modify Quotation"
                    tvSend.text = "Confirm & Modify"
                    ivViewRequest.isVisible = true

                etPerItemPrice.setText(CommonMethod.formatPrice(chatDetailData?.price?:0.0))
                etTax.setText(CommonMethod.formatPrice(chatDetailData?.tax?:0.0))
                etShippingFee.setText(CommonMethod.formatPrice(chatDetailData?.shipping?:0.0))
                etTotalAmount.setText(CommonMethod.formatPrice(chatDetailData?.totalAmount?:0.0))
            }

            val textWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val total = calculateTotal(
                        etPerItemPrice.text?.toString(),
                        etShippingFee.text?.toString(),
                        etTax.text?.toString())
                    etTotalAmount.setText(total)
                }

                override fun afterTextChanged(s: Editable?) {}
            }

            etPerItemPrice.addTextChangedListener(textWatcher)
            etShippingFee.addTextChangedListener(textWatcher)
            etTax.addTextChangedListener(textWatcher)


            tvToolbar.setOnClickListener {
                quotationDialog?.dismiss()
            }

            ivViewRequest.setOnClickListener {
                viewQuotationRequestDialog()
            }

            tvSend.setOnClickListener {
                if (etPerItemPrice.text?.trim()?.isEmpty() == true){
                    showToast("Please enter per item price")
                }
                else if (etShippingFee.text?.trim()?.isEmpty() == true){
                    showToast("Please enter shipping charge")
                }
                else if (etTax.text?.trim()?.isEmpty() == true){
                    showToast("Please enter tax amount")
                }
                else {
                    val map = HashMap<String, Any>()
                    map["unique_id"] = uniqueId
                    map["price"] = etPerItemPrice.text?.trim().toString()
                    map["shipping"] = etShippingFee.text?.trim().toString()
                    map["quantity"] = chatDetailData?.quantity.toString()
                    map["tax"] = etTax.text?.trim().toString()
                    map["total_amount"] = etTotalAmount.text?.trim().toString()
                    sendQuotation(map)
                }
            }
        }
        quotationDialog?.show()
    }

    private fun calculateTotal(
        price: String?,
        shipping: String?,
        tax: String?,
    ): String {

        val itemPrice = price?.toDoubleOrNull() ?: 0.0
        val shippingFee = shipping?.toDoubleOrNull() ?: 0.0
        val taxAmount = tax?.toDoubleOrNull() ?: 0.0
        val quantity = chatDetailData?.quantity?.toInt()?:1

        val total = (itemPrice * quantity) + shippingFee + taxAmount
        return CommonMethod.formatPrice(total)
    }

    private fun viewQuotationRequestDialog() {
        val dialog = Dialog(this, R.style.CustomDialog)
        val dialogBinding = ViewQuotationRequestDialogBinding.inflate(LayoutInflater.from(this))
        dialogBinding.root.let { dialog?.setContentView(it) }

        val window: Window = dialog?.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.setCancelable(true)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.CENTER
        window.attributes = params

        dialogBinding.apply {
            ivProduct.load(chatDetailData?.productVariation?.variantImage?.get(0)){
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }
            tvPrice.text = "${CommonMethod.formatPrice(chatDetailData?.productVariation?.actualPrice?:0.0)} KD"
            tvDiscountPrice.text = "${CommonMethod.formatPrice(chatDetailData?.productVariation?.finalPrice?:0.0)} KD"
            tvProductName.text =chatDetailData?.productVariation?.productDetails?.productNameEn
            tvSizeValue.text =chatDetailData?.productVariation?.sizeId
            tvTotalQuantityValue.text = chatDetailData?.quantity.toString()
            tvAddressValue.text = chatDetailData?.deliveryDetails?.address.toString()
            tvCustomDetail.text = chatDetailData?.customDetails.toString()
        }
        dialog?.show()
    }

    private fun setData(){
        binding?.apply {

            if (chatDetailData?.type=="2"){
                toolbar.isVisible = false
                clQuotation.isVisible = true
                tvRequestId.text = "Request Id #${chatDetailData?.id}"
                ivProduct.load(chatDetailData?.productVariation?.variantImage?.get(0)){
                    crossfade(true)
                    placeholder(R.drawable.ic_placeholder)
                    error(R.drawable.ic_placeholder)
                }
                tvSizeValue.text = chatDetailData?.productVariation?.size?.name.toString()
                ivColor.setColorFilter(Color.parseColor(chatDetailData?.productVariation?.color?.hexCode))

                tvPrice.text = "${CommonMethod.formatPrice(chatDetailData?.productVariation?.actualPrice?:0.0)} KD"
                tvDiscountPrice.text = "${CommonMethod.formatPrice(chatDetailData?.productVariation?.finalPrice?:0.0)} KD"
                tvProductName.text =chatDetailData?.productVariation?.productDetails?.productNameEn
                tvTotalQuantityValue.text = chatDetailData?.quantity.toString()
                tvCustomDetail.text = chatDetailData?.customDetails.toString()
                tvAddressValue.text = chatDetailData?.deliveryDetails?.address.toString()
                tvPerItemPriceValue.text = "${CommonMethod.formatPrice(chatDetailData?.price?:0.0)} KD"
                tvTaxValue.text = "${CommonMethod.formatPrice(chatDetailData?.tax?:0.0)} KD"
                tvShippingFeesValue.text = "${CommonMethod.formatPrice(chatDetailData?.shipping?:0.0)} KD"
                tvTotalAmountValue.text = "${CommonMethod.formatPrice(chatDetailData?.totalAmount?:0.0)} KD"
                when (chatDetailData?.action) {
                    "0" -> {
                        tvStatus.isVisible = false
                        ivViewRequest.isVisible = false
                        clCustomerRequest.isVisible = true
                        clQuotationDetail.isVisible = false
                        btnSendQuotation.text = "Send Quotation"
                    }
                    "1" -> {
                        tvStatus.isVisible = true
                        ivViewRequest.isVisible = false
                        clCustomerRequest.isVisible = false
                        clQuotationDetail.isVisible = true
                        tvStatus.text = "Quotation Sent"
                        tvStatus.setTextColor(Color.parseColor("#172B85"))
                        btnSendQuotation.text = "Modify Quotation"
                    }
                    "2" -> {
                        tvStatus.isVisible = true
                        ivViewRequest.isVisible = false
                        clCustomerRequest.isVisible = false
                        clQuotationDetail.isVisible = true
                        tvStatus.text = "Quotation Sent"
                        tvStatus.setTextColor(Color.parseColor("#172B85"))
                        btnSendQuotation.text = "Modify Quotation"
                    }
                    "4" -> {
                        tvStatus.isVisible = true
                        ivViewRequest.isVisible = true
                        clCustomerRequest.isVisible = false
                        clQuotationDetail.isVisible = true
                        btnSendQuotation.isVisible = false
                        tvStatus.text = "Rejected"
                        tvStatus.setTextColor(Color.parseColor("#D53B36"))
                    }
                    "5" -> {
                        tvStatus.isVisible = true
                        ivViewRequest.isVisible = true
                        clCustomerRequest.isVisible = false
                        clQuotationDetail.isVisible = true
                        btnSendQuotation.isVisible = false
                        tvStatus.text = "Accepted"
                        tvStatus.setTextColor(Color.parseColor("#68CA44"))
                    }
                }
            }
            else{
                clQuotation.isVisible = false
                toolbar.isVisible = true
            }

        }
    }

    private fun sendQuotation(map: HashMap<String, Any>) {
        viewModel.modifySingleChat(map)
    }

    private fun imagePreviewDialog(image: String) {
        var imageList = arrayListOf(image)
        val dialog = Dialog(this, R.style.CustomDialog)
        val dialogBinding = ImagePreviewDialogBinding.inflate(LayoutInflater.from(this))
        dialogBinding.root.let { dialog?.setContentView(it) }

        val window: Window = dialog?.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.CENTER
        window.attributes = params

        dialogBinding.apply {

            ivCross.setOnClickListener{
                dialog?.dismiss()
            }

            dotsIndicator.isVisible = false

            val imagePreviewAdapter = ImagePreviewAdapter(this@ChatActivity,imageList)
            viewPager?.adapter = imagePreviewAdapter

        }

        dialog?.show()
    }

}

fun Any?.toSafeString(): String {
    return when (this) {
        is String -> this
        is Long -> this.toString()
        is Int -> this.toString()
        is Double -> this.toString()
        else -> ""
    }
}

fun Any?.toSafeInt(): Int {
    return when (this) {
        is Int -> this
        is Long -> this.toInt()
        is String -> this.toIntOrNull() ?: 0
        else -> 0
    }
}

fun Any?.toSafeLong(): Long {
    return when (this) {
        is Long -> this
        is Int -> this.toLong()
        is String -> this.toLongOrNull() ?: 0L
        else -> 0L
    }
}


data class UploadImageType(
    var image:Uri,
    var imageUrl:String
)
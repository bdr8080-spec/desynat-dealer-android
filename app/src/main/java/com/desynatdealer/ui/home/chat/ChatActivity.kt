package com.desynatdealer.ui.home.chat

import android.app.Dialog
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
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
import com.desynatdealer.adapter.UploadMsgImageAdapter
import com.desynatdealer.base.BaseActivity
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.ActivityChatBinding
import com.desynatdealer.databinding.SendQuotationDialogBinding
import com.desynatdealer.databinding.ViewQuotationRequestDialogBinding
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.SharedPreference
import com.desynatdealer.utils.showToast
import com.desynatdealer.ui.home.chat.response.ChatMessage
import com.desynatdealer.ui.home.chat.response.GetChatListResponse
import com.desynatdealer.ui.home.chat.response.SendMessageRequest
import com.desynatdealer.utils.CommonMethod.toMultiPartBody
import com.desynatdealer.utils.ImagePicker
import com.desynatdealer.utils.ProgressDialogUtils
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
    var isObserver = false
    var from = ""
    var productId = ""
    var storeName = ""
    var productName = ""
    private var isModified = false
    var chatData: GetChatListResponse.Data?=null

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
                }
            tvToolbar.text = chatData?.userDetails?.fullName.toString()
            setData()
            chatAdapter = ChatAdapter(this@ChatActivity,chatList,object : ChatAdapter.ChatClick{
                override fun onChatClick(position: Int) {

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

                    if (message.by != "seller" && message.read == 0 && message.id != null) {
                        markMessageRead(uniqueId, message.id!!)
                    }
                },
                onMessageUpdated = { updatedMessage ->
                    Log.e("update", "initView: ${updatedMessage}" )

                    val index = chatList.indexOfFirst { it.id == updatedMessage.id }
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
                        type = "0"
                    )
                    etMessage.setText("")
                }

                if (hasImages) {
                    uploadImageList.forEach { image ->
                        sendMessage(
                            uniqueId = uniqueId,
                            message = image.imageUrl,
                            type = "1"
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
    fun sendMessage(uniqueId: String, message: String, type: String) {
        val ref = database.child("chats").child(uniqueId).push()
        val messageId = ref.key ?: return

        val msg = SendMessageRequest(
            id = messageId,
            by = "seller",
            message = message,
            type = type,
            unique_id = uniqueId,
            times = Instant.now().toString(),
            user_id = SharedPreference.get(this).getUser()?.id.toString(),
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

        viewModel.modifySingleChatResponse.observe(this) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(this)                  }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    if (isModified){
                        CommonMethod.showSuccessDialog(this@ChatActivity,"Modified successfully!"){
                            isModified = false
                            quotationDialog?.dismiss()
                        }
                    }
                    else {
                        CommonMethod.showSuccessDialog(
                            this@ChatActivity,
                            "Request Sent successfully!"
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
            if (chatData?.action=="0"){
                isModified = false
                tvToolbar.text = "Send Quotation"
                tvSend.text = "Confirm & Send"
                ivViewRequest.isVisible = false
            }
            if (chatData?.action=="1"){
                isModified = true
                tvToolbar.text = "Modify Quotation"
                tvSend.text = "Confirm & Modify"
                ivViewRequest.isVisible = true
            }

            tvToolbar.setOnClickListener {
                quotationDialog?.dismiss()
            }

            ivViewRequest.setOnClickListener {
                viewQuotationRequestDialog()
            }

            tvSend.setOnClickListener {
                val map = HashMap<String, Any>()
                map["unique_id"] = uniqueId
                map["price"] = etPerItemPrice.text?.trim().toString()
                map["shipping"] = etShippingFee.text?.trim().toString()
                map["quantity"] = chatData?.quantity.toString()
                map["tax"] = etTax.text?.trim().toString()
                map["total_amount"] = etTotalAmount.text?.trim().toString()

                sendQuotation(map)
            }
        }
        quotationDialog?.show()
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
            ivProduct.load(chatData?.productVariation?.variantImage?.get(0)){
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }
            tvPrice.text = "${CommonMethod.formatPrice(chatData?.productVariation?.actualPrice?:0.0)} KD"
            tvDiscountPrice.text = "${CommonMethod.formatPrice(chatData?.productVariation?.finalPrice?:0.0)} KD"
            tvProductName.text =chatData?.productVariation?.productDetails?.productNameEn
            tvSizeValue.text =chatData?.productVariation?.sizeId
            tvTotalQuantityValue.text = chatData?.quantity.toString()
            tvAddressValue.text = chatData?.deliveryAddress.toString()
            tvCustomDetail.text = chatData?.customDetails.toString()
        }
        dialog?.show()
    }

    private fun setData(){
        binding?.apply {

            if (chatData?.type=="2"){
                toolbar.isVisible = false
                clQuotation.isVisible = true
                tvRequestId.text = "Request Id #${chatData?.id}"
                ivProduct.load(chatData?.productVariation?.variantImage?.get(0)){
                    crossfade(true)
                    placeholder(R.drawable.ic_placeholder)
                    error(R.drawable.ic_placeholder)
                }
                tvPrice.text = "${CommonMethod.formatPrice(chatData?.productVariation?.actualPrice?:0.0)} KD"
                tvDiscountPrice.text = "${CommonMethod.formatPrice(chatData?.productVariation?.finalPrice?:0.0)} KD"
                tvProductName.text =chatData?.productVariation?.productDetails?.productNameEn
                tvTotalQuantityValue.text = chatData?.quantity.toString()
                tvCustomDetail.text = chatData?.customDetails.toString()
                tvAddressValue.text = chatData?.deliveryAddress.toString()
                tvPerItemPriceValue.text = "${CommonMethod.formatPrice(chatData?.price?:0.0)} KD"
                tvTaxValue.text = "${CommonMethod.formatPrice(chatData?.tax?:0.0)} KD"
                tvShippingFeesValue.text = "${CommonMethod.formatPrice(chatData?.shipping?:0.0)} KD"
                tvTotalAmountValue.text = "${CommonMethod.formatPrice(chatData?.totalAmount?:0.0)} KD"
                when (chatData?.action) {
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
                        tvStatus.text = "Pending"
                        tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                        btnSendQuotation.text = "Modify Quotation"
                    }
                    "2" -> {
                        tvStatus.isVisible = true
                        ivViewRequest.isVisible = false
                        clCustomerRequest.isVisible = false
                        clQuotationDetail.isVisible = true
                        tvStatus.text = "Pending"
                        tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                        btnSendQuotation.text = "Modify Quotation"
                    }
                    "3" -> {
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

}


data class UploadImageType(
    var image:Uri,
    var imageUrl:String
)
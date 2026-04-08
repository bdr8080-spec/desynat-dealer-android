package com.desynatdealer.ui.home.cms

import android.app.Dialog
import android.content.Intent
import android.net.Uri
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
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.adapter.TicketAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentSupportBinding
import com.desynatdealer.databinding.RaiseTicketDialogBinding
import com.desynatdealer.utils.showToast
import com.desynatdealer.ui.home.cms.response.GetAllRaisedTicketResponse
import com.desynatdealer.ui.home.cms.response.GetTicketTypeResponse
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.CommonMethod.toMultiPartBody
import com.desynatdealer.utils.ImagePicker
import com.desynatdealer.utils.ProgressDialogUtils
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import kotlin.getValue

@AndroidEntryPoint
class SupportFragment : BaseFragment() {
    private var binding: FragmentSupportBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = false
    private var ticketList = ArrayList<GetAllRaisedTicketResponse.Data.Data>()
    private var ticketAdapter: TicketAdapter?=null
    private var search: Job? = null
    private var lastQuery: String? = null
    private var type = 0

    private var ticketTypeList = ArrayList<GetTicketTypeResponse.Data>()

    private var currentPage = 1
    private var lastPage = 1
    private var totalPage = 0
    private var totalItem=0
    private var pageLimit=10
    private var pagination=false
    private var isRaised=false

    private var dialog: Dialog?=null
    private var dialogBinding: RaiseTicketDialogBinding?=null

    private lateinit var imagePicker: ImagePicker
    private var imageFile: File? = null
    private var imageUrl:String?=null
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

    private var selectedTicketType: GetTicketTypeResponse.Data?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSupportBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        binding?.apply {
            toolbar.tvToolbar.text = "Support"

            etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    val query = s.toString().trim()
                    if (query == lastQuery) return
                    lastQuery = query
                    search?.cancel()
                    search = lifecycleScope.launch {
                        delay(600)
                        if (!isObserver){
                            currentPage = 1
                            getAllTicket()
                        }
                    }

                }
            })

            ticketAdapter = TicketAdapter(requireActivity(),ticketList,object : TicketAdapter.OnClick{
                override fun click(position: Int, type: Int) {
                    val bundle = Bundle().apply {
                        putString("ticketData", Gson().toJson(ticketList[position]))
                    }
                    findNavController().navigate(R.id.action_supportFragment_to_ticketDetailFragment,bundle)
                }

            })
            rvTicket.adapter = ticketAdapter

            nestedScrollView.setOnScrollChangeListener { nestedScrollView: NestedScrollView, _, scrollY, _, oldScrollY ->
                val lastChild = nestedScrollView.getChildAt(nestedScrollView.childCount - 1)

                if (lastChild != null) {
                    val diff = lastChild.bottom - (nestedScrollView.height + scrollY)
                    if (diff == 0 && scrollY > oldScrollY) {
                        if(totalItem==0)
                            return@setOnScrollChangeListener
                        if (!isObserver && !pagination && currentPage < lastPage) {
                            pagination = true
                            currentPage += 1
                            getAllTicket()
                            binding?.progressBar?.isVisible = true
                        }
                    }
                }
            }

            when(type){
                0->{
                    contact()
                }
                1->{
                    ticketManagement()
                }
            }

            imagePicker = ImagePicker(
                requireActivity(),
                cameraLauncher,
                galleryLauncher,
                permissionLauncher
            ) { uri ->
                uri?.let {
                    imageFile = CommonMethod.uriToFile(requireActivity(), it)
                    dialogBinding?.ivDoc?.setImageURI(it)
                }
            }
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            clSupportEmail.setOnClickListener {
                val email = tvSupportEmailAddress.text.trim()
                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
//                    putExtra(Intent.EXTRA_SUBJECT, "Your Subject Here")
//                    putExtra(Intent.EXTRA_TEXT, "Hello, this is the email body.")
                }

                startActivity(emailIntent)
            }

            clSupportNumber.setOnClickListener {
                val number = tvSupportMobileNumber.text.trim()
                val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${number}")
                }
                startActivity(dialIntent)
            }

            clWhatsapp.setOnClickListener {
                val number = tvSupportMobileNumber.text.trim()
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://wa.me/${number}")
                }
                startActivity(intent)
            }

            clSupportTeam.setOnClickListener {

            }

            tvContact.setOnClickListener {
              contact()
            }

            tvTicketManagement.setOnClickListener {
               ticketManagement()
            }

            ivAddTicket.setOnClickListener {
                raiseTicketDialog()
            }

            ivMic.setOnClickListener {
                openSpeechDialog()
            }
        }

    }

    override fun observe() {

        viewModel.uploadResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                  //  ProgressDialogUtils.getInstance().hideProgress()
                    imageUrl = result.data?.data?.url
                    raiseTicket()
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

        viewModel.getTicketTypeResponse.observe(viewLifecycleOwner){result->
            when(result){
                ApiResult.Loading -> {

                }
                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    ticketTypeList.clear()
                    result?.data?.data?.let { ticketTypeList.addAll(it) }
                    viewModel.getTicketTypeResponse.value = null
                }
                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.getTicketTypeResponse.value = null
                }
                else ->{

                }
            }
        }

        viewModel.getRaisedTicketResponse.observe(viewLifecycleOwner){result->
            when(result){
                ApiResult.Loading -> {
                    if (isRaised==false) {
                        if (pagination) {
                            binding?.progressBar?.isVisible = true
                        } else {
                            ProgressDialogUtils.getInstance().showProgress(requireActivity())
                        }
                    }
                }
                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    if (isRaised){
                        isRaised = false

                        CommonMethod.showSuccessDialog(requireActivity(),"Ticket Raised Successfully!")
                    }
                    if (pagination) {
                        binding?.progressBar?.isVisible = false
                        pagination = false
                    }
                    else{
                        ticketList.clear()
                    }

                    result.data?.data?.data?.let { ticketList.addAll(it) }
                    ticketAdapter?.notifyDataSetChanged()
                    checkData()
                    viewModel.getRaisedTicketResponse.value = null
                }
                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.getRaisedTicketResponse.value = null
                }
                else ->{

                }
            }
        }

        viewModel.raiseTicketResponse.observe(viewLifecycleOwner){result->
            when(result){
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }
                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    isRaised = true
                    selectedTicketType = null
                    imageUrl = null
                    imageFile = null
                    currentPage = 1
                    getAllTicket()
                    viewModel.raiseTicketResponse.value = null
                }
                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.raiseTicketResponse.value = null
                }
                else ->{

                }
            }
        }

    }

    private fun ticketManagement(){
        binding?.apply {
            type = 1
            currentPage = 1
            getAllTicket()
            if (ticketList.isEmpty()){
                getTicketType()
            }
            llTicket.isVisible = true
            llContact.isVisible = false
            tvContact.background = null
            tvContact.setTextColor(requireActivity().getColor(R.color.black))
            tvTicketManagement.setTextColor(requireActivity().getColor(R.color.white))
            tvTicketManagement.setBackgroundResource(R.drawable.selected_right_tab)
        }
    }

    private fun contact(){
        binding?.apply {
            type = 0
            llTicket.isVisible = false
            llContact.isVisible = true
            tvContact.setBackgroundResource(R.drawable.selected_left_tab)
            tvContact.setTextColor(requireActivity().getColor(R.color.white))
            tvTicketManagement.setTextColor(requireActivity().getColor(R.color.black))
            tvTicketManagement.background = null
        }
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
                }
            }
        }

    private fun raiseTicketDialog() {
        dialog = Dialog(requireActivity(), R.style.CustomDialog)
        dialogBinding = RaiseTicketDialogBinding.inflate(LayoutInflater.from(context))
        dialogBinding?.root.let { it?.let { it1 -> dialog?.setContentView(it1) } }

        val window: Window = dialog?.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = params

        dialogBinding?.apply {

            ivDoc.setOnClickListener {
                imagePicker.showImageSourceDialog()
            }

            tvToolbar.setOnClickListener {
                dialog?.dismiss()
            }

            etTicketType.setOnClickListener {
                showTicketTypePopup(dialogBinding)
            }

            btnSubmit.setOnClickListener{

                if (imageFile!=null){
                    uploadFile()
                }
                else{
                    raiseTicket()
                }


                dialog?.dismiss()
            }

        }

        dialog?.show()
    }

    private fun getAllTicket(){
        val map = HashMap<String,Any>()
        if (lastQuery!=null){
            map["search"] = lastQuery.toString()
        }
        map["limit"] = "10"
        map["page"] = currentPage
        viewModel.getRaisedTicket(map)
    }

    private fun raiseTicket() {
        val map = HashMap<String,Any>()
        map["ticket_type_id"] = selectedTicketType?.id.toString()
        map["title"] = dialogBinding?.etTitle?.text?.trim().toString()
        map["description"] = dialogBinding?.etDescription?.text?.trim().toString()
        if (imageUrl!=null){
            map["file"] = imageUrl.toString()
        }

        viewModel.raiseTicket(map)
    }

    fun uploadFile(){
        val image = imageFile?.toMultiPartBody("file")
        viewModel.upload(image)
    }

    private fun getTicketType(){
        viewModel.getTicketType()
    }

    private fun checkData(){
        binding?.tvNoDataFound?.isVisible = ticketList.isEmpty()
    }

    private fun showTicketTypePopup(dialogBinding: RaiseTicketDialogBinding?) {

        val popup = android.widget.PopupMenu(requireContext(), dialogBinding?.etTicketType)

        ticketTypeList.forEachIndexed { index, item ->
            popup.menu.add(0, index, index, item.name) // use correct field name
        }

        popup.setOnMenuItemClickListener { menuItem ->
            selectedTicketType = ticketTypeList[menuItem.itemId]

            dialogBinding?.etTicketType?.setText(selectedTicketType?.name)

            true
        }

        popup.show()
    }


}

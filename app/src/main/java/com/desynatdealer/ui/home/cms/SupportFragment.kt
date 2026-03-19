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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.adapter.TicketAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentSupportBinding
import com.desynatdealer.databinding.RaiseTicketDialogBinding
import com.desynatdealer.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class SupportFragment : BaseFragment() {
    private var binding: FragmentSupportBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = false
    private var ticketList = ArrayList<String>()
    private var ticketAdapter: TicketAdapter?=null
    private var search: Job? = null
    private var lastQuery: String? = null
    private var type = 0

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

                        }
                    }

                }
            })

            ticketAdapter = TicketAdapter(requireActivity(),ticketList,object : TicketAdapter.OnClick{
                override fun click(position: Int, type: Int) {
                    findNavController().navigate(R.id.action_supportFragment_to_ticketDetailFragment)
                }

            })
            rvTicket.adapter = ticketAdapter

            when(type){
                0->{
                   contact()
                }
                1->{
                   ticketManagement()
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
    }

    private fun ticketManagement(){
        binding?.apply {
            type = 1
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
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = RaiseTicketDialogBinding.inflate(LayoutInflater.from(context))
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

            tvToolbar.setOnClickListener {
                dialog.dismiss()
            }

            btnSubmit.setOnClickListener{
                dialog.dismiss()
            }

        }

        dialog.show()
    }


}

package com.desynatdealer.ui.home.cms

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentTidioChatBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class TidioChatFragment : BaseFragment() {
    private var binding: FragmentTidioChatBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTidioChatBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        binding?.webView?.apply {

            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.databaseEnabled = true

            settings.allowFileAccess = true
            settings.allowContentAccess = true

            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true

            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.loadsImagesAutomatically = true

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }

            // Optional but helpful
            settings.userAgentString =
                "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 Chrome/100 Safari/537.36"

            webViewClient = object : WebViewClient() {

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)

                    Log.d("WEBVIEW", "HTML Loaded")

                    // Debug HTML (like iOS)
                    view?.evaluateJavascript(
                        "(function() { return document.body.innerHTML; })();"
                    ) { html ->
                        Log.d("WEBVIEW_HTML", html ?: "empty")
                    }
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    Log.e("WEBVIEW", "Error: ${error?.description}")
                }
            }

            webChromeClient = WebChromeClient()

            loadUrl("file:///android_asset/tidio_chat.html")
          //  loadUrl("https://desynat.com/seller/chat-bot/107")
        }

    }

    override fun initControl() {
        binding?.apply {

            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                findNavController().popBackStack()
            }

        }
    }

    override fun observe() {
    }

}
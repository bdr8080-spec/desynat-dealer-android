package com.desynatdealer.ui.home.cms

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentTidioChatBinding
import com.desynatdealer.ui.home.response.GetSellerInfo
import com.desynatdealer.utils.SharedPreference
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TidioChatFragment : BaseFragment() {

    private var binding: FragmentTidioChatBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var userInfo : GetSellerInfo.Data?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTidioChatBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userInfo = SharedPreference.get(requireActivity()).getUser()
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        binding?.webView?.apply {

            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                allowFileAccess = true
                allowContentAccess = true
                useWideViewPort = true
                loadWithOverviewMode = true
                javaScriptCanOpenWindowsAutomatically = true
                loadsImagesAutomatically = true

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                }

                userAgentString =
                    "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 Chrome/100 Safari/537.36"
            }

            webViewClient = object : WebViewClient() {

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)

                    Log.d("WEBVIEW", "url : ${url}")
                    Log.d("WEBVIEW", "Page Loaded")
                    if (!url.isNullOrEmpty() && url.contains("tidio_chat.html")) {
                        injectUserData(view)
                    }

                    if (!url.isNullOrEmpty() && url.contains("#mobile-widget")) {
                        Log.d("WEBVIEW", "Chat fully loaded")
                        Handler(Looper.getMainLooper()).postDelayed({
                            binding?.progressBar?.visibility = View.GONE
                            binding?.view?.visibility = View.GONE
                        },300)

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
        }
    }


    private fun injectUserData(view: WebView?) {

        val name = "${userInfo?.firstName ?: ""} ${userInfo?.lastName ?: ""}".trim()
        val email = userInfo?.email ?: ""
        val userId = userInfo?.id.toString() ?: ""
        val role = "user"

        val safeName = escapeJs(name)
        val safeEmail = escapeJs(email)
        val safeUserId = escapeJs(userId)
        val safeRole = escapeJs(role)

        val js = "javascript:setUserData('$safeName','$safeEmail','$safeUserId','$safeRole')"

        view?.postDelayed({
            view.evaluateJavascript(js) {
                Log.d("WEBVIEW", "User injected")
            }
        }, 500) // 🔥 Important delay for stability
    }

    override fun initControl() {
        binding?.apply {

            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            ivClose.setOnClickListener {
                findNavController().popBackStack()
            }

            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                findNavController().popBackStack()
            }
        }
    }

    override fun observe() {

    }

    private fun escapeJs(value: String): String {
        return value.replace("'", "\\'")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
package com.desynatdealer.ui.home.cms

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentTidioChatBinding
import com.desynatdealer.ui.home.response.GetSellerInfo
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.Constants
import com.desynatdealer.utils.SharedPreference
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class TidioChatFragment : BaseFragment() {

    private var binding: FragmentTidioChatBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var userInfo : GetSellerInfo.Data?=null
    private var filePathCallback: ValueCallback<Array<Uri>>? = null


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
        if (Constants.language=="ar"){
            binding?.webView?.apply {

                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    databaseEnabled = true
                    allowFileAccess = true
                    allowContentAccess = true
                    javaScriptCanOpenWindowsAutomatically = true
                    loadsImagesAutomatically = true

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    }
                }

                // RTL support
                layoutDirection = View.LAYOUT_DIRECTION_RTL

                // JS bridge
                addJavascriptInterface(WebAppInterface(), "Android")

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)

                        injectUserData(view)

                        // Force Arabic
                        view?.evaluateJavascript(
                            """
                document.documentElement.lang = 'ar';
                document.documentElement.dir = 'rtl';
                """.trimIndent(),
                            null
                        )
                    }
                }

                loadUrl("file:///android_asset/tidio_chat_ar.html?lang=ar")

            }
        }
        else{
              binding?.webView?.apply {

         settings.apply {
             javaScriptEnabled = true
             domStorageEnabled = true
             databaseEnabled = true
             allowFileAccess = true
             allowContentAccess = true
             javaScriptCanOpenWindowsAutomatically = true
             loadsImagesAutomatically = true

             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                 mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
             }
         }

         // JS bridge for loader hide
         addJavascriptInterface(WebAppInterface(), "Android")

         webViewClient = object : WebViewClient() {
             override fun onPageFinished(view: WebView?, url: String?) {
                 super.onPageFinished(view, url)

                 Log.d("WEBVIEW", "Page Loaded")
                 injectUserData(view)
             }
         }

         webChromeClient = object : WebChromeClient() {

             override fun onShowFileChooser(
                 webView: WebView?,
                 filePathCallback: ValueCallback<Array<Uri>>?,
                 fileChooserParams: FileChooserParams?
             ): Boolean {

                 this@TidioChatFragment.filePathCallback?.onReceiveValue(null)
                 this@TidioChatFragment.filePathCallback = filePathCallback

                 return try {
                     val intent = fileChooserParams?.createIntent()

                     intent?.addFlags(
                         Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                 Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                     )

                     fileChooserLauncher.launch(intent)
                     true
                 } catch (e: Exception) {
                     this@TidioChatFragment.filePathCallback = null
                     false
                 }
             }
         }

         loadUrl("file:///android_asset/tidio_chat.html")
     }
        }



    }

    private var fileChooserLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (filePathCallback == null) return@registerForActivityResult

            val data = result.data

            val results = if (result.resultCode == Activity.RESULT_OK && data != null) {

                val uri = data.data

                if (uri != null) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val file = uriToFileSafe(requireContext(), uri)

                        file?.let {
                            arrayOf(Uri.fromFile(it))
                        }

                    } else {
                        arrayOf(uri)
                    }

                } else null

            } else null

            if (results == null) {
                filePathCallback?.onReceiveValue(null)
            } else {
                filePathCallback?.onReceiveValue(results)
            }

            filePathCallback = null
        }

    private fun injectUserData(view: WebView?) {

        val name = userInfo?.firstName ?: ""+" "+ userInfo?.lastName ?: ""
        val email = userInfo?.email ?: ""
        val userId = userInfo?.id.toString()

        val js = "javascript:setUserData('$name','$email','$userId','user')"

        view?.evaluateJavascript(js) {
            Log.d("WEBVIEW", "User injected")
        }
    }


    inner class WebAppInterface {
        @JavascriptInterface
        fun onChatLoaded() {
            Handler(Looper.getMainLooper()).post {
                binding?.progressBar?.visibility = View.GONE
                binding?.view?.visibility = View.GONE
            }
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


    fun uriToFileSafe(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null

            val file = File(context.cacheDir, "picked_image_${System.currentTimeMillis()}.jpg")
            file.outputStream().use { output ->
                inputStream.copyTo(output)
            }

            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}
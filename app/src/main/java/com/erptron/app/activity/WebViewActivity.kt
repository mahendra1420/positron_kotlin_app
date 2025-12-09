package com.positron.teachers.activity

import android.os.Bundle
import android.util.Log
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.positron.teachers.databinding.ActivityWebViewBinding

class WebViewActivity : BaseActivity() {

    lateinit var binding : ActivityWebViewBinding
    var document = ""
    var documentType = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(intent!=null){
            document = intent.getStringExtra("document").toString()
            documentType = intent.getStringExtra("type").toString()
            Log.e("asdfasdf" , "document ==> " + document)
            Log.e("asdfasdf" , "documentType ==> " + documentType)
            if (documentType == "link") {
                if(isOnline()) {
                    binding.webView.loadUrl(document)
                }
                binding.webView.settings.apply {
                    javaScriptEnabled = true
                    // Enable hardware acceleration
                    setRenderPriority(WebSettings.RenderPriority.HIGH)
                    // Configure caching
                    cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                }
                binding.webView.settings.javaScriptEnabled = true
            } else {
                binding.webView.loadUrl("https://docs.google.com/gview?embedded=true&url=$document")
                binding.webView.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                        // Handle URL redirects here if needed
                        return super.shouldOverrideUrlLoading(view, url)
                    }
                }
            }
        }

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }









    }
}
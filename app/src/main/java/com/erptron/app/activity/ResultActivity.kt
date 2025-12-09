package com.positron.teachers.activity

import android.annotation.SuppressLint

import android.os.Bundle
import android.webkit.WebViewClient
import com.positron.teachers.databinding.ActivityResultBinding


class ResultActivity : BaseActivity() {
    private lateinit var binding: ActivityResultBinding
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_result)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if(isOnline()) {
            binding.webView.loadUrl("https://carmelsteeldgp2.cloudsoftware.website/api/result/${prefs.getAdmissionNumber()}/Kj9Ulxafa5")
        }
        binding.webView.settings.javaScriptEnabled = true

        binding.webView.webViewClient = WebViewClient()
    }
}
package com.positron.teachers.activity

import android.annotation.SuppressLint

import android.os.Bundle
import android.webkit.WebViewClient
import com.positron.teachers.databinding.ActivityExamBinding


class ExamActivity : BaseActivity() {
    private lateinit var binding: ActivityExamBinding
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_exam)
        binding = ActivityExamBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if(isOnline()) {
            binding.webView.loadUrl("https://carmelsteeldgp2.cloudsoftware.website/api/exam_schedule/${prefs.getAdmissionNumber()}/Kj9Ulxafa5")
        }
        binding.webView.settings.javaScriptEnabled = true

        binding.webView.webViewClient = WebViewClient()
    }
}
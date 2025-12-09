package com.positron.teachers.activity

import android.content.Intent
import android.os.Bundle
import com.positron.teachers.R
import com.positron.teachers.databinding.ActivityFullDetailsBinding
import com.positron.teachers.model.NoticeData


class FullDetailsActivity : BaseActivity() {
    //Steel Carmel DGP
    private lateinit var binding: ActivityFullDetailsBinding

    private var pnoticeData : NoticeData? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
        if (intent != null) {
            pnoticeData = intent.getSerializableExtra("NoticeData") as NoticeData?
            // Log.e("MyLogData  ", "NoticeData" + pnoticeData)
        }

        binding.tvEntrytime.setOnClickListener {
            startActivity(
                Intent(
                    this@FullDetailsActivity,
                    WebViewActivity::class.java
                ).putExtra("document", pnoticeData?.attachment_url).putExtra("type" ,pnoticeData?.content_type )
            )
        }

        if (pnoticeData?.notice_date.isNullOrEmpty()) {
            binding.tvDate.text = pnoticeData?.notice_date
        } else {
            binding.tvDate.text = pnoticeData?.notice_date
        }

        if (pnoticeData?.content_type.equals("text")) {
            binding.tvDate.text = pnoticeData?.notice_date
            binding.tvDescription.setTextColor(resources.getColor(R.color.black))
            binding.tvDescription.text = "Message :- " + pnoticeData?.message
            binding.tvTitle.text = "Title : " + pnoticeData?.title

        } else if (pnoticeData?.content_type.equals("document")) {
            binding.tvDate.text = pnoticeData?.notice_date
            binding.tvTitle.text = "Title : " + pnoticeData?.title
            binding.tvEntrytime.setTextColor(resources.getColor(R.color.blue))
            binding.tvDescription.text = "Message :- " + pnoticeData?.message
            binding.tvEntrytime.text = pnoticeData?.attachment_url
        } else {
            binding.tvDate.text = pnoticeData?.notice_date
            binding.tvTitle.text = "Title : " + pnoticeData?.title
            binding.tvEntrytime.setTextColor(resources.getColor(R.color.blue))
            binding.tvDescription.text = "Message :- " + pnoticeData?.message
            binding.tvEntrytime.text = pnoticeData?.attachment_url
        }
    }
}
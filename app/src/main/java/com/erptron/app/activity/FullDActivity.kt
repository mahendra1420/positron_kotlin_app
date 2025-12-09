package com.positron.teachers.activity


import android.os.Bundle
import com.positron.teachers.R
import com.positron.teachers.databinding.ActivityFullDactivityBinding
import com.positron.teachers.model.PNoticeData

class FullDActivity : BaseActivity() {
    private lateinit var binding: ActivityFullDactivityBinding

    //Steel Carmel DGP
    private var pnoticeData: PNoticeData? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_full_dactivity)

        binding = ActivityFullDactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        if(intent!=null){
            pnoticeData = intent.getSerializableExtra("PNoticeData") as PNoticeData?

        }

        if(pnoticeData?.notice_date.isNullOrEmpty()){
            binding.tvDate.text = pnoticeData?.notice_date
        }else{
            binding.tvDate.text = pnoticeData?.notice_date
        }

        if (pnoticeData?.category.equals("Academic")) {
            binding.tvDate.text = pnoticeData?.notice_date
            binding.tvDescription.setTextColor(resources.getColor(R.color.blue))
            binding.tvDescription.text = pnoticeData?.message
            binding.tvTitle.text = "Title : "+ pnoticeData?.title
            binding.tvEntrytime.text ="Entry Time : "+ pnoticeData?.notice_date

        } else {
            binding.tvDate.text = pnoticeData?.notice_date
            binding.tvTitle.text = "Title : "+ pnoticeData?.title
            binding.tvDescription.setTextColor(resources.getColor(R.color.blue))
            binding.tvDescription.text = pnoticeData?.message
            binding.tvEntrytime.text = "Entry Time : "+pnoticeData?.notice_date
        }
    }
}
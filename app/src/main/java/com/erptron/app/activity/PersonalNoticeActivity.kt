package com.positron.teachers.activity


import android.content.Intent
import android.os.Bundle
import android.view.View
import com.positron.teachers.adapters.PersonalNoticeBoardAdapter
import com.positron.teachers.R
import com.positron.teachers.api.ApiClass
import com.positron.teachers.databinding.ActivityPersonalNoticeBinding
import com.positron.teachers.model.PNoticeData
import com.positron.teachers.model.PersonalNotice

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class PersonalNoticeActivity : BaseActivity(), View.OnClickListener,
    PersonalNoticeBoardAdapter.BookingDetailsAdapterInterface{
    //Steel Carmel DGP

    private lateinit var binding: ActivityPersonalNoticeBinding
    private var noticeDataList: MutableList<PNoticeData> = mutableListOf()
    private var noticeBoardAdapter: PersonalNoticeBoardAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPersonalNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnBack.setOnClickListener(this)
                if(isOnline()){
            getNoticeBoardDataFromDate()
        }
    }

    override fun onClick(v: View?) {
        val viewId = v?.id

        if (viewId == R.id.btnBack) {
            onBackPressed()
        }
    }
    private fun getNoticeBoardDataFromDate() {
        showProgressDialog()

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(120, TimeUnit.SECONDS) // Set connection timeout
            .readTimeout(120, TimeUnit.SECONDS)    // Set read timeout
            .writeTimeout(120, TimeUnit.SECONDS)   // Set write timeout
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(prefs.getErpUrl().toString())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        val loginApi = retrofit.create(ApiClass::class.java)

        val call = loginApi.getPersonalNotice(prefs.getAdmissionNumber().toString())

        call.enqueue(object : Callback<PersonalNotice<List<PNoticeData>>> {
            override fun onResponse(
                call: Call<PersonalNotice<List<PNoticeData>>>,
                response: Response<PersonalNotice<List<PNoticeData>>>
            ) {
                if (response.isSuccessful) {
                    noticeDataList.clear()
                    noticeDataList = response.body()?.personal_notice as MutableList<PNoticeData>
                    if (noticeDataList.isEmpty()) {
                        binding.rvNoticeBoard.visibility = View.GONE
                        binding.noDataLayout.visibility = View.VISIBLE
                    } else {
                        binding.rvNoticeBoard.visibility = View.VISIBLE
                        binding.noDataLayout.visibility = View.GONE
                        noticeBoardAdapter =
                            PersonalNoticeBoardAdapter(this@PersonalNoticeActivity, noticeDataList, this@PersonalNoticeActivity)
                        binding.rvNoticeBoard.adapter = noticeBoardAdapter
                    }
                    dismissProgressDialog()
                } else {
                    dismissProgressDialog()
                    showMessage(" Something went wrong : ${response.code()} \n NULL")
                }
            }

            override fun onFailure(call: Call<PersonalNotice<List<PNoticeData>>>, t: Throwable) {
                dismissProgressDialog()
                //  Log.e("MyResponse", " failure registerApi Error ==> $t.message")
                showMessage("Something went wrong : ${t.message.toString()} \n onFailure")
            }
        })
    }

    override fun onDocumentClick(item: PNoticeData?) {
        TODO("Not yet implemented")
    }

    override fun onSelected(item: PNoticeData?) {
        startActivity(
            Intent(
                this@PersonalNoticeActivity,
                FullDActivity::class.java
            ).putExtra("PNoticeData", item)
        )
    }
}
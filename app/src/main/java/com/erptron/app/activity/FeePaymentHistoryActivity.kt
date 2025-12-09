package com.positron.teachers.activity


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.positron.teachers.adapters.FeePaymentHistoryAdapter
import com.positron.teachers.R
import com.positron.teachers.api.ApiClass
import com.positron.teachers.databinding.ActivityFeePaymentHistoryBinding
import com.positron.teachers.model.FeePayment
import com.positron.teachers.model.FeePaymentData

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class FeePaymentHistoryActivity :  BaseActivity(), View.OnClickListener,
    FeePaymentHistoryAdapter.BookingDetailsAdapterInterface    {

    private lateinit var binding: ActivityFeePaymentHistoryBinding
    private var noticeDataList: MutableList<FeePaymentData> = mutableListOf()

    private var noticeBoardAdapter: FeePaymentHistoryAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_fee_payment_history)
        binding = ActivityFeePaymentHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnBack.setOnClickListener(this)
        if (isOnline()){
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

        val call = loginApi.getFeePaymentHistory(prefs.getAdmissionNumber().toString())

        call.enqueue(object : Callback<FeePayment<List<FeePaymentData>>> {
            override fun onResponse(
                call: Call<FeePayment<List<FeePaymentData>>>,
                response: Response<FeePayment<List<FeePaymentData>>>
            ) {
                if (response.isSuccessful) {
                    noticeDataList.clear()
                    noticeDataList = response.body()?.payment_history as MutableList<FeePaymentData>
                    if (noticeDataList.isEmpty()) {
                        binding.rvNoticeBoard.visibility = View.GONE
                        binding.noDataLayout.visibility = View.VISIBLE
                    } else {
                        binding.rvNoticeBoard.visibility = View.VISIBLE
                        binding.noDataLayout.visibility = View.GONE
                        noticeBoardAdapter =
                            FeePaymentHistoryAdapter(this@FeePaymentHistoryActivity, noticeDataList, this@FeePaymentHistoryActivity)
                        binding.rvNoticeBoard.adapter = noticeBoardAdapter
                    }
                    dismissProgressDialog()
                } else {
                    dismissProgressDialog()
                    showMessage(" Something went wrong : ${response.code()} \n NULL")
                }
            }

            override fun onFailure(call: Call<FeePayment<List<FeePaymentData>>>, t: Throwable) {
                dismissProgressDialog()
                //  Log.e("MyResponse", " failure registerApi Error ==> $t.message")
                showMessage("Something went wrong : ${t.message.toString()} \n onFailure")
            }
        })
    }

    override fun onDocumentClick(item: FeePaymentData?) {

    }

    override fun onSelected(item: FeePaymentData?) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(item!!.receipt_url)
        startActivity(intent)
    }
}
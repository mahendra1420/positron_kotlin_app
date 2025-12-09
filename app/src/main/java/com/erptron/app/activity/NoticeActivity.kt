package com.positron.teachers.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import com.positron.teachers.adapters.NoticeBoardAdapter
import com.positron.teachers.R
import com.positron.teachers.api.ApiClass
import com.positron.teachers.databinding.ActivityNoticeBinding
import com.positron.teachers.model.NoticeBoardWeb
import com.positron.teachers.model.NoticeData
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

class NoticeActivity : BaseActivity(), View.OnClickListener,
    NoticeBoardAdapter.BookingDetailsAdapterInterface {

    private lateinit var binding: ActivityNoticeBinding
    private var noticeDataList: MutableList<NoticeData> = mutableListOf()
    private var noticeBoardAdapter: NoticeBoardAdapter? = null
    //Steel Carmel DGP
    private val calendar = Calendar.getInstance()
    private var yearNow = calendar.get(Calendar.YEAR)
    private var monthNow = calendar.get(Calendar.MONTH)
    private var dayNow = calendar.get(Calendar.DAY_OF_MONTH)
    private var selectedMonth: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener(this)
        binding.ivCalendar.setOnClickListener(this)
        //Log.e("MyLogData ", "FCM TOKEN ==>"+prefs.getFcmToken())
        if(isOnline()){
        getNoticeBoardDataFromDate((monthNow+1).toString(), yearNow.toString())}
    }

    override fun onClick(v: View?) {
        val viewId = v?.id

        if (viewId == R.id.btnBack) {
            onBackPressed()
        } else if (viewId == R.id.iv_calendar) {
            if(isOnline()){
            createDialogWithoutDateField()}
        }
    }

    private fun getNoticeBoardData() {
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

        val call = loginApi.getNoticeBoardData()

        call.enqueue(object : Callback<NoticeBoardWeb<List<NoticeData>>> {
            override fun onResponse(
                call: Call<NoticeBoardWeb<List<NoticeData>>>,
                response: Response<NoticeBoardWeb<List<NoticeData>>>
            ) {
                if (response.isSuccessful) {
                    dismissProgressDialog()
                    noticeDataList.clear()
                    noticeDataList = response.body()?.personal_notice as MutableList<NoticeData>
                    noticeBoardAdapter =
                        NoticeBoardAdapter(this@NoticeActivity, noticeDataList, this@NoticeActivity)
                    binding.rvNoticeBoard.adapter = noticeBoardAdapter
                } else {
                    dismissProgressDialog()
                    showMessage(" Something went wrong : ${response.code()} \n NULL")
                }
            }

            override fun onFailure(call: Call<NoticeBoardWeb<List<NoticeData>>>, t: Throwable) {
                dismissProgressDialog()
                Log.e("MyResponse", " failure registerApi Error ==> $t.message")
                showMessage("Something went wrong : ${t.message.toString()} \n onFailure")
            }
        })
    }

    private fun getNoticeBoardDataFromDate(month: String, year: String) {
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

        val call = loginApi.getNoticeBoardDataFromDate(month = month, year = year)

        call.enqueue(object : Callback<NoticeBoardWeb<List<NoticeData>>> {
            override fun onResponse(
                call: Call<NoticeBoardWeb<List<NoticeData>>>,
                response: Response<NoticeBoardWeb<List<NoticeData>>>
            ) {
                if (response.isSuccessful) {
                    noticeDataList.clear()
                    noticeDataList = response.body()?.personal_notice as MutableList<NoticeData>
                    if (noticeDataList.isEmpty()) {
                        binding.rvNoticeBoard.visibility = View.GONE
                        binding.noDataLayout.visibility = View.VISIBLE
                    } else {
                        binding.rvNoticeBoard.visibility = View.VISIBLE
                        binding.noDataLayout.visibility = View.GONE
                        noticeBoardAdapter =
                            NoticeBoardAdapter(this@NoticeActivity, noticeDataList, this@NoticeActivity)
                        binding.rvNoticeBoard.adapter = noticeBoardAdapter
                    }
                    dismissProgressDialog()
                } else {
                    dismissProgressDialog()
                    showMessage(" Something went wrong : ${response.code()} \n NULL")
                }
            }

            override fun onFailure(call: Call<NoticeBoardWeb<List<NoticeData>>>, t: Throwable) {
                dismissProgressDialog()
                Log.e("MyResponse", " failure registerApi Error ==> $t.message")
                showMessage("Something went wrong : ${t.message.toString()} \n onFailure")
            }
        })
    }

    @SuppressLint("QueryPermissionsNeeded")
    override fun onDocumentClick(item: NoticeData?) {
        if (item!!.content_type.equals("document")) {
            val url = "https://drive.google.com/u/0/uc?id=${item.attachment_url}&export=download"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        } else if (item.content_type.equals("link")) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(item.attachment_url)
            startActivity(intent)
        }
    }

    override fun onSelected(item: NoticeData?) {
        startActivity(
            Intent(
                this@NoticeActivity,
                FullDetailsActivity::class.java
            ).putExtra("NoticeData", item)
        )
    }

    private fun createDialogWithoutDateField() {
        val now = System.currentTimeMillis() - 1000
        val monthDatePickerDialog = object : DatePickerDialog(
            this, AlertDialog.THEME_HOLO_LIGHT, { view, year, month, dayOfMonth ->
                selectedMonth = month + 1
                getNoticeBoardDataFromDate(month = (month+1).toString(), year = year.toString())
            }, yearNow, monthNow, dayNow
        ) {
            @SuppressLint("DiscouragedApi")
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                datePicker.findViewById<View>(resources.getIdentifier("day", "id", "android"))
                    .visibility = View.GONE
                datePicker.maxDate = now
            }
        }
        monthDatePickerDialog.setTitle("Select Month")
        monthDatePickerDialog.show()
    }


}
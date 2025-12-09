package com.positron.teachers.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog

import android.os.Bundle
import android.util.Log
import android.view.View
import com.positron.teachers.adapters.StudyAdapter
import com.positron.teachers.R
import com.positron.teachers.api.ApiClass
import com.positron.teachers.databinding.ActivityStudyMaterialBinding
import com.positron.teachers.model.StudyMaterial
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

class StudyMaterialActivity : BaseActivity(), View.OnClickListener,
    StudyAdapter.BookingDetailsAdapterInterface{
    private lateinit var binding: ActivityStudyMaterialBinding


    private var noticeDataList: MutableList<StudyMaterial> = mutableListOf()
    private var studyAdapter: StudyAdapter? = null

    private val calendar = Calendar.getInstance()
    private var yearNow = calendar.get(Calendar.YEAR)
    private var monthNow = calendar.get(Calendar.MONTH)
    private var dayNow = calendar.get(Calendar.DAY_OF_MONTH)
    private var selectedMonth: Int? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_study_material)
        binding = ActivityStudyMaterialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener(this)
        binding.ivCalendar.setOnClickListener(this)
        if(isOnline()){
        getNoticeBoardDataFromDate((monthNow+1).toString(), yearNow.toString())
        }
    }

    override fun onClick(v: View?) {
        val viewId = v?.id

        if (viewId == R.id.btnBack) {
            onBackPressed()
        } else if (viewId == R.id.iv_calendar) {
            createDialogWithoutDateField()
        }
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

        val call = loginApi.getStudyMaterial(month = month, year = year, admission_no = prefs.getAdmissionNumber().toString())

        call.enqueue(object : Callback<List<StudyMaterial>> {
            override fun onResponse(
                call: Call<List<StudyMaterial>>,
                response: Response<List<StudyMaterial>>
            ) {

                if (response.isSuccessful) {
                    noticeDataList.clear()
                    val data = response.body()
                    Log.e("MyLogData" ," ===== " + data.toString())
                   noticeDataList = response.body() as MutableList<StudyMaterial>
                    if (noticeDataList.isEmpty()) {
                        binding.rvStudy.visibility = View.GONE
                        binding.noDataLayout.visibility = View.VISIBLE
                    } else {
                        binding.rvStudy.visibility = View.VISIBLE
                        binding.noDataLayout.visibility = View.GONE
                        studyAdapter =
                            StudyAdapter(this@StudyMaterialActivity, noticeDataList, this@StudyMaterialActivity)
                        binding.rvStudy.adapter = studyAdapter

                    }
                    dismissProgressDialog()
                } else {
                    dismissProgressDialog()
                    showMessage("Something went wrong : ${response.code()} \n NULL")
                }
            }

            override fun onFailure(call: Call<List<StudyMaterial>>, t: Throwable) {
                dismissProgressDialog()
                //  Log.e("MyResponse", " failure registerApi Error ==> $t.message")
                showMessage("Something went wrong : ${t.message.toString()} \n onFailure")
            }
        })
    }

    override fun onDocumentClick(item: StudyMaterial?) {
/*        if (item!!.file_location.equals("GDrive")) {
            val url = "https://drive.google.com/u/0/uc?id=${item.document}&export=download"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        } else if (item.document.equals("Link")) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(item.document)
            startActivity(intent)
        }*/
    }

    override fun onSelected(item: StudyMaterial?) {

    }
}
package com.positron.teachers.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.positron.teachers.adapters.AttendanceCorrectionAdapter
import com.positron.teachers.R
import com.positron.teachers.api.ApiClass
import com.positron.teachers.databinding.ActivityAttendanceCorrectionBinding
import com.positron.teachers.model.AttendanceData
import com.positron.teachers.model.AttendanceDays
import com.positron.teachers.model.SaveAttendance
import com.positron.teachers.model.StudentAttendanceHistoryByDay
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class AttendanceCorrectionActivity : BaseActivity()  , AttendanceCorrectionAdapter.BookingDetailsAdapterInterface{

    lateinit var binding : ActivityAttendanceCorrectionBinding
    private var attendanceCorrectionData : AttendanceData? = null
    var monthToSend = ""
    var yearToSend = ""
    private var attendanceHistoryList : MutableList<AttendanceDays> = mutableListOf()
    private var attendanceHistoryAdapter: AttendanceCorrectionAdapter? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttendanceCorrectionBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (intent != null) {
            attendanceCorrectionData = intent.getSerializableExtra("attendanceData") as AttendanceData?
            monthToSend = intent.getStringExtra("month").toString()
            yearToSend = intent.getStringExtra("year").toString()
        }

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.ivLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        binding.btnSave.setOnClickListener {
            getSaveAttendance()
        }

        binding.tvStudentName.text = attendanceCorrectionData!!.student_name.toString()
        binding.tvAdmnNo.text = "ADMN NO.: ${attendanceCorrectionData!!.admission_no.toString()}"
        binding.tvClass.text = "CLASS: ${attendanceCorrectionData!!.class_name.toString()}-${attendanceCorrectionData!!.section_name.toString()}"
        binding.tvRollNo.text = "ROLL NO.: ${attendanceCorrectionData!!.roll_no.toString()}"


        Glide.with(this)
            .load(attendanceCorrectionData!!.student_photo.toString())
            .apply(RequestOptions())
            .placeholder(R.drawable.dummy)
            .error(R.drawable.dummy)
            .into(binding.profileImage)

        getStudentsAttendanceHistory()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getSaveAttendance() {
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

        val attendanceDaysArray = JsonArray()
        val jsonArray = JsonArray()
        for (i in 0 until attendanceHistoryList.size) {
            jsonArray.add(attendanceHistoryList[i].date)
        }

        val jsonArrayNew = JsonArray()
        for (j in 0 until attendanceHistoryList.size) {
            jsonArrayNew.add(attendanceHistoryList[j].status)
        }

        for (i in 0 until attendanceHistoryList.size) {
            val attendanceDay = JsonObject().apply {
                addProperty("date", attendanceHistoryList[i].date)
                addProperty("status", attendanceHistoryList[i].status)
            }
            attendanceDaysArray.add(attendanceDay)
        }


        val requestObject = JsonObject().apply {
            addProperty("month", monthToSend)
            addProperty("year", yearToSend)
            addProperty("student_id", attendanceCorrectionData!!.student_id)

            // Create attendance_days array
            val attendanceDaysArray = JsonArray()
            for (attendance in attendanceHistoryList) {
                val attendanceDay = JsonObject().apply {
                    addProperty("date", attendance.date)
                    addProperty("status", attendance.status)
                }
                attendanceDaysArray.add(attendanceDay)
            }

            add("attendance_days", attendanceDaysArray) // Add attendance_days to the main object
        }

        Log.e("MyLogData", " student_session_id ==> " + jsonArray)
        Log.e("MyLogData", " attendence_type_id ==> " + jsonArrayNew)
        Log.e("MyLogData", " attendanceDaysArray ==> " + attendanceDaysArray.toString())

        val call = loginApi.saveStudentMonthAttendanceHistory(
            "Bearer ${prefs.getAuthorizationToken().toString()}",requestObject)

        call.enqueue(object : Callback<SaveAttendance> {
            override fun onResponse(
                call: Call<SaveAttendance>,
                response: Response<SaveAttendance>,
            ) {
                if (response.isSuccessful) {
                    if (response.body()?.result?.isNotEmpty() == true) {
                        dismissProgressDialog()
                        showMessage("Attendance Record Saved")
                    } else {
                        dismissProgressDialog()
                        showMessage("Attendance Record Saved")
                    }
                } else {
                    dismissProgressDialog()
                    showMessage(" Something went wrong : ${response.code()} \n NULL")
                }
            }

            override fun onFailure(call: Call<SaveAttendance>, t: Throwable) {
                dismissProgressDialog()
                // Log.e("MyLogData", " failure registerApi Error ==> $t.message")
                showMessage("Something went wrong : ${t.message.toString()} \n onFailure")
            }
        })


    }

    private fun showLogoutConfirmationDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        val customLayout = layoutInflater.inflate(R.layout.alert_dialog_logout, null)
        dialogBuilder.setView(customLayout)
        dialogBuilder.setPositiveButton("OK") { dialogInterface, _ ->
            logOut()
        }
        dialogBuilder.setNegativeButton("Cancel") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    private fun logOut() {
        showProgressDialog()
        prefs.clear()
        startActivity(Intent(this@AttendanceCorrectionActivity, TeacherIdActivity::class.java))
        finish()
        dismissProgressDialog()
    }

    private fun getStudentsAttendanceHistory() {
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

        val call = loginApi.getStudentMonthAttendanceHistory(
            "Bearer ${prefs.getAuthorizationToken().toString()}",
            prefs.getClassTeacherClassId().toString(),
            prefs.getTeacherClassSectionId().toString(),
            monthToSend,
            yearToSend,
            attendanceCorrectionData?.student_id.toString()
        )

        call.enqueue(object : Callback<StudentAttendanceHistoryByDay> {
            override fun onResponse(
                call: Call<StudentAttendanceHistoryByDay>,
                response: Response<StudentAttendanceHistoryByDay>,
            ) {
                Log.e("MyResponse", "Successful ==> ${response.body().toString()}")
                if (response.isSuccessful) {

                    attendanceHistoryList.clear()
                    attendanceHistoryList.addAll(response.body()?.attendance_data!!.attendance_days)
                    //response.body()?.attendance_data?.let { attendanceHistoryList.addAll(it) } as MutableList<AttendanceData>
                    if (attendanceHistoryList.isEmpty()) {
                        //     Log.e("MyLogData","if")
                        binding.NestedScrollView.visibility = View.GONE
                        binding.rvAttendance.visibility = View.GONE
                        binding.noDataLayout.visibility = View.VISIBLE
                        binding.btnSave.visibility = View.GONE
                    } else {
                        binding.btnSave.visibility = View.VISIBLE
                        binding.NestedScrollView.visibility = View.VISIBLE
                        binding.rvAttendance.visibility = View.VISIBLE
                        binding.noDataLayout.visibility = View.GONE
                        attendanceHistoryAdapter =
                            AttendanceCorrectionAdapter(
                                this@AttendanceCorrectionActivity,
                                attendanceHistoryList,
                                this@AttendanceCorrectionActivity
                            )
                        binding.rvAttendance.adapter = attendanceHistoryAdapter

                    }
                    dismissProgressDialog()
                } else {
                    dismissProgressDialog()
                    showMessage(" Something went wrong : ${response.code()} \n NULL")
                }
            }

            override fun onFailure(call: Call<StudentAttendanceHistoryByDay>, t: Throwable) {
                dismissProgressDialog()
                Log.e("MyResponse", " failure registerApi Error ==> ${t.message}")
                showMessage("Something went wrong : ${t.message.toString()} \n onFailure")
            }
        })
    }

    override fun onSelected(position: Int, newMarks: String) {

    }

}
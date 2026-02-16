package com.positron.teachers.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.positron.teachers.adapters.AttendanceHistoryAdapter
import com.positron.teachers.R
import com.positron.teachers.api.ApiClass
import com.positron.teachers.databinding.ActivityAttendanceHistroyBinding
import com.positron.teachers.model.AttendanceData
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class AttendanceHistoryActivity : BaseActivity() , AttendanceHistoryAdapter.AttendanceHistoryAdapterInterface{

    lateinit var binding: ActivityAttendanceHistroyBinding
    var isStartDate = false
    var cal = Calendar.getInstance()
    var date = ""
    var monthToSend = ""
    var yearToSend = ""
    private var attendanceHistoryList : MutableList<AttendanceData> = mutableListOf()
    private var attendanceHistoryAdapter: AttendanceHistoryAdapter? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttendanceHistroyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val today: LocalDate = LocalDate.now()
        monthToSend = today.monthValue.toString() // Retrieves the numeric month (1-12)
        yearToSend = today.year.toString()
        val sdf = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        val formattedDate = sdf.format(cal.time)
        date = formattedDate
        binding.tvDate.text = formattedDate
        Log.e("AttendancetActivity", "monthToSend 1 ==> ${monthToSend}")
        Log.e("AttendancetActivity", "yearToSend 1 ==> ${yearToSend}")
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.ivLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        binding.ivCalendar.setOnClickListener {
            isStartDate = true
            //openDatePicker()
            openMonthPicker()
        }

        /*        binding.ivUp.setOnClickListener{
                    showPlusDate()

                }
                binding.ivDown.setOnClickListener{
                    showPreviousDate()
                }*/

        binding.editTextClass.setOnClickListener {
            binding.editTextSection.setText("Select")
            prefs.setTeacherClassSectionName("")
            prefs.setTeacherClassSectionId("")
            attendanceHistoryList.clear()
            attendanceHistoryAdapter?.notifyDataSetChanged()
            startActivity(
                Intent(
                    this@AttendanceHistoryActivity,
                    DialogActivity::class.java
                ).putExtra("type", 1)
            )
        }
        binding.editTextSection.setOnClickListener {
            if (binding.editTextClass.text.isNotEmpty()) {
                attendanceHistoryList.clear()
                attendanceHistoryAdapter?.notifyDataSetChanged()
                startActivity(
                    Intent(
                        this@AttendanceHistoryActivity,
                        DialogActivity::class.java
                    ).putExtra("type", 2)
                )
            } else {
                showMessage("please select class")
            }
        }

        binding.btnSearch.setOnClickListener {
            if (binding.editTextSection.text.isNotEmpty()) {
                if (binding.editTextSection.text.isNotEmpty()) {
                    if (binding.tvDate.text.isNotEmpty()) {
                        if (isOnline()) {
                            getStudentsAttendanceHistory()
                        }
                    } else {
                        showMessage("please select Date")
                    }
                } else {
                    showMessage("please select section")
                }
            } else {
                showMessage("please select Class")
            }
        }

    }

    override fun onResume() {
        super.onResume()
        try {
            binding.editTextClass.setText(prefs.getClassTeacherClassName())
            binding.editTextSection.setText(prefs.getTeacherClassSectionName())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openMonthPicker() {
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, _ ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)

                // Format the selected month and year
                val sdf = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                val formattedDate = sdf.format(cal.time)
                date = formattedDate
                monthToSend =
                    (cal.get(Calendar.MONTH) + 1).toString() // +1 because Calendar.MONTH is 0-based
                yearToSend = cal.get(Calendar.YEAR).toString()

                Log.e("AttendanceActivity", "monthToSend ==> $monthToSend")
                Log.e("AttendanceActivity", "yearToSend ==> $yearToSend")

                // Update the UI
                binding.tvDate.text = formattedDate
            }

        val dialog = DatePickerDialog(
            binding.root.context,
            dateSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )

        // Hide the day spinner
        dialog.datePicker.findViewById<View>(
            Resources.getSystem().getIdentifier("day", "id", "android")
        )?.visibility = View.GONE

        dialog.show()
    }

    /*    private fun openDatePicker() {
            val dateSetListener =
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val sdf = SimpleDateFormat("MMMM yyyy")
                    val tvdate = sdf.format(cal.time)
                    date = sdf.format(cal.time)
                    monthToSend = (cal.get(Calendar.MONTH) + 1).toString() // +1 because Calendar.MONTH is 0-based
                    yearToSend = cal.get(Calendar.YEAR).toString()
                    Log.e("AttendancetActivity" , "monthToSend ==> ${monthToSend}")
                    Log.e("AttendancetActivity" , "yearToSend ==> ${yearToSend}")
                    if (isStartDate) {
                        startDate = tvdate
                    } else {
                        endtDate = tvdate
                    }
                    binding.tvDate.text = startDate
                }

            binding.root.context?.let {
                DatePickerDialog(
                    it,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        }*/

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
        startActivity(Intent(this@AttendanceHistoryActivity, TeacherIdActivity::class.java))
        finish()
        dismissProgressDialog()
    }

    private fun getStudentsAttendanceHistory() {
        showProgressDialog()
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val authInterceptor = okhttp3.Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ${prefs.getAuthorizationToken()}")
                .build()
            chain.proceed(request)
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(prefs.getErpUrl().toString())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        val loginApi = retrofit.create(ApiClass::class.java)

        val call = loginApi.getTotalAttendanceForMonth(
            prefs.getClassTeacherClassId().toString(),
            prefs.getTeacherClassSectionId().toString(),
            yearToSend,
            monthToSend
        )

        call.enqueue(object : Callback<List<AttendanceData>> {
            override fun onResponse(
                call: Call<List<AttendanceData>>,
                response: Response<List<AttendanceData>>,
            ) {
                if (response.isSuccessful) {
                    val studentList = response.body() ?: emptyList()
                    attendanceHistoryList.clear()
                    attendanceHistoryList.addAll(studentList)
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
                            AttendanceHistoryAdapter(
                                this@AttendanceHistoryActivity,
                                attendanceHistoryList,
                                this@AttendanceHistoryActivity
                            )
                        binding.rvAttendance.adapter = attendanceHistoryAdapter

                    }
                    dismissProgressDialog()
                } else {
                    dismissProgressDialog()
                    showMessage(" Something went wrong : ${response.code()} \n NULL")
                }
            }

            override fun onFailure(call: Call<List<AttendanceData>>, t: Throwable) {
                dismissProgressDialog()
                Log.e("MyResponse", " failure registerApi Error ==> ${t.message}")
                showMessage("Something went wrong : ${t.message.toString()} \n onFailure")
            }
        })
    }

    override fun onSelected(attendanceData: AttendanceData) {
        startActivity(Intent(this@AttendanceHistoryActivity , AttendanceCorrectionActivity::class.java).putExtra("attendanceData" , attendanceData)
            .putExtra("month" , monthToSend).putExtra("year" , yearToSend))
        Log.e("AttendanceHistory" , "onSelected")
    }

}
package com.positron.teachers.activity

import Attendance
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.view.WindowCompat
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.positron.teachers.adapters.AttendanceTeacherAdapter
import com.positron.teachers.R
import com.positron.teachers.api.ApiClass
import com.positron.teachers.databinding.ActivityAttendancetBinding
import com.positron.teachers.model.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*
import java.util.concurrent.TimeUnit

class AttendancetActivity : BaseActivity(), AttendanceTeacherAdapter.BookingDetailsAdapterInterface,
    View.OnClickListener {
    private lateinit var binding: ActivityAttendancetBinding
    private var classDataList: MutableList<Attendance> = mutableListOf()

    private var attendanceTeacherAdapter: AttendanceTeacherAdapter? = null

    private val calendar = Calendar.getInstance()
    private var yearNow = calendar.get(Calendar.YEAR)
    private var monthNow = calendar.get(Calendar.MONTH)
    private var dayNow = calendar.get(Calendar.DAY_OF_MONTH)
    private var selectedMonth: Int? = null
    var isChecked = false
    var cal = Calendar.getInstance()
    var date = ""
    var isStartDate = false
    var startDate = ""
    var endtDate = ""
    var monthToSend = ""
    var yearToSend = ""

    @RequiresApi(Build.VERSION_CODES.O)
    val currentDate = LocalDate.now()

    @RequiresApi(Build.VERSION_CODES.O)
    val formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.ENGLISH)

    @RequiresApi(Build.VERSION_CODES.O)
    val formatter1 = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    @RequiresApi(Build.VERSION_CODES.O)
    val formattedDate = currentDate.format(formatter1)

    @RequiresApi(Build.VERSION_CODES.O)
    val currentDateString = formattedDate
    private var daysToGoBack = 0

    @RequiresApi(Build.VERSION_CODES.O)
    val today = LocalDate.now()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_attendancet)
        WindowCompat.setDecorFitsSystemWindows(window,false)
        binding = ActivityAttendancetBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnBack.setOnClickListener(this)
        binding.btnSave.setOnClickListener(this)
        binding.btnSearch.setOnClickListener(this)
        binding.ivUp.setOnClickListener(this)
        binding.ivDown.setOnClickListener(this)
        binding.ivLogout.setOnClickListener(this)
        binding.ivCalendar.setOnClickListener(this)

        binding.editTextClass.setOnClickListener(this)
        binding.editTextSection.setOnClickListener(this)
        binding.tvDate.text = formattedDate
        binding.ivRight.setOnClickListener {
            if (isChecked) {
                isChecked = false
                val newImageResource = R.drawable.wrong // Replace with your new image resource
                binding.ivRight.setImageResource(newImageResource)
            } else {
                isChecked = true
                val newImageResource = R.drawable.right // Replace with your new image resource
                binding.ivRight.setImageResource(newImageResource)
            }
        }

        //getTeacherClass()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View?) {
        val viewId = v?.id
        if (viewId == R.id.btnBack) {
            onBackPressed()
        } else if (viewId == R.id.iv_logout) {
            //logOut()
            showLogoutConfirmationDialog()
        } else if (viewId == R.id.iv_calendar) {
            if (isOnline()) {
                //createDialogWithoutDateField()
                isStartDate = true
                openDatePicker()
            }
        } else if (viewId == R.id.iv_down) {
            //daysToGoBack -= 1
            showPreviousDate()
        } else if (viewId == R.id.iv_up) {
            // daysToGoBack += 1
            //showPreviousDate()
            showPlusDate()
        } else if (viewId == R.id.editTextClass) {
            binding.editTextSection.setText("Select")
            prefs.setTeacherClassSectionName("")
            prefs.setTeacherClassSectionId("")
            classDataList.clear()
            attendanceTeacherAdapter?.notifyDataSetChanged()
            startActivity(
                Intent(
                    this@AttendancetActivity,
                    DialogActivity::class.java
                ).putExtra("type", 1)
            )
        } else if (viewId == R.id.editTextSection) {
            if (binding.editTextClass.text.isNotEmpty()) {
                classDataList.clear()
                attendanceTeacherAdapter?.notifyDataSetChanged()
                startActivity(
                    Intent(
                        this@AttendancetActivity,
                        DialogActivity::class.java
                    ).putExtra("type", 2)
                )
            } else {
                showMessage("please select class")
            }
        } else if (viewId == R.id.btnSearch) {
            if (binding.editTextSection.text.isNotEmpty()) {
                if (binding.tvDate.text.isNotEmpty()) {
                    if (isOnline()) {
                        // Log.e("MyLogData" , "getStudentsAttendance===  " + binding.tvDate.text.toString())
                        getStudentsAttendance()
                    }
                } else {
                    showMessage("please select Date")
                }
            } else {
                showMessage("please select section")
            }
        } else if (viewId == R.id.btnSave) {
            //if (binding.editTextSection.text.isNotEmpty()) {}
            if (isOnline()) {
                // Log.e("MyLogData","getSaveAttendance== " + binding.tvDate.text.toString())
                getSaveAttendance()
            }
            // else {
            //     showMessage("please select section")
            // }
        }
    }

    private fun createDialogWithoutDateField() {
        // val now = System.currentTimeMillis() - 1000
        val now = Calendar.getInstance()
        val monthDatePickerDialog = object : DatePickerDialog(
            this, android.app.AlertDialog.THEME_HOLO_LIGHT, { view, year, month, dayOfMonth ->
                selectedMonth = month + 1
                // getNoticeBoardDataFromDate(month = (month+1).toString(), year = year.toString())
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)
        ) {
            @SuppressLint("DiscouragedApi")
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                datePicker.findViewById<View>(resources.getIdentifier("day", "id", "android"))
                    .visibility = View.GONE
                datePicker.maxDate = now.timeInMillis
            }
        }
        monthDatePickerDialog.setTitle("Select Month")
        monthDatePickerDialog.show()
    }

    private fun openDatePicker() {
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val sdf = SimpleDateFormat("dd-MM-yyyy")
                val tvdate = sdf.format(cal.time)
                date = sdf.format(cal.time)

                if (isStartDate) {
                    startDate = tvdate
                } else {
                    endtDate = tvdate
                }
                binding.tvDate.text = startDate

            }

        binding.root.context?.let {
            val datePickerDialog = DatePickerDialog(
                it,
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            // Set maxDate to prevent selecting future dates
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
            datePickerDialog.show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showPreviousDate() {
        try {
            val currentDateText = binding.tvDate.text.toString().trim()
            if (currentDateText.isBlank()) {
                showMessage("Date cannot be empty.")
                return
            }

            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy") // Adjust if needed
            val currentDate = LocalDate.parse(currentDateText, formatter)
            val previousDate = currentDate.minusDays(1)

            binding.tvDate.text = previousDate.format(formatter)
        } catch (e: DateTimeParseException) {
            showMessage("Invalid date format.")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showPlusDate() {
        try {
            val currentDateText = binding.tvDate.text.toString().trim()
            if (currentDateText.isBlank()) {
                showMessage("Date cannot be empty.")
                return
            }

            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy") // Adjust format as needed
            val currentDate = LocalDate.parse(currentDateText, formatter)
            val newDate = currentDate.plusDays(1)

            val today = LocalDate.now()
            if (newDate <= today) {
                binding.tvDate.text = newDate.format(formatter)
            } else {
                showMessage("Future dates are not allowed.")
            }
        } catch (e: DateTimeParseException) {
            showMessage("Invalid date format.")
        }
    }


    /*@RequiresApi(Build.VERSION_CODES.O)
    private fun showPreviousDate() {
        try {
        val currentDateText = binding.tvDate.text.toString()
        val currentDate = LocalDate.parse(currentDateText)  // Use ThreeTenABP for date handling
        val previousDate = currentDate.minusDays(1)
        binding.tvDate.text = previousDate.toString()

        } catch (e: DateTimeParseException) {
            showMessage("Invalid date format.")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showPlusDate() {
        try {
        val currentDateText = binding.tvDate.text.toString()
        val currentDate = LocalDate.parse(currentDateText)
        val newDate = currentDate.plusDays(1)
        if (newDate <= today) {
            binding.tvDate.text = newDate.toString()
        } else {
            showMessage("Future dates are not allowed.")
        }
        } catch (e: DateTimeParseException) {
            showMessage("Invalid date format.")
        }
    }*/

    private fun showLogoutConfirmationDialog() {
        val dialogBuilder = AlertDialog.Builder(this)


        val customLayout = layoutInflater.inflate(R.layout.alert_dialog_logout, null)
        dialogBuilder.setView(customLayout)

        // dialogBuilder.setMessage("Are you sure you want to logout?")

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
        startActivity(Intent(this@AttendancetActivity, TeacherIdActivity::class.java))
        finish()
        dismissProgressDialog()
    }

    /*private fun getTeacherClass() {
        showProgressDialog()

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(prefs.getErpUrl().toString())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        val loginApi = retrofit.create(ApiClass::class.java)

        val call = loginApi.getTeacherClass(prefs.getTeacherId().toString())

        call.enqueue(object : Callback<List<Teacherclass>> {
            override fun onResponse(
                call: Call<List<Teacherclass>>,
                response: Response<List<Teacherclass>>
            ) {

                if (response.isSuccessful) {
                    classDataList.clear()
                    val data = response.body()
                    Log.e("MyLogData" ," ===== " + data.toString())
                    classDataList = response.body() as MutableList<Teacherclass>
                    if (classDataList.isEmpty()) {
                        Log.e("MyLogData","if")
                     //   binding.rvStudy.visibility = View.GONE
                      //  binding.noDataLayout.visibility = View.VISIBLE
                    } else {
                        Log.e("MyLogData","else" + classDataList.get(0).`class`)
                        showMessage(classDataList.get(0).`class`)
                      //  binding.rvStudy.visibility = View.VISIBLE
                      //  binding.noDataLayout.visibility = View.GONE
                      //  studyAdapter =
                      //      StudyAdapter(this@StudyMaterialActivity, noticeDataList, this@StudyMaterialActivity)
                      //  binding.rvStudy.adapter = studyAdapter

                    }
                    dismissProgressDialog()
                } else {
                    dismissProgressDialog()
                    showMessage("Something went wrong : ${response.code()} \n NULL")
                }
            }

            override fun onFailure(call: Call<List<Teacherclass>>, t: Throwable) {
                dismissProgressDialog()
                //  Log.e("MyResponse", " failure registerApi Error ==> $t.message")
                showMessage("Something went wrong : ${t.message.toString()} \n onFailure")
            }
        })
    }*/

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getStudentsAttendance() {
        showProgressDialog()

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(prefs.getErpUrl().toString())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        val api = retrofit.create(ApiClass::class.java)

        val call = api.getStudentsAttendance(
            "Bearer ${prefs.getAuthorizationToken()}",
            prefs.getClassTeacherClassId().toString(),
            prefs.getTeacherClassSectionId().toString(),
            binding.tvDate.text.toString()
        )

        call.enqueue(object : Callback<List<Attendance>> {

            override fun onResponse(
                call: Call<List<Attendance>>,
                response: Response<List<Attendance>>
            ) {
                dismissProgressDialog()

                if (!response.isSuccessful) {
                    showMessage("Error: ${response.code()}")
                    return
                }

                val list = response.body() ?: emptyList()

                classDataList.clear()
                classDataList.addAll(list)

                if (classDataList.isEmpty()) {
                    binding.NestedScrollView.visibility = View.GONE
                    binding.rvAttendance.visibility = View.GONE
                    binding.noDataLayout.visibility = View.VISIBLE
                    binding.btnSave.visibility = View.GONE
                } else {
                    binding.btnSave.visibility = View.VISIBLE
                    binding.NestedScrollView.visibility = View.VISIBLE
                    binding.rvAttendance.visibility = View.VISIBLE
                    binding.noDataLayout.visibility = View.GONE

                    attendanceTeacherAdapter =
                        AttendanceTeacherAdapter(
                            this@AttendancetActivity,
                            classDataList,
                            this@AttendancetActivity
                        )
                    binding.rvAttendance.adapter = attendanceTeacherAdapter
                }
            }

            override fun onFailure(call: Call<List<Attendance>>, t: Throwable) {
                dismissProgressDialog()
                showMessage("Failure: ${t.message}")
            }
        })
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


        val jsonArray = JsonArray()
        for (i in 0 until classDataList.size) {
            jsonArray.add(classDataList[i].student_session_id)
        }

        val jsonArrayNew = JsonArray()
        for (j in 0 until classDataList.size) {
            jsonArrayNew.add(classDataList[j].attendance)
        }

        // Build lists from classDataList
        val studentIdList = classDataList.mapNotNull { it.student_session_id } // List<String?>
        val studentAttendanceList = classDataList.map { it.attendance ?: "" } // keep empty if null

        // Build attendance date array (repeat date per student)
        val date = binding.tvDate.text.toString()
        val attendanceDateList = List(studentIdList.size) { date }

        // Convert lists to JSON strings (so they match cURL: '["a","b","c"]')
        val gson = Gson()
        val studentIdsJson = gson.toJson(studentIdList)
        val attendanceTypesJson = gson.toJson(studentAttendanceList)
        val attendanceDatesJson = gson.toJson(attendanceDateList)

        // Debug logs
        Log.e("MyLogData", "student_session_id => $studentIdsJson")
        Log.e("MyLogData", "attendence_type_id => $attendanceTypesJson")
        Log.e("MyLogData", "attendence_date => $attendanceDatesJson")
        Log.e("MyLogData", "teacher_id => ${prefs.getTeacherId().toString()}")
        val request = AttendanceRequest(
            student_session_id = studentIdList,
            attendance_type_id = studentAttendanceList,
            teacher_id = prefs.getTeacherId().toString(),
            attendance_date = attendanceDateList
        )

        val call = loginApi.SaveAttendanceAPI(
            "Bearer ${prefs.getAuthorizationToken()}",
            request
        )

        call.enqueue(object : Callback<SaveAttendance> {
            override fun onResponse(
                call: Call<SaveAttendance>,
                response: Response<SaveAttendance>,
            ) {
                if (response.isSuccessful) {
                    if (response.body()?.result?.isNotEmpty() == true) {

                        showMessage(response.body()?.result)
                        classDataList.clear()
                        binding.rvAttendance.adapter?.notifyDataSetChanged()
                        binding.btnSave.visibility = View.GONE
                        binding.editTextClass.setText("")
                        binding.editTextSection.setText("")
                        prefs.setClassTeacherClassName("")
                        prefs.setTeacherClassSectionName("")
                        binding.tvDate.text = ""
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
                showMessage("Something went wrong : ${t.message.toString()} \n onFailure")
            }
        })


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

    override fun onDestroy() {
        super.onDestroy()
        prefs.setClassTeacherClassName("")
        prefs.setTeacherClassSectionName("")
    }

    override fun onSelected(position: Int, newMarks: String) {
        Log.e("MyLogData ==> ", "=== $newMarks")
    }

}
package com.positron.teachers.activity

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import com.positron.teachers.adapters.AttendanceAdapter
import com.positron.teachers.R
import com.positron.teachers.api.ApiClass
import com.positron.teachers.databinding.ActivityAttendanceBinding
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
import java.util.*
import java.util.concurrent.TimeUnit

class AttendanceActivity : BaseActivity(), View.OnClickListener,
    AttendanceAdapter.BookingDetailsAdapterInterface {

    private lateinit var binding: ActivityAttendanceBinding
//Steel Carmel DGP
    private var attendanceDataList: MutableList<PunchData> = mutableListOf()
    private var attendanceDataListt: MutableList<InOutPunchData> = mutableListOf()
    private var attendanceAdapter: AttendanceAdapter? = null

    private val currentDate = Date()
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    private var formattedDate = dateFormat.format(currentDate)

    val calendar = Calendar.getInstance()
    val calendar2 = Calendar.getInstance()
    var yearNow = calendar.get(Calendar.YEAR)
    var monthNow = calendar.get(Calendar.MONTH)
    var dayNow = calendar.get(Calendar.DAY_OF_MONTH)

    var startDate =""
    var endDate = ""
    var isStartDate = false
    var cal = Calendar.getInstance()
    var date = ""
    @RequiresApi(Build.VERSION_CODES.O)
    val currentDatee = LocalDate.now()
    @RequiresApi(Build.VERSION_CODES.O)
    val firstDateOfMonth = getFirstDateOfCurrentMonthWithCurrentDate()
    @RequiresApi(Build.VERSION_CODES.O)
    val formattedCurrentDate = getFormattedDate(currentDatee)
    @RequiresApi(Build.VERSION_CODES.O)
    val formattedFirstDateOfMonth = getFormattedDate(firstDateOfMonth)

    var dayone  =  calendar.set(Calendar.DAY_OF_MONTH,1)

    fun getFirstDateOfCurrentMonth(): Calendar {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        return calendar
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getFormattedDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        return date.format(formatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getFirstDateOfCurrentMonthWithCurrentDate(): LocalDate {
        val currentDate = LocalDate.now()
        return currentDate.withDayOfMonth(1)
    }
    @RequiresApi(Build.VERSION_CODES.O)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener(this)
        binding.ivCalendar.setOnClickListener(this)
        binding.etStartDate.setOnClickListener(this)
        binding.etEndDate.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)
        if(isOnline()){
        //getPunchReport(formattedDate)
            getPunchReportInOut2(formattedFirstDateOfMonth,formattedDate)
        }
    }

    override fun onClick(v: View?) {
        val viewId = v?.id

        if (viewId == R.id.btnBack) {
            onBackPressed()
        } else if (viewId == R.id.iv_calendar) {
          //  openDatePicker()
        }else if (viewId == R.id.et_start_date) {
            isStartDate = true
            openDatePicker2()
        } else if (viewId == R.id.et_end_date) {
            isStartDate = false
            openDatePicker2()
        }else if(viewId == R.id.btn_submit){
            if (validate()){
                if(isOnline()) {
                    getPunchReportInOut()
                }
            }
        }
    }
    private fun validate() : Boolean{
        var check = true
        if (binding.etStartDate.text.isEmpty()) check = false
        if (binding.etEndDate.text.isEmpty()) check = false
        return check
    }

    private fun openDatePicker() {
        val dpd = DatePickerDialog(this, { view, year, monthOfYear, dayOfMonth ->
            calendar2.set(year, monthOfYear, dayOfMonth)
            formattedDate = dateFormat.format(calendar2.time)
          //  getPunchReport(formattedDate)
        }, yearNow, monthNow, dayNow)
        dpd.datePicker.maxDate = calendar.timeInMillis
        dpd.datePicker.updateDate(
            calendar2.get(Calendar.YEAR),
            calendar2.get(Calendar.MONTH),
            calendar2.get(Calendar.DAY_OF_MONTH)
        )
        dpd.show()
    }

    private fun openDatePicker2(){
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
                    endDate = tvdate
                }
                binding.etStartDate.setText(startDate)
                binding.etEndDate.setText(endDate)
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

    private fun getPunchReport(date: String) {
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

        val call = loginApi.getPunchReport(
            date = date,
            admission_no = prefs.getAdmissionNumber().toString(),
            id = "0488"
        )

        call.enqueue(object : Callback<ResponsePunch<List<PunchData>>> {
            override fun onResponse(
                call: Call<ResponsePunch<List<PunchData>>>,
                response: Response<ResponsePunch<List<PunchData>>>
            ) {
                if (response.isSuccessful) {

                   // attendanceDataList = response.body()?.PunchData as MutableList<PunchData>
                    /*Log.e("MyLogdata","==> " + attendanceDataList)
                    attendanceAdapter = AttendanceAdapter(
                        this@AttendanceActivity,
                        attendanceDataList,
                        this@AttendanceActivity
                    )
                    binding.rvAttendance.adapter = attendanceAdapter*/
                    Log.e("MyLogData","==>  if upar" + attendanceDataList.isEmpty())
                    if (attendanceDataList.isEmpty()){
                        Log.e("MyLogData","==> if " + attendanceDataList.isEmpty())

                        binding.rvAttendance.visibility = View.GONE
                        binding.noDataLayout.visibility = View.VISIBLE
                    } else {
                        Log.e("MyLogData","==> if no else" + attendanceDataList.isEmpty())
                        Log.e("MyLogData","==> if no else $attendanceDataList")
                        attendanceDataList = response.body()?.PunchData as MutableList<PunchData>
                        attendanceAdapter = AttendanceAdapter(
                            this@AttendanceActivity,
                            attendanceDataListt,
                            this@AttendanceActivity
                        )
                        binding.rvAttendance.adapter = attendanceAdapter
                        binding.rvAttendance.visibility = View.VISIBLE
                        binding.noDataLayout.visibility = View.GONE
                    }
                    dismissProgressDialog()
                } else {
                    dismissProgressDialog()
                    showMessage(" Something went wrong : ${response.code()} \n NULL")
                }
            }

            override fun onFailure(call: Call<ResponsePunch<List<PunchData>>>, t: Throwable) {
                dismissProgressDialog()
                Log.e("MyResponse", " failure registerApi Error ==> $t.message")
                showMessage("Something went wrong : ${t.message.toString()} \n onFailure")
            }
        })
    }

    private fun getPunchReportInOut() {
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

        val call = loginApi.getPunchReportInOut(
            date = startDate,
            datee = endDate,
            admission_no = prefs.getAdmissionNumber().toString(),
            id = "0488"
        )

        call.enqueue(object : Callback<ResponsePunchInOut<List<InOutPunchData>>> {
            override fun onResponse(
                call: Call<ResponsePunchInOut<List<InOutPunchData>>>,
                response: Response<ResponsePunchInOut<List<InOutPunchData>>>
            ) {
                if (response.isSuccessful) {
                try{
                    attendanceDataListt = response.body()?.InOutPunchData as MutableList<InOutPunchData>
                }catch (e:Exception){
                    e.printStackTrace()
                }
                    /*Log.e("MyLogdata","==> " + attendanceDataList)
                    attendanceAdapter = AttendanceAdapter(
                        this@AttendanceActivity,
                        attendanceDataList,
                        this@AttendanceActivity
                    )
                    binding.rvAttendance.adapter = attendanceAdapter*/

                    if (attendanceDataListt.isEmpty()){
                        //  Log.e("MyLogData","==> if " + attendanceDataListt.isEmpty())

                        binding.rvAttendance.visibility = View.GONE
                        binding.noDataLayout.visibility = View.VISIBLE
                    } else {

                        attendanceAdapter = AttendanceAdapter(
                            this@AttendanceActivity,
                            attendanceDataListt,
                            this@AttendanceActivity
                        )
                        binding.rvAttendance.adapter = attendanceAdapter
                        binding.rvAttendance.visibility = View.VISIBLE
                        binding.noDataLayout.visibility = View.GONE
                    }
                    dismissProgressDialog()
                } else {
                    dismissProgressDialog()
                    showMessage(" Something went wrong : ${response.code()} \n NULL")
                }
            }

            override fun onFailure(call: Call<ResponsePunchInOut<List<InOutPunchData>>>, t: Throwable) {
                dismissProgressDialog()
                //  Log.e("MyResponse", " failure registerApi Error ==> $t.message")
                showMessage("Something went wrong : ${t.message.toString()} \n onFailure")
            }
        })
    }

    private fun getPunchReportInOut2(date:String,datee: String) {
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

        val call = loginApi.getPunchReportInOut(
            date = date,
            datee = datee,
            admission_no = prefs.getAdmissionNumber().toString(),
            id = "0488"
        )

        call.enqueue(object : Callback<ResponsePunchInOut<List<InOutPunchData>>> {
            override fun onResponse(
                call: Call<ResponsePunchInOut<List<InOutPunchData>>>,
                response: Response<ResponsePunchInOut<List<InOutPunchData>>>
            ) {
                if (response.isSuccessful) {
                    try {
                        attendanceDataListt =
                            response.body()?.InOutPunchData as MutableList<InOutPunchData>
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                        /*Log.e("MyLogdata","==> " + attendanceDataList)
                    attendanceAdapter = AttendanceAdapter(
                        this@AttendanceActivity,
                        attendanceDataList,
                        this@AttendanceActivity
                    )
                    binding.rvAttendance.adapter = attendanceAdapter*/

                    if (attendanceDataListt.isEmpty()){
                        Log.e("MyLogData","==> if " + attendanceDataListt.isEmpty())

                        binding.rvAttendance.visibility = View.GONE
                        binding.noDataLayout.visibility = View.VISIBLE
                    } else {

                        attendanceAdapter = AttendanceAdapter(
                            this@AttendanceActivity,
                            attendanceDataListt,
                            this@AttendanceActivity
                        )
                        binding.rvAttendance.adapter = attendanceAdapter
                        binding.rvAttendance.visibility = View.VISIBLE
                        binding.noDataLayout.visibility = View.GONE
                    }
                    dismissProgressDialog()
                } else {
                    dismissProgressDialog()
                    showMessage(" Something went wrong : ${response.code()} \n NULL")
                }
            }

            override fun onFailure(call: Call<ResponsePunchInOut<List<InOutPunchData>>>, t: Throwable) {
                dismissProgressDialog()
                //  Log.e("MyResponse", " failure registerApi Error ==> $t.message")
                showMessage("Something went wrong : ${t.message.toString()} \n onFailure")
            }
        })
    }

    override fun onDocumentClick(documentUrl: String) {
        TODO("Not yet implemented")
    }

    override fun onSelected(item: NoticeData?) {
        TODO("Not yet implemented")
    }

}
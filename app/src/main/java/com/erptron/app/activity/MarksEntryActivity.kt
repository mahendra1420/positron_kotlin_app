package com.positron.teachers.activity


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.gson.JsonArray
import com.positron.teachers.adapters.MarksEntryAdapter
import com.positron.teachers.R
import com.positron.teachers.api.ApiClass
import com.positron.teachers.databinding.ActivityMarksEntryBinding
import com.positron.teachers.model.AttendanceCoRequest
import com.positron.teachers.model.ExamScheduleData
import com.positron.teachers.model.Grade
import com.positron.teachers.model.GradeNew
import com.positron.teachers.model.SaveAttendance
import com.positron.teachers.model.WithMark
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class MarksEntryActivity : BaseActivity(), View.OnClickListener,
    MarksEntryAdapter.BookingDetailsAdapterInterface {
    private lateinit var binding: ActivityMarksEntryBinding


    private var examScheduleDataList: MutableList<WithMark> = mutableListOf()
    private var examGradeNewList: MutableList<GradeNew> = mutableListOf()

    private var marksEntryAdapter: MarksEntryAdapter? = null

    private var grades: String? = null
    val jsonArrayGm = JsonArray()
    var isChecked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_marks_entry)
        binding = ActivityMarksEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // getGrades()
        //binding.rlAttendance.setOnClickListener(this)
        binding.btnBack.setOnClickListener(this)
        binding.btnSave.setOnClickListener(this)
        binding.btnSearch.setOnClickListener(this)
        binding.ivLogout.setOnClickListener(this)
        binding.editTextClass.setOnClickListener(this)
        binding.editTextSection.setOnClickListener(this)
        binding.editTextExamName.setOnClickListener(this)
        binding.editTextSubject.setOnClickListener(this)
        binding.editTextSubCategory.setOnClickListener(this)
        binding.radioButton.setOnClickListener {
            if (isChecked) {
                binding.radioButton.isChecked = false
                isChecked = false
                binding.editTextMarks.isEnabled = true
                binding.editTextMarks.setText("100")
            } else {
                binding.radioButton.isChecked = true
                isChecked = true
                binding.editTextMarks.isEnabled = false
                binding.editTextMarks.setText("0")
            }
        }
        /*setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.editTextMarks.isEnabled = false
            } else {
                Log.e("MylogData","")
                binding.editTextMarks.isEnabled = true
                binding.editTextMarks.setText("0")
            }
        }*/

        binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
            //item.isChecked = isChecked
            if (isChecked) {
                binding.editTextMarks.isEnabled = false
            } else {
                Log.e("MylogData", "")
                binding.editTextMarks.isEnabled = true
                binding.editTextMarks.setText("0")
            }
        }


    }

    override fun onClick(v: View?) {
        val viewId = v?.id
        if (viewId == R.id.btnBack) {
            onBackPressed()
        } else if (viewId == R.id.iv_logout) {
            //logOut()
            showLogoutConfirmationDialog()
        } else if (viewId == R.id.editTextClass) {
            binding.editTextSection.setText("Select")
            binding.editTextExamName.setText("Select")
            binding.editTextSubject.setText("Select")
            prefs.setSubjectTeacherSectionId("")
            prefs.setSubjectTeacherSectionName("")
            prefs.setExamId("")
            prefs.setExamName("")
            prefs.setSubjectName("")
            prefs.setSubjectId("")
            prefs.setSubSubjectCategoryName("")

            examScheduleDataList.clear()
            marksEntryAdapter?.notifyDataSetChanged()
            startActivity(
                Intent(
                    this@MarksEntryActivity,
                    DialogActivity::class.java
                ).putExtra("type", 3)
            )
        } else if (viewId == R.id.editTextSection) {
            if (binding.editTextClass.text.isNotEmpty()) {
                binding.editTextExamName.setText("Select")
                binding.editTextSubject.setText("Select")
                prefs.setExamId("")
                prefs.setExamName("")
                prefs.setSubjectName("")
                prefs.setSubjectId("")
                prefs.setSubSubjectCategoryName("")

                examScheduleDataList.clear()
                marksEntryAdapter?.notifyDataSetChanged()
                startActivity(
                    Intent(
                        this@MarksEntryActivity,
                        DialogActivity::class.java
                    ).putExtra("type", 4)
                )
            } else {
                showMessage("please select class")
            }
        } else if (viewId == R.id.editTextExamName) {
            if (binding.editTextSection.text.isNotEmpty()) {
                binding.editTextSubject.setText("Select")
                prefs.setSubjectName("")
                prefs.setSubjectId("")
                prefs.setSubSubjectCategoryName("")

                examScheduleDataList.clear()
                marksEntryAdapter?.notifyDataSetChanged()
                startActivity(
                    Intent(
                        this@MarksEntryActivity,
                        DialogActivity::class.java
                    ).putExtra("type", 5)
                )
            } else {
                showMessage("please select section")
            }
        }

        else if (viewId == R.id.editTextSubject) {
            if (binding.editTextExamName.text.isNotEmpty()) {
                examScheduleDataList.clear()
                prefs.setSubSubjectCategoryName("")
                marksEntryAdapter?.notifyDataSetChanged()
                startActivity(
                    Intent(
                        this@MarksEntryActivity,
                        DialogActivity::class.java
                    ).putExtra("type", 6)
                )
            } else {
                showMessage("please select exam name")
            }
        }
        else if (viewId == R.id.editTextSubCategory) {
            if (binding.editTextSubject.text.isNotEmpty()) {
                marksEntryAdapter?.notifyDataSetChanged()
                startActivity(
                    Intent(
                        this@MarksEntryActivity,
                        DialogActivity::class.java
                    ).putExtra("type", 9)
                )
            } else {
                showMessage("please select subject name")
            }
        }


        else if (viewId == R.id.btnSearch) {
            if (binding.editTextSubject.text.isNotEmpty()) {
                if (isOnline()) {
                    getExamScheduleData()
                }
            } else {
                showMessage("please select subject")
            }

        } else if (viewId == R.id.btnSave) {
            //if (binding.editTextSection.text.isNotEmpty()) {}
            if (isOnline()) {

                //   getmarks()
                if (examScheduleDataList[0].exam_array.dropdown == 1) {
                    getSaveMarksEntry("1")
                } else {
                    getSaveMarksEntry("")
                }

            }
            // else {
            //     showMessage("please select section")
            // }
        }
    }

    private fun getGrades() {
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

        val call = loginApi.getGrades()

        call.enqueue(object : Callback<Grade> {
            override fun onResponse(
                call: Call<Grade>,
                response: Response<Grade>,
            ) {
                if (response.isSuccessful) {
                    dismissProgressDialog()

                    grades = response.body().toString()

                    val jsonObject = JSONObject(response.body().toString())
                    val gradesObject = jsonObject.getJSONObject("grades")

                    // Create a new JSON object with the desired entries
                    val newGradesObject = JSONObject()
                    newGradesObject.put("0", gradesObject.getString("0"))
                    newGradesObject.put("95", gradesObject.getString("95"))
                    newGradesObject.put("85", gradesObject.getString("85"))

                    // Create the final JSON object
                    val newJsonObject = JSONObject()
                    newJsonObject.put("grades", newGradesObject)

                    // Convert the new JSON object to a string
                    val newJsonResponse = newJsonObject.toString()


                    //  Log.e("MyLogData","getGrades " + grades )

                } else {
                    dismissProgressDialog()
                    showMessage(" Something went wrong : ${response.code()} \n NULL")
                }
            }

            override fun onFailure(call: Call<Grade>, t: Throwable) {
                dismissProgressDialog()
                // Log.e("MyResponse", " failure registerApi Error ==> $t.message")
                showMessage("Something went wrong : ${t.message.toString()} \n 2")
            }
        })
    }


    private fun getExamScheduleData() {
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
        val selectedName = prefs.getSubSubjectCategoryName().toString()
        val mappedCategory = when (selectedName) {
            "Third Language" -> "third_language"
            "Second Language" -> "second_language"
            else -> selectedName.lowercase().replace(" ", "_")
        }
        val call = loginApi.getStudentsExam(
            "Bearer ${prefs.getAuthorizationToken().toString()}",
            prefs.getSubjectTeacherClassId().toString(),
            prefs.getSubjectTeacherSectionId().toString(),
            prefs.getSubjectId().toString(),
            prefs.getExamId().toString(),/*"1","1"*/
            mappedCategory ,/*"1","1"*/
        )
        Log.e("3333333333333333333", "3333333333333333"  )

        //val call = loginApi.getStudentsExam("5","1","28","31","1","1")

        /* Log.e("MyLogData","param  === " + prefs.getSubjectTeacherClassId().toString() +
             prefs.getSubjectTeacherSectionId().toString() + prefs.getSubjectId().toString()  +
             prefs.getExamId().toString())*/

        call.enqueue(object : Callback<ExamScheduleData> {
            override fun onResponse(
                call: Call<ExamScheduleData>,
                response: Response<ExamScheduleData>,
            ) {
                Log.e("11111111111", "1111111111" + response.body())
                if (response.isSuccessful) {
                    if (response.body()?.status == true) {
                        Log.e("2222222222222", "22222222222" + response.body())
                        examScheduleDataList.clear()
                        examGradeNewList.clear()
                        examScheduleDataList = response.body()?.with_marks as MutableList<WithMark>
                        examGradeNewList = response.body()?.marksgrade as MutableList<GradeNew>
                        Log.e("MyLogData", "if no upar" + response.body())
                        if (examScheduleDataList.isEmpty()) {
                            Log.e("MyLogData", "if")

                            binding.rvMarksEntry.visibility = View.GONE
                            binding.NestedScrollView.visibility = View.GONE
                            binding.noDataLayout.visibility = View.VISIBLE
                            binding.btnSave.visibility = View.GONE
                        } else {
                            binding.btnSave.visibility = View.VISIBLE
                            binding.NestedScrollView.visibility = View.VISIBLE
                            binding.rvMarksEntry.visibility = View.VISIBLE
                            Log.e("MyLogData", "else  " + examScheduleDataList)
                            binding.noDataLayout.visibility = View.GONE
                            marksEntryAdapter =
                                MarksEntryAdapter(
                                    this@MarksEntryActivity,
                                    examScheduleDataList,
                                    this@MarksEntryActivity,
                                    examGradeNewList
                                )
                            binding.rvMarksEntry.adapter = marksEntryAdapter
                        }
                        dismissProgressDialog()
                    } else {
                        showMessage("${response.body()?.message.toString()}")
                        binding.rvMarksEntry.visibility = View.GONE
                        binding.NestedScrollView.visibility = View.GONE
                        binding.noDataLayout.visibility = View.VISIBLE
                        binding.btnSave.visibility = View.GONE
                        dismissProgressDialog()
                    }
                } else {
                    dismissProgressDialog()
                    showMessage(" Something went wrong : ${response.code()} \n NULL")
                }
            }

            override fun onFailure(call: Call<ExamScheduleData>, t: Throwable) {
                dismissProgressDialog()
                Log.e("MyResponse", " failure registerApi Error ==> $t.message")
                showMessage("Something went wrong : ${t.message.toString()} \n 1")
            }
        }
        )
    }

    /*    private fun getmarks(){
            val jsonArrayGm = JsonArray()
            for (k in 0 until examScheduleDataList.size) {

                jsonArrayGm.add(examScheduleDataList[k].exam_array[0].get_marks)
                Log.e("MyLogData" , " jsonArrayGm ==> " + jsonArrayGm )
        }
        }*/

    private fun getSaveMarksEntry(grade: String) {
        Log.e("getSaveMarksEntry" , "==> $grade")
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

        val jsonArraySi = JsonArray()
        for (i in 0 until examScheduleDataList.size) {
            jsonArraySi.add(examScheduleDataList[i].student_id)
        }
        Log.e("MyLogData", " jsonArraySi  ==> $jsonArraySi")
        val jsonArrayGm = JsonArray()
        for (k in 0 until examScheduleDataList.size) {
            jsonArrayGm.add(examScheduleDataList[k].exam_array.get_marks)
        }
        Log.e("MyLogData", " jsonArrayGm  ==> $jsonArrayGm")
        val jsonArrayAt = JsonArray()
        for (l in 0 until examScheduleDataList.size) {
            jsonArrayAt.add(examScheduleDataList[l].exam_array.attendence)
        }
        Log.e("MyLogData", " jsonArrayAt  ==> $jsonArrayAt")
        val studentIdList: List<String> = jsonArraySi.map { it.asString }
        val get_marks: List<String> = jsonArrayGm.map { it.asString }
//      val get_marks: List<String> =jsonArrayGm.map {
//          if (it.isJsonNull) "" else it.asString
//      }
        val attendence: List<String> = jsonArrayAt.map { it.asString }
        val attendanceRequest = AttendanceCoRequest(
            prefs.getSubjectTeacherClassId().toString().toInt(),
            prefs.getSubjectTeacherSectionId().toString().toInt(),
            prefs.getExamId().toString().toInt(),
            prefs.getSubjectId().toString().toInt(),
            studentIdList,
            get_marks,
            attendence,
        )


        val call = loginApi.SaveMarksEntry(
            "Bearer ${prefs.getAuthorizationToken().toString()}", attendanceRequest
        )

        /*Log.e(
            "MyLogData", " param ==> " + prefs.getExamId().toString() +
                    prefs.getSubjectTeacherClassId().toString() +
                    prefs.getSubjectTeacherSectionId().toString() + prefs.getSubjectId()
                .toString() + prefs.getTeacherId()
                .toString() + studentIdList + get_marks + attendence
        )*/

        call.enqueue(object : Callback<SaveAttendance> {
            override fun onResponse(
                call: Call<SaveAttendance>,
                response: Response<SaveAttendance>,
            ) {
                if (response.isSuccessful) {
                    examScheduleDataList.clear()
                    binding.rvMarksEntry.adapter?.notifyDataSetChanged()
                    binding.btnSave.visibility = View.GONE
                    binding.editTextClass.setText("")
                    binding.editTextSection.setText("")
                    binding.editTextExamName.setText("")
                    binding.editTextSubject.setText("")
                    prefs.setSubjectTeacherClassName(null)
                    prefs.setSubjectTeacherSectionName(null)
                    prefs.setExamName(null)
                    prefs.setSubjectName(null)
                    prefs.setSubSubjectCategoryName(null)
                    showMessage("Record Saved")
                    dismissProgressDialog()
                } else {
                    dismissProgressDialog()
                    showMessage(" Something went wrong : ${response.code()}")
                }
            }

            override fun onFailure(call: Call<SaveAttendance>, t: Throwable) {
                dismissProgressDialog()
                Log.e("MyResponse", " failure registerApi Error ==> $t.message")
                showMessage("Something went wrong : ${t.message.toString()} \n 3")
            }
        })


    }

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
        startActivity(Intent(this@MarksEntryActivity, TeacherIdActivity::class.java))
        finish()
        dismissProgressDialog()
    }

    override fun onResume() {
        super.onResume()

        try {
            binding.editTextClass.setText(prefs.getSubjectTeacherClassName())
            binding.editTextSection.setText(prefs.getSubjectTeacherSectionName())
            binding.editTextExamName.setText(prefs.getExamName())
            binding.editTextSubject.setText(prefs.getSubjectName())

            if (prefs.getSubjectCategoryId() == "2") {

                binding.editTextSubCategory.visibility = View.VISIBLE
                binding.TextSubCategory.visibility = View.VISIBLE
                binding.editTextSubCategory.setText(prefs.getSubSubjectCategoryName())

            } else {
                binding.editTextSubCategory.visibility = View.GONE
                binding.TextSubCategory.visibility = View.GONE
            }
            //    Log.d("MyLogData", "ID ${prefs.getComplainCategoryId()}")
            //    Log.d("MyLogData", "SUB ID ${prefs.getComplainSubCategoryId()}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        prefs.setSubjectTeacherClassName(null)
        prefs.setSubjectTeacherSectionName(null)
        prefs.setExamName(null)
        prefs.setSubjectName(null)
        prefs.setSubSubjectCategoryName(null)

    }

    override fun onMarksUpdated(position: Int, marks: String) {

        //Log.e("asdfasdf", "==> ${marks}")

        //examScheduleDataList[0].exam_array[0].get_marks =marks

        //val jsonArrayGm = JsonArray()
        /* for (k in 0 until examScheduleDataList.size) {

             jsonArrayGm.add(examScheduleDataList[k].exam_array[0].get_marks == marks)

         }*/

        // val jsonArrayGm = JsonArray()
        /*for (k in 0 until examScheduleDataList.size) {
            Log.e("MyLogData" ,"examScheduleDataList " +examScheduleDataList.size )
            for (l in 0 until examScheduleDataList[k].exam_array.size){
            Log.e("MyLogData" ,"exam_array " +examScheduleDataList[k].exam_array.size )
            jsonArrayGm.add(examScheduleDataList[k].exam_array[l].get_marks ==marks)
                Log.e("MyLogData" ,"jsonArrayGm " +jsonArrayGm  )
        }
    }*/
    }

    override fun onAttUpdated(position: Int, att: String) {

    }
}
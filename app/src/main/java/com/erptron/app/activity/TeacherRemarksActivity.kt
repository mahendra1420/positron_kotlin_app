package com.positron.teachers.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog

import com.positron.teachers.adapters.TeacherRemarksAdapter
import com.positron.teachers.R
import com.positron.teachers.adapters.TeacherRemarksNewAdapter

import com.positron.teachers.api.ApiClass
import com.positron.teachers.databinding.ActivityTeacherRemarksBinding
import com.positron.teachers.model.GetStudentsPhotoList
import com.positron.teachers.model.GradeNew
import com.positron.teachers.model.SaveTeacherRemarks
import com.positron.teachers.model.SaveTeacherRemarksData
import com.positron.teachers.model.TeacherRemarks
import com.google.gson.JsonArray
import com.positron.teachers.model.ReMarkRequest
import com.positron.teachers.model.Remarks
import com.positron.teachers.model.SaveAttendance
import com.positron.teachers.model.TeacherRemarksRequest
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class TeacherRemarksActivity : BaseActivity(), View.OnClickListener, TeacherRemarksAdapter.BookingDetailsAdapterInterface,TeacherRemarksNewAdapter.BookingDetailsAdapterInterface {
    private lateinit var binding: ActivityTeacherRemarksBinding
    private var teacherRemarksDataList: MutableList<TeacherRemarks> = mutableListOf()
    private var teacherRemarksList: MutableList<TeacherRemarks> = mutableListOf()
    private var remarksList: MutableList<Remarks> = mutableListOf()
    private var teacherRemarksAdapter: TeacherRemarksAdapter? = null
    private var teacherRemarksNewAdapter: TeacherRemarksNewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_teacher_remarks)
        binding = ActivityTeacherRemarksBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //binding.rlAttendance.setOnClickListener(this)
        binding.btnBack.setOnClickListener(this)
        binding.btnSearch.setOnClickListener(this)
        binding.btnSave.setOnClickListener(this)
        binding.ivLogout.setOnClickListener(this)
        binding.editTextClass.setOnClickListener(this)
        binding.editTextSection.setOnClickListener(this)
        binding.editTextExamName.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val viewId = v?.id
        if (viewId == R.id.btnBack) {
            onBackPressed()
        }else if (viewId == R.id.iv_logout) {
            //logOut()
            showLogoutConfirmationDialog()
        }else if (viewId == R.id.editTextClass) {
            binding.editTextSection.setText("Select")
            binding.editTextExamName.setText("Select")
            prefs.setTeacherClassSectionName("")
            prefs.setTeacherClassSectionId("")
            prefs.setExamName("")
            prefs.setExamId("")
            prefs.setFormatName("")
            prefs.setFormatId("")
            teacherRemarksDataList.clear()
            teacherRemarksAdapter?.notifyDataSetChanged()
            startActivity(
                Intent(
                    this@TeacherRemarksActivity,
                    DialogActivity::class.java
                ).putExtra("type", 1)
            )
        }
        else if (viewId == R.id.editTextSection) {
            if (binding.editTextClass.text.isNotEmpty()) {
                binding.editTextExamName.setText("Select")
                prefs.setTeacherClassSectionName("")
                prefs.setTeacherClassSectionId("")
                prefs.setExamName("")
                prefs.setExamId("")
                prefs.setFormatName("")
                prefs.setFormatId("")
                teacherRemarksDataList.clear()
                teacherRemarksAdapter?.notifyDataSetChanged()
                startActivity(
                    Intent(
                        this@TeacherRemarksActivity,
                        DialogActivity::class.java
                    ).putExtra("type", 2)
                )
            } else {
                showMessage("please select class")
            }
        }
        else if (viewId == R.id.editTextExamName) {
            if (binding.editTextSection.text.isNotEmpty()) {
                teacherRemarksDataList.clear()
                teacherRemarksAdapter?.notifyDataSetChanged()
                startActivity(
                    Intent(
                        this@TeacherRemarksActivity,
                        DialogActivity::class.java
                    ).putExtra("type", 8)
                )
            } else {
                showMessage("please select section")
            }
        }

        else if(viewId == R.id.btnSearch){
            if (binding.editTextExamName.text.isNotEmpty()) {


                if(isOnline()){
                    //getTeacherRemarks()
                    getStudentForRemarks()
                }
            }
            else {
                showMessage("please select Exam")
            }
        }
        else if(viewId == R.id.btnSave){

            if(isOnline()) {
               // Log.e("MyLogData" , "saveTeacherRemarks  param ==> " + prefs.getClassTeacherClassId().toString() +   " "+ prefs.getTeacherClassSectionId().toString())
                       // saveTeacherRemarks()
                saveTeacherRemarksNew()
            }


        }

    }

    private fun getTeacherRemarks() {
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

        val call = loginApi.getTeacherRemarks("Bearer ${prefs.getAuthorizationToken().toString()}"/*prefs.getTeacherId().toString(),prefs.getExamId().toString(),
            prefs.getClassTeacherClassId().toString(),prefs.getTeacherClassSectionId().toString()*/)

        /*Log.e("MyLogData" , "getTeacherRemarks  param ==> " +  prefs.getTeacherId().toString() +
            prefs.getExamId().toString() +
            prefs.getClassTeacherClassId().toString() + prefs.getTeacherClassSectionId().toString())*/
        call.enqueue(object : Callback<List<TeacherRemarks>> {
            override fun onResponse(
                call: Call<List<TeacherRemarks>>,
                response: Response<List<TeacherRemarks>>
            ) {
                if (response.isSuccessful) {

                    teacherRemarksDataList.clear()
                    teacherRemarksDataList = response.body() as MutableList<TeacherRemarks>

                    if (teacherRemarksDataList.isEmpty()) {
                        Log.e("MyLogData","if")
                        binding.rvTeacherRemarks.visibility = View.GONE
                        binding.NestedScrollView.visibility = View.GONE
                        binding.noDataLayout.visibility = View.VISIBLE
                        binding.btnSave.visibility = View.GONE
                    } else {
                        binding.btnSave.visibility = View.VISIBLE
                        binding.NestedScrollView.visibility = View.VISIBLE
                        binding.rvTeacherRemarks.visibility = View.VISIBLE
                        binding.noDataLayout.visibility = View.GONE
                        teacherRemarksAdapter =
                            TeacherRemarksAdapter(this@TeacherRemarksActivity, teacherRemarksDataList,this@TeacherRemarksActivity)
                        binding.rvTeacherRemarks.adapter = teacherRemarksAdapter
                    }
                    dismissProgressDialog()
                } else {
                    dismissProgressDialog()
                    showMessage("Something went wrong : ${response.code()} \n NULL")
                }
            }

            override fun onFailure(call: Call<List<TeacherRemarks>>, t: Throwable) {
                dismissProgressDialog()
                Log.e("MyResponse", " failure registerApi Error ==> $t.message")
                showMessage("Something went wrong : ${t.message.toString()} \n onFailure")
            }
        })
    }

    private fun getStudentForRemarks() {
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
        val call = loginApi.getTeacherRemarksStudents(
            prefs.getTeacherId().toString(),
            prefs.getFormatId().toString(),
            prefs.getClassTeacherClassId().toString(),
            prefs.getTeacherClassSectionId().toString()
        )

        call.enqueue(object : Callback<List<TeacherRemarks>> {
            override fun onResponse(
                call: Call<List<TeacherRemarks>>,
                response: Response<List<TeacherRemarks>>
            ) {
                dismissProgressDialog()

                if (response.isSuccessful && response.body() != null) {
                    val students = response.body() ?: emptyList()
                    teacherRemarksList.clear()
                    remarksList.clear()

                    if (students.isEmpty()) {
                        binding.rvTeacherRemarks.visibility = View.GONE
                        binding.NestedScrollView.visibility = View.GONE
                        binding.noDataLayout.visibility = View.VISIBLE
                        binding.btnSave.visibility = View.GONE
                    } else {
                        teacherRemarksList = students.toMutableList()
                        remarksList = mutableListOf()

                        binding.btnSave.visibility = View.VISIBLE
                        binding.NestedScrollView.visibility = View.VISIBLE
                        binding.rvTeacherRemarks.visibility = View.VISIBLE
                        binding.noDataLayout.visibility = View.GONE

                        teacherRemarksNewAdapter = TeacherRemarksNewAdapter(
                            this@TeacherRemarksActivity,
                            teacherRemarksList,
                            this@TeacherRemarksActivity,
                            remarksList
                        )
                        binding.rvTeacherRemarks.adapter = teacherRemarksNewAdapter
                    }
                } else {
                    showMessage("Something went wrong : ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<TeacherRemarks>>, t: Throwable) {
                dismissProgressDialog()
                Log.e("MyResponse", " failure registerApi Error ==> $t.message")
                showMessage("Something went wrong : ${t.message}")
            }
        })
    }

    private fun saveTeacherRemarksNew() {
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
        for (i in 0 until teacherRemarksList.size) {
            val studentId = teacherRemarksList[i].student_id ?: teacherRemarksList[i].id?.toString() ?: ""
            jsonArraySi.add(studentId)
        }



        val jsonArrayDp = JsonArray()
        for (l in 0 until teacherRemarksList.size) {

            jsonArrayDp.add(teacherRemarksList[l].remarks_id)
        }

       /* val jsonArrayRm = JsonArray()
        for (m in 0 until teacherRemarksList.size) {

            jsonArrayRm.add(teacherRemarksList[m].remarks)
        }*/


        val jsonArrayRm = JsonArray()
        teacherRemarksList.forEach { it.remarks?.let { remark -> jsonArrayRm.add(remark) } }

        val remarks: List<String> = jsonArrayRm.mapNotNull {
            if (it.isJsonNull) null else it.asString
        }

        val studentIdList: List<String> = jsonArraySi.map { it.asString }
        val remarksid: List<String> = jsonArrayDp.map { it.asString }
       // val remarks: List<String> = jsonArrayRm.map { it.asString }
        val teacherRemarksRequest = TeacherRemarksRequest(
            prefs.getClassTeacherClassId()!!.toInt(),
            prefs.getTeacherClassSectionId()!!.toInt(),
            prefs.getFormatId()!!.toInt(),
            studentIdList,
            remarksid,
            remarks,
        )


        val call = loginApi.saveStudentRemarks(
            "Bearer ${prefs.getAuthorizationToken().toString()}",teacherRemarksRequest
        )



        call.enqueue(object : Callback<SaveAttendance> {
            override fun onResponse(
                call: Call<SaveAttendance>,
                response: Response<SaveAttendance>
            ) {
                if (response.isSuccessful) {
                    //Log.e("MyLogData","if no upar" + response.body())

                    // Log.e("MyLogData","if no upar" + response.body()!!.result)
                    if(response.body()?.message.isNullOrEmpty()){
                        dismissProgressDialog()
                        showMessage("Record Not Saved")
                    }else{
                        dismissProgressDialog()


                        showMessage(response.body()?.message)
                        teacherRemarksList.clear()
                        binding.rvTeacherRemarks.adapter?.notifyDataSetChanged()
                        binding.btnSave.visibility =View.GONE
                        binding.editTextClass.setText("")
                        binding.editTextSection.setText("")
                        binding.editTextExamName.setText("")
                        prefs.setClassTeacherClassName("")
                        prefs.setTeacherClassSectionName("")
                        prefs.setExamName("")
                    }
                } else {
                    dismissProgressDialog()
                    showMessage(" Something went wrong : ${response.code()} \n NULL")
                }
            }

            override fun onFailure(call: Call<SaveAttendance>, t: Throwable) {
                dismissProgressDialog()
                // Log.e("MyResponse", " failure registerApi Error ==> $t.message")
                showMessage("Something went wrong : ${t.message.toString()} \n onFailure")
            }
        })




    }

    private fun saveTeacherRemarks() {
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
        for (i in 0 until teacherRemarksDataList.size) {
            jsonArraySi.add(teacherRemarksDataList[i].student_id)
        }

        val jsonArraySsi = JsonArray()
        for (j in 0 until teacherRemarksDataList.size) {

            jsonArraySsi.add(teacherRemarksDataList[j].student_session_id)
        }


        val jsonArrayWd = JsonArray()
        for (k in 0 until teacherRemarksDataList.size) {

            jsonArrayWd.add(teacherRemarksDataList[k].working_days)
        }

        val jsonArrayDp = JsonArray()
        for (l in 0 until teacherRemarksDataList.size) {

            jsonArrayDp.add(teacherRemarksDataList[l].days_present)
        }

        val jsonArrayRm = JsonArray()
        for (m in 0 until teacherRemarksDataList.size) {

            jsonArrayRm.add(teacherRemarksDataList[m].remarks)
        }

        //Log.e("MyLogData" , " jsonArraySi ==> " + jsonArraySi )
       // Log.e("MyLogData" , " jsonArraySsi ==> " + jsonArraySsi )
       // Log.e("MyLogData" , " jsonArrayWd ==> " + jsonArrayWd )
       // Log.e("MyLogData" , " jsonArrayDp ==> " + jsonArrayDp )
       // Log.e("MyLogData" , " jsonArrayRm ==> " + jsonArrayRm )

        val studentIdList: List<String> = jsonArraySi.map { it.asString }
        val student_session_id: List<String> = jsonArraySsi.map { it.asString }
        val working_days: List<String> = jsonArrayWd.map { it.asString }
        val days_present: List<String> = jsonArrayDp.map { it.asString }
        val remarks: List<String> = jsonArrayRm.map { it.asString }
        val reMarkRequest = ReMarkRequest(
            prefs.getTeacherId().toString(),
            prefs.getExamId().toString(),
            prefs.getSubjectTeacherClassId().toString(),
            prefs.getSubjectTeacherSectionId().toString(),
            student_session_id,
            working_days,
            days_present,
            studentIdList,
            remarks,
        )


        val call = loginApi.saveTeacherRemarks(
            prefs.getAuthorizationToken().toString(),reMarkRequest
        )

        /*Log.e("MyLogData" , "saveTeacherRemarks  param ==> " +  prefs.getTeacherId().toString() +
            prefs.getExamId().toString() + prefs.getSubjectTeacherClassId().toString() +
            prefs.getSubjectTeacherSectionId().toString())*/

        call.enqueue(object : Callback<SaveTeacherRemarks<List<SaveTeacherRemarksData>>> {
            override fun onResponse(
                call: Call<SaveTeacherRemarks<List<SaveTeacherRemarksData>>>,
                response: Response<SaveTeacherRemarks<List<SaveTeacherRemarksData>>>
            ) {
                if (response.isSuccessful) {
                    //Log.e("MyLogData","if no upar" + response.body())

                   // Log.e("MyLogData","if no upar" + response.body()!!.result)
                    if(response.body()?.result?.isNotEmpty() == true){
                        dismissProgressDialog()
                        showMessage(response.body()?.result)
                        teacherRemarksDataList.clear()
                        binding.rvTeacherRemarks.adapter?.notifyDataSetChanged()
                        binding.btnSave.visibility =View.GONE
                        binding.editTextClass.setText("")
                        binding.editTextSection.setText("")
                        binding.editTextExamName.setText("")
                        prefs.setClassTeacherClassName("")
                        prefs.setTeacherClassSectionName("")
                        prefs.setExamName("")
                    }else{
                        dismissProgressDialog()
                        showMessage("Record Not Saved")
                    }
                } else {
                    dismissProgressDialog()
                    showMessage(" Something went wrong : ${response.code()} \n NULL")
                }
            }

            override fun onFailure(call: Call<SaveTeacherRemarks<List<SaveTeacherRemarksData>>>, t: Throwable) {
                dismissProgressDialog()
               // Log.e("MyResponse", " failure registerApi Error ==> $t.message")
                showMessage("Something went wrong : ${t.message.toString()} \n onFailure")
            }
        })




    }

    override fun onResume() {
        super.onResume()

        try {
            binding.editTextClass.setText(prefs.getClassTeacherClassName())
            binding.editTextSection.setText(prefs.getTeacherClassSectionName())
            binding.editTextExamName.setText(prefs.getFormatName())

            //    Log.d("MyLogData", "ID ${prefs.getComplainCategoryId()}")
            //    Log.d("MyLogData", "SUB ID ${prefs.getComplainSubCategoryId()}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        prefs.setClassTeacherClassName("")
        prefs.setTeacherClassSectionName("")
        prefs.setExamName("")
        prefs.setFormatName("")
        prefs.setFormatId("")

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
        startActivity(Intent(this@TeacherRemarksActivity, TeacherIdActivity::class.java))
        finish()
        dismissProgressDialog()
    }

    override fun onSelected(position: Int, remarks: String, day: String) {

    }

    override fun onSelectednew(position: Int, remarks: String, day: String) {

    }


}
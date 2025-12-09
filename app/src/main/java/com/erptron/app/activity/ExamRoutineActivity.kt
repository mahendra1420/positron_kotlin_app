package com.positron.teachers.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.positron.teachers.adapters.ExamRoutineAdapter

import com.positron.teachers.R

import com.positron.teachers.api.ApiClass
import com.positron.teachers.databinding.ActivityExamRoutineBinding
import com.positron.teachers.model.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ExamRoutineActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding:ActivityExamRoutineBinding
    private var examRoutineList: MutableList<ExamRoutine> = mutableListOf()
    private var examRoutineAdapter: ExamRoutineAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityExamRoutineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener(this)

        binding.ivLogout.setOnClickListener(this)
        binding.btnSearch.setOnClickListener(this)
        binding.editTextClass.setOnClickListener(this)
        binding.editTextSection.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val viewId = v?.id
        if (viewId == R.id.btnBack) {
            onBackPressed()
        }else if (viewId == R.id.iv_logout) {
            //logOut()
            showLogoutConfirmationDialog()
        }
        else if (viewId == R.id.editTextClass) {
            binding.editTextSection.setText("Select")
            prefs.setTeacherClassSectionName("")
            prefs.setTeacherClassSectionId("")
            examRoutineList.clear()
            examRoutineAdapter?.notifyDataSetChanged()
            startActivity(
                Intent(
                    this@ExamRoutineActivity,
                    DialogActivity::class.java
                ).putExtra("type", 1)
            )
        }
        else if (viewId == R.id.editTextSection) {
            if (binding.editTextClass.text.isNotEmpty()) {
                examRoutineList.clear()
                examRoutineAdapter?.notifyDataSetChanged()
                startActivity(
                    Intent(
                        this@ExamRoutineActivity,
                        DialogActivity::class.java
                    ).putExtra("type", 2)
                )
            } else {
                showMessage("please select class")
            }
        }
        else if(viewId == R.id.btnSearch){
            if (binding.editTextSection.text.isNotEmpty()) {
                if(isOnline()){
                    getExamRoutine()
                }}
            else {
                showMessage("please select section")
            }
        }
    }

    private fun getExamRoutine() {
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

        val call = loginApi.getExamRoutine(prefs.getTeacherId().toString(),prefs.getClassTeacherClassId().toString(),prefs.getTeacherClassSectionId().toString())

        call.enqueue(object : Callback<ExamRoutineObject<List<ExamRoutine>>> {
            override fun onResponse(
                call: Call<ExamRoutineObject<List<ExamRoutine>>>,
                response: Response<ExamRoutineObject<List<ExamRoutine>>>
            ) {
                if (response.isSuccessful) {
                    dismissProgressDialog()
                    examRoutineList.clear()
                    examRoutineList = response.body()?.exam_routine as MutableList<ExamRoutine>

                    if (examRoutineList.isEmpty()) {

                        binding.rvExamRoutine.visibility = View.GONE
                        binding.noDataLayout.visibility = View.VISIBLE

                    }else{

                        binding.rvExamRoutine.visibility = View.VISIBLE
                        binding.noDataLayout.visibility = View.GONE
                        examRoutineAdapter =
                            ExamRoutineAdapter(this@ExamRoutineActivity, examRoutineList)
                        binding.rvExamRoutine.adapter = examRoutineAdapter
                    }


                } else {
                    dismissProgressDialog()
                    showMessage(" Something went wrong : ${response.code()} \n NULL")
                }
            }

            override fun onFailure(call: Call<ExamRoutineObject<List<ExamRoutine>>>, t: Throwable) {
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
        startActivity(Intent(this@ExamRoutineActivity, TeacherIdActivity::class.java))
        finish()
        dismissProgressDialog()
    }
}
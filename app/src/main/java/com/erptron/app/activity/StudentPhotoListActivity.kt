package com.positron.teachers.activity


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.positron.teachers.adapters.StudentPhotoAdapter
import com.positron.teachers.R
import com.positron.teachers.api.ApiClass
import com.positron.teachers.databinding.ActivityStudentPhotoListBinding
import com.positron.teachers.model.GetStudentsPhotoList
import com.positron.teachers.model.GetStudentsPhotoListMain
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class StudentPhotoListActivity : BaseActivity(), View.OnClickListener,
    StudentPhotoAdapter.StudentDetailsAdapterInterface {

    private lateinit var binding: ActivityStudentPhotoListBinding
    private var studentPhotoAdapter: StudentPhotoAdapter? = null
    private var classDataList: MutableList<GetStudentsPhotoList> = mutableListOf()
    private var recentlyUpdatedStudentId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_student_photo_list)
        binding = ActivityStudentPhotoListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnBack.setOnClickListener(this)
        binding.btnSave.setOnClickListener(this)
        binding.btnSearch.setOnClickListener(this)
        binding.ivLogout.setOnClickListener(this)
        binding.editTextClass.setOnClickListener(this)
        binding.editTextSection.setOnClickListener(this)
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
            prefs.setTeacherClassSectionName("")
            prefs.setTeacherClassSectionId("")
            classDataList.clear()
            studentPhotoAdapter?.notifyDataSetChanged()
            startActivity(
                Intent(
                    this@StudentPhotoListActivity,
                    DialogActivity::class.java
                ).putExtra("type", 1)
            )
        } else if (viewId == R.id.editTextSection) {
            if (binding.editTextClass.text.isNotEmpty()) {
                classDataList.clear()
                studentPhotoAdapter?.notifyDataSetChanged()
                startActivity(
                    Intent(
                        this@StudentPhotoListActivity,
                        DialogActivity::class.java
                    ).putExtra("type", 2)
                )
            } else {
                showMessage("please select class")
            }
        } else if (viewId == R.id.btnSearch) {
            if (binding.editTextSection.text.isNotEmpty()) {
                if (isOnline()) {
                    // Log.e("MyLogData" , "getStudentsAttendance===  " + binding.tvDate.text.toString())
                    getStudentListPhotoSession()
                }
            } else {
                showMessage("please select section")
            }
        }

    }

    private fun getStudentListPhotoSession() {
        showProgressDialog()

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
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

        val call = loginApi.getStudentListPhotoSession(
            "Bearer ${
                prefs.getAuthorizationToken().toString()
            }",
            prefs.getClassTeacherClassId().toString(),
            prefs.getTeacherClassSectionId().toString()
        )

        call.enqueue(object : Callback<GetStudentsPhotoListMain> {
            override fun onResponse(
                call: Call<GetStudentsPhotoListMain>,
                response: Response<GetStudentsPhotoListMain>,
            ) {

                if (response.isSuccessful) {
                    if (response.body()?.status == true) {
                        classDataList.clear()
                        classDataList =
                            response.body()?.data as MutableList<GetStudentsPhotoList>
                        binding.NestedScrollView.visibility = View.VISIBLE
                        binding.rvAttendance.visibility = View.VISIBLE
                        binding.noDataLayout.visibility = View.GONE
                        studentPhotoAdapter =
                            StudentPhotoAdapter(
                                this@StudentPhotoListActivity,
                                classDataList,
                                this@StudentPhotoListActivity,


                            )
                        binding.rvAttendance.adapter = studentPhotoAdapter
                        studentPhotoAdapter?.notifyDataSetChanged()
                        dismissProgressDialog()
                    } else {
                        binding.NestedScrollView.visibility = View.GONE
                        binding.rvAttendance.visibility = View.GONE
                        binding.noDataLayout.visibility = View.VISIBLE
                        binding.btnSave.visibility = View.GONE
                        dismissProgressDialog()
                    }

                    /*if (classDataList.isEmpty()) {
                        binding.NestedScrollView.visibility = View.GONE
                        binding.rvAttendance.visibility = View.GONE
                        binding.noDataLayout.visibility = View.VISIBLE
                        binding.btnSave.visibility = View.GONE
                    } else {
                        binding.NestedScrollView.visibility = View.VISIBLE
                        binding.rvAttendance.visibility = View.VISIBLE
                        binding.noDataLayout.visibility = View.GONE
                        studentPhotoAdapter =
                            StudentPhotoAdapter(this@StudentPhotoListActivity, classDataList,this@StudentPhotoListActivity)
                        binding.rvAttendance.adapter = studentPhotoAdapter

                    }*/

                } else {
                    dismissProgressDialog()
                    showMessage("Something went wrong : ${response.code()} \n NULL")
                }
            }

            override fun onFailure(call: Call<GetStudentsPhotoListMain>, t: Throwable) {
                dismissProgressDialog()
                //  Log.e("MyResponse", " failure registerApi Error ==> $t.message")
                showMessage("Something went wrong : ${t.message.toString()} \n onFailure")
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
        startActivity(Intent(this@StudentPhotoListActivity, TeacherIdActivity::class.java))
        finish()
        dismissProgressDialog()
    }

    override fun onResume() {
        super.onResume()
        try {
            binding.editTextClass.setText(prefs.getClassTeacherClassName())
            binding.editTextSection.setText(prefs.getTeacherClassSectionName())
            prefs.setUpdatedStudentId("") // Clear it after use
            if (binding.editTextClass.text.isNotEmpty() && binding.editTextSection.text.isNotEmpty()) {
                getStudentListPhotoSession()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        prefs.setSubjectTeacherClassName(null)
        prefs.setSubjectTeacherSectionName(null)
        prefs.setClassTeacherClassName("")
        prefs.setTeacherClassSectionName("")
        prefs.setClassTeacherClassId("")
        prefs.setTeacherClassSectionId("")


    }

    override fun onBackPressed() {

        prefs.setClassTeacherClassName("")
        prefs.setTeacherClassSectionName("")

        prefs.setClassTeacherClassId("")
        prefs.setTeacherClassSectionId("")

        super.onBackPressed()
    }

    override fun onSelected(position: Int, item: GetStudentsPhotoList?) {
        startActivity(
            Intent(
                this@StudentPhotoListActivity,
                UploadStudentPhotoActivity::class.java
            ).putExtra("NoticeData", item)
        )

    }
}
package com.positron.teachers.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.positron.teachers.R
import com.positron.teachers.api.ApiClass
import com.positron.teachers.databinding.ActivityRegistersteptwoBinding
import com.positron.teachers.model.RegisterData
import com.positron.teachers.model.ResultListNew
import com.positron.teachers.model.StudentsInfo
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class REgistersteptwoActivity :  BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityRegistersteptwoBinding
    //Steel Carmel DGP

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistersteptwoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnRegister.setOnClickListener(this)
        if(isOnline()){
        getDataFromAdmissionNumber(prefs.getAdmissionNumber().toString())}
    }

    override fun onClick(v: View?) {
        val viewId = v?.id
        if (viewId == R.id.btnRegister) {
            if(isOnline()){
            registerApi(prefs.getFcmToken().toString())}
            //startActivity(Intent(this@REgistersteptwoActivity, HomePageActivity::class.java))
            //intent.putExtra("message_key", message)
            //finish()
           /* if (validation()) {
                generateFcmToken()
            } else {
                Toast.makeText(this, "PLease fill all fields", Toast.LENGTH_SHORT).show()
            }*/
        }
    }
    private fun validation(): Boolean {
        var check = true
        if (binding.editTextAdmissionNumber.text.isEmpty()) check = false
        if (binding.editTextStudentName.text.isEmpty()) check = false
//        if (binding.editTextGuardiansName.text.isEmpty()) check = false
//        if (binding.editTextClass.text.isEmpty()) check = false
//        if (binding.editTextSection.text.isEmpty()) check = false
//        if (binding.editTextRollNo.text.isEmpty()) check = false
//        if (binding.editTextMobileNo.text.isEmpty()) check = false
        return check
    }

    private fun getDataFromAdmissionNumber(admissionNumber: String) {
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

        val call = loginApi.getDataFromAdmissionNumber(admissionNumber)

        call.enqueue(object : Callback<ResultListNew<StudentsInfo>> {
            override fun onResponse(
                call: Call<ResultListNew<StudentsInfo>>,
                response: Response<ResultListNew<StudentsInfo>>
            ) {
                if (response.isSuccessful) {
                    prefs.setAdmissionNumber(admissionNumber)
                    binding.otherDetailsLayout.visibility = View.VISIBLE
                    binding.editTextAdmissionNumber.setText(prefs.getAdmissionNumber().toString())
                    binding.editTextStudentName.text =
                        response.body()?.student_info?.student_name_uni.toString()
                    binding.editTextGuardiansName.text =
                        response.body()?.student_info?.guardian_name.toString()
                    binding.editTextClass.text = response.body()?.student_info?.class_id.toString()
                    binding.editTextSection.text = response.body()?.student_info?.section.toString()
                    binding.editTextRollNo.text = response.body()?.student_info?.roll_no.toString()
                    Log.e("MyLogData", ""+response.body()?.student_info?.mobileno)
                    val outputString = response.body()?.student_info?.mobileno?.substring(0, 3) + "****" + response.body()?.student_info?.mobileno?.substring(7)
                    Log.e("MyLogData", ""+outputString)
                    binding.editTextMobileNo.text = outputString.toString()
                    if(response.body()?.student_info?.image.toString().isNullOrEmpty()){
                        Log.e("MyLogData", "image  if" + response.body()?.student_info?.image.toString())
                        prefs.setImage(response.body()?.student_info?.image.toString())
                    }else{
                        prefs.setImage(response.body()?.student_info?.image.toString())
                        Log.e("MyLogData", "image else" + response.body()?.student_info?.image.toString())
                    }
                    dismissProgressDialog()
                } else {
                    dismissProgressDialog()
                    showMessage("Something went wrong : ${response.code()} \n NULL")
                }
            }

            override fun onFailure(call: Call<ResultListNew<StudentsInfo>>, t: Throwable) {
                dismissProgressDialog()
                Log.e("MyResponse", " failure getTransactionId Error 123 ==> ${t.message}")
                showMessage("Something went wrong : ${t.message.toString()} \n onFailure")
            }
        })
    }

    private fun registerApi(token: String) {
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

        val call = loginApi.registerApi(
            admission_no = binding.editTextAdmissionNumber.text.toString(),
            student_name = binding.editTextStudentName.text.toString(),
            app_code = "0488",
            token = token
        )

        call.enqueue(object : Callback<RegisterData> {
            override fun onResponse(
                call: Call<RegisterData>,
                response: Response<RegisterData>
            ) {
                if (response.isSuccessful) {
                    prefs.setInsertId(response.body()?.insert_id.toString())
                    prefs.setLoggedIn(true)
                    dismissProgressDialog()
                     startActivity(Intent(this@REgistersteptwoActivity, HomePageActivity::class.java))
                   // startActivity(Intent(this@REgistersteptwoActivity, OtpVerifyActivity::class.java))
                    //intent.putExtra("message_key", message)
                    finish()
                } else {
                    dismissProgressDialog()
                    showMessage(" Something went wrong : ${response.code()} \n NULL")
                }
            }

            override fun onFailure(call: Call<RegisterData>, t: Throwable) {
                dismissProgressDialog()
                Log.e("MyResponse", " failure registerApi Error ==> $t.message")
                showMessage("Something went wrong : ${t.message.toString()} \n onFailure")
            }
        })
    }
}
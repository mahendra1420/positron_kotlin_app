package com.positron.teachers.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.positron.teachers.R
import com.positron.teachers.api.ApiClass
import com.positron.teachers.databinding.ActivityRegisterBinding
import com.positron.teachers.firebase_services.MyFirebaseMessagingService
import com.positron.teachers.model.RegisterData
import com.positron.teachers.model.ResultListNew
import com.positron.teachers.model.StudentsInfo
import com.positron.teachers.model.VerifyRegOtp
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RegisterActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityRegisterBinding
    val message = "Steel Carmel DGP"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener(this)
        binding.btnSumbit.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val viewId = v?.id

        if (viewId == R.id.btnRegister) {
            if (validation()) {
                if(isOnline()){
                generateFcmToken()}
            } else {
                Toast.makeText(this, "PLease fill all fields", Toast.LENGTH_SHORT).show()
            }
        } else if (viewId == R.id.btnSumbit) {
            if (binding.editTextAdmissionNumber.text.isNotEmpty()) {
                if(isOnline()){
                getDataFromAdmissionNumber(binding.editTextAdmissionNumber.text.toString())
                }
                generateFcmToken()
                prefs.setAdmissionNumber(binding.editTextAdmissionNumber.text.toString())
                //showMessage("An OTP has been sent to your registered email id , please enter that and verify")
                //startActivity(Intent(this@RegisterActivity, OtpVerifyActivity::class.java))
               // finish()
            }
        }
    }

    private fun generateFcmToken() {
        val fcmToken = Intent(this, MyFirebaseMessagingService::class.java)
        startService(fcmToken)
        FirebaseApp.initializeApp(this)
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.e("MyLogData  ", "==>  $token")
                prefs.setFcmToken(token)
                registerApi()
                //registerApi(token)
               // verifyRegotp(token)
            }
        }
    }

    private fun registerApi() {
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
            student_name = prefs.getName().toString(),
            app_code = "0488",
            token = prefs.getFcmToken().toString()
        )

        call.enqueue(object : Callback<RegisterData> {
            override fun onResponse(
                call: Call<RegisterData>,
                response: Response<RegisterData>
            ) {
                if (response.isSuccessful) {
                    prefs.setInsertId(response.body()?.insert_id.toString())
                    //prefs.setLoggedIn(true)
                    dismissProgressDialog()
                   // startActivity(Intent(this@RegisterActivity, HomePageActivity::class.java))
                    startActivity(Intent(this@RegisterActivity, OtpVerifyActivity::class.java))
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

    private fun verifyRegotp(token: String) {
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

        val call = loginApi.verifyRegotp(
            admission_no = binding.editTextAdmissionNumber.text.toString(),
            token = token
        )

        call.enqueue(object : Callback<VerifyRegOtp> {
            override fun onResponse(
                call: Call<VerifyRegOtp>,
                response: Response<VerifyRegOtp>
            ) {
                if (response.isSuccessful) {
                   // prefs.setInsertId(response.body()?.insert_id.toString())
                   // prefs.setLoggedIn(true)
                    Log.e("MyLogData","OTP" + response.body()!!.otp)
                    Log.e("MyLogData","verified" + response.body()!!.verified)
                    dismissProgressDialog()
                    //startActivity(Intent(this@RegisterActivity, HomePageActivity::class.java))
                    //intent.putExtra("message_key", message)
                   // finish()
                } else {
                    dismissProgressDialog()
                    showMessage(" Something went wrong : ${response.code()} \n NULL")
                }
            }

            override fun onFailure(call: Call<VerifyRegOtp>, t: Throwable) {
                dismissProgressDialog()
                Log.e("MyResponse", " failure registerApi Error ==> $t.message")
                showMessage("Something went wrong : ${t.message.toString()} \n onFailure")
            }
        })
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
                    prefs.setEmail(response.body()?.student_info?.email.toString())
                    prefs.setName(response.body()?.student_info?.student_name_uni.toString())
                    dismissProgressDialog()
                    binding.otherDetailsLayout.visibility = View.GONE
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
                Log.e("MyResponse", " failure getTransactionId Error 456 ==> ${t.message}")
                Log.e("MyResponse", " failure getTransactionId Error 456 printStackTrace ==> ${t.printStackTrace()}")
                showMessage("Something went wrong : ${t.message.toString()} \n onFailure")
            }
        })
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

}
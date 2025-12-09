package com.positron.teachers.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.positron.teachers.SpalshScreen
import com.positron.teachers.api.ApiClass
import com.positron.teachers.databinding.ActivityPasswordResetBinding
import com.positron.teachers.model.TeachersLogin
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class PasswordResetActivity : BaseActivity() {

    lateinit var binding: ActivityPasswordResetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordResetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSubmit.setOnClickListener {
            if (binding.etOldPassword.text.toString().isEmpty()) {
                showMessage("Please enter Old Password")
            } else if (binding.etNewPassword.text.toString().isEmpty()) {
                showMessage("Please enter New Password")
            } else if (binding.etConfirmPassword.text.toString().isEmpty()) {
                showMessage("Please enter Confirm Password")
            } else if (binding.etNewPassword.text.toString() != binding.etConfirmPassword.text.toString()) {
                showMessage("New Password and confirm password no same please enter same confirm password to continue")
            } else {
                passwordChange()
            }
        }

    }


    private fun passwordChange() {
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


        val call = loginApi.passwordChange(
            "Bearer ${prefs.getAuthorizationToken().toString()}",
            binding.etOldPassword.text.toString(),
            binding.etNewPassword.text.toString(),
            binding.etConfirmPassword.text.toString()
        )

        call.enqueue(object : Callback<TeachersLogin> {
            override fun onResponse(
                call: Call<TeachersLogin>,
                response: Response<TeachersLogin>,
            ) {
                Log.e("getDataFromLogin", "getDataFromLogin ==> ${response.body().toString()}")
                if (response.isSuccessful) {
                    if (response.body()?.status == true) {
                        showMessage(response.body()?.message.toString())
                        prefs.setLoggedIn(true)
                        val intent = Intent(this@PasswordResetActivity, SpalshScreen::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        dismissProgressDialog()
                        showMessage(response.body()?.message.toString())
                    }

                } else {
                    dismissProgressDialog()
                    showMessage(response.body()?.message.toString())
                }
            }

            override fun onFailure(call: Call<TeachersLogin>, t: Throwable) {
                dismissProgressDialog()
                //   Log.e("MyResponse", " failure registerApi Error ==> $t.message")
                showMessage("Something went wrong : ${t.message.toString()} \n onFailure")
            }
        })

    }
}
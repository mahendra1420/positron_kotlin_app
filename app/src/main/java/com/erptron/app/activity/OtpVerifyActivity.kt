package com.positron.teachers.activity


import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.core.text.HtmlCompat
import com.positron.teachers.R
import com.positron.teachers.api.ApiClass
import com.positron.teachers.databinding.ActivityOtpVerifyBinding
import com.positron.teachers.model.VerifyRegOtp
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class OtpVerifyActivity : BaseActivity(), View.OnClickListener {
    lateinit var binding: ActivityOtpVerifyBinding
    private lateinit var countDownTimer: CountDownTimer
    private val otpTimerDuration = 30L // Timer duration in seconds
    private var isTimerRunning = false
    //Steel Carmel DGP
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpVerifyBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnRegister.setOnClickListener(this)

        val text  = "An OTP has been sent to your registered email id <font color=\"#FFFFFF\"><font size=\"24\">${prefs.getEmail()}</font></font>, please enter that and verify"
        binding.tvEmail.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)

        binding.otpView.setOtpCompletionListener {
            val otpCode = binding.otpView.text.toString()
           // Log.e("MyLogData","otp" +otpCode)
        }

        // Set up the timer
        countDownTimer = object : CountDownTimer(otpTimerDuration * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                binding.tvTimer.text = secondsRemaining.toString()
            }

            override fun onFinish() {
                // Timer finished, enable the OTP sending functionality here
                isTimerRunning = false
                binding.tvTimer.text = "0"
                // Enable the OTP sending functionality here, like enabling the "Resend OTP" button
            }
        }

        // Start the timer when the activity starts
        startTimer()

        binding.tvresendotp.setOnClickListener {
            if (!isTimerRunning) {
                startTimer()
                // Disable the OTP sending functionality here, like disabling the "Resend OTP" button
            }
        }
    }

    override fun onClick(v: View?) {
        val viewId = v?.id

        if (viewId == R.id.btnRegister) {
            if(isOnline()){
            verifyRegotp()
            }
            //startActivity(Intent(this@OtpVerifyActivity, REgistersteptwoActivity::class.java))

            //finish()
        }
    }
    private fun startTimer() {
        countDownTimer.start()
        isTimerRunning = true
    }

    private fun verifyRegotp() {
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

        val call = loginApi.verifyotp(
            admission_no = prefs.getAdmissionNumber().toString(),
            otp=binding.otpView.text.toString(),
            token = prefs.getFcmToken().toString()
        )

        call.enqueue(object : Callback<VerifyRegOtp> {
            override fun onResponse(
                call: Call<VerifyRegOtp>,
                response: Response<VerifyRegOtp>
            ) {
                if (response.isSuccessful) {
                    dismissProgressDialog()

                   // Log.e("MyLogData","OTP" + response.body()!!.otp)
                   // Log.e("MyLogData","verified" + response.body()!!.verified)

                    if(response.body()!!.verified == true){
                        //binding.otpView.setText(response.body()!!.otp)
                         //prefs.setLoggedIn(true)
                        // startActivity(Intent(this@OtpVerifyActivity, HomePageActivity::class.java))
                         startActivity(Intent(this@OtpVerifyActivity, REgistersteptwoActivity::class.java))

                         finish()
                    } else{
                          showMessage("OTP Not Verified")

                    }

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

}
package com.positron.teachers

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
//import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import com.positron.teachers.activity.BaseActivity
import com.positron.teachers.activity.HomeActivity
import com.positron.teachers.activity.HomePageActivity
import com.positron.teachers.activity.PasswordResetActivity
import com.positron.teachers.api.ApiClass

import com.positron.teachers.databinding.ActivityMainBinding
import com.positron.teachers.firebase_services.MyFirebaseMessagingService
import com.positron.teachers.model.ResetPasswordModel
import com.positron.teachers.model.TeachersLogin
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

class SpalshScreen : BaseActivity(), View.OnClickListener  {

    var handler = Handler()
    private lateinit var binding: ActivityMainBinding
    var versionName = ""
    private lateinit var auth: FirebaseAuth
    private lateinit var googleAuth: GoogleSignInClient
    companion object {
        const val CONST_SIGN_IN = 34
    }



    fun isBiometricAvailable(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)

        return when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                // Biometric authentication is available and enabled
                true
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                // Device doesn't have biometric hardware
                false
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                // Biometric hardware is unavailable
                false
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                // Biometric is available but not enrolled
                false
            else -> false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()


        binding.tvSchoolName.text = prefs.getSchoolName()
        binding.tvAddressLine1.text = prefs.getAddress1()
        Glide.with(this)
            .load(prefs.getSchoolLogo())
            .apply(RequestOptions())
            .placeholder(R.drawable.logonew)
            .error(R.drawable.logonew)
            .into(binding.imageView)
        generateFcmToken()
        if (isBiometricAvailable(applicationContext)) {
            // Biometric authentication is available and enabled
         //   Log.e("MyLogData","isBiometricAvailable  if")
        } else {
          //  Log.e("MyLogData","isBiometricAvailable  else")
            // Biometric authentication is not available or not enabled
        }


        try {
            versionName = this.packageManager
                .getPackageInfo(this.packageName, 0).versionName.toString()
            Log.e("VERSION_NAME" , " ==> ${versionName}")
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        binding.resetPassword.setOnClickListener {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_layout, null)
            val editText = dialogView.findViewById<EditText>(R.id.editTextEmail) // Email input
            val cancelText = dialogView.findViewById<TextView>(R.id.cancelButton) // Cancel button
            val continueText = dialogView.findViewById<TextView>(R.id.continueButton) // Continue button

            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .create()

            // Handle "Cancel" button click
            cancelText.setOnClickListener {
                dialog.dismiss()
            }

            // Handle "Continue" button click
            continueText.setOnClickListener {
                val enteredText = editText.text.toString()
                if (enteredText.isEmpty()) {
                    showMessage("Please enter email to continue")
                } else {
                    resetPassword(enteredText, prefs.getSchoolId().toString(), dialog)
                }
            }

            dialog.show()
        }

        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override
            fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
              //  Toast.makeText(this@SpalshScreen, "Authentication successful!", Toast.LENGTH_SHORT).show()
                if (prefs.isLoggedIn()) {
                    val intent = Intent(this@SpalshScreen, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }


            override
            fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
               // Toast.makeText(this@SpalshScreen, "Authentication error: $errString", Toast.LENGTH_SHORT).show()
            //    Toast.makeText(this@SpalshScreen, "Authentication failed Something went Wrong", Toast.LENGTH_SHORT).show()

            }

            override
            fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
             //   Toast.makeText(this@SpalshScreen, "Authentication failed", Toast.LENGTH_SHORT).show()
            }
        })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Fingerprint Authentication")
            .setSubtitle("Confirm fingerprint to access app")
            .setDescription("Use your fingerprint to authenticate")
            .setNegativeButtonText("Cancel")
            .build()

        // Trigger authentication when needed
        biometricPrompt.authenticate(promptInfo)

        // Configure Google Sign In
        /*val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.clientid))
            .requestEmail()
            .build()

        googleAuth = GoogleSignIn.getClient(this, gso)*/

        binding.btnSubmit.setOnClickListener(this)

    }

    private fun resetPassword(email: String, schoolId: String, dialog: AlertDialog) {
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

        val call = loginApi.resetPassword(schoolId , email)

        call.enqueue(object : Callback<ResetPasswordModel> {
            override fun onResponse(
                call: Call<ResetPasswordModel>,
                response: Response<ResetPasswordModel>
            ) {
                Log.e("getDataFromLogin" , "getDataFromLogin ==> ${response.body().toString()}")
                Log.e("getDataFromLogin" , "getDataFromLogin ==> ${response.code().toString()}")
                if (response.isSuccessful) {
                    if (response.body()?.status == true) {
                        dialog.dismiss()
                        dismissProgressDialog()
                        showMessage(response.body()?.message.toString())
                    } else {
                        dismissProgressDialog()
                        showMessage(response.body()?.message.toString())
                    }

                } else {
                    dismissProgressDialog()
                    showMessage("unsuccessful  " + response.body()?.message.toString())
                }
            }

            override fun onFailure(call: Call<ResetPasswordModel>, t: Throwable) {
                dismissProgressDialog()
                //   Log.e("MyResponse", " failure registerApi Error ==> $t.message")
                showMessage("Something went wrong : ${t.message.toString()} \n onFailure")
            }
        })
    }

    override fun onResume() {
        super.onResume()
        //handler.postDelayed(runnable, 3000)

        /*if (prefs.isLoggedIn()) {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }*/
    }

    var runnable =
        Runnable {
            /*val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()*/

            if (prefs.isLoggedIn()) {
                val intent = Intent(this, HomePageActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, HomeActivity::class.java)//RegisterActivity
                startActivity(intent)
                finish()
            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        /*if(requestCode== CONST_SIGN_IN){
            val task=GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val accout=task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(accout.idToken)
            }
            catch (e: ApiException){
                Toast.makeText(this,"${e}",Toast.LENGTH_LONG).show()
            }
        }*/
    }

    private fun navigateToMain() {
       // handler.postDelayed(runnable, 3000)
        //startActivity(Intent(this, MainActivity::class.java))
        //finishAffinity()
    }

    private fun popupSnackBarForCompleteUpdate() {
        val snackBar = Snackbar.make(
            findViewById(android.R.id.content),
            "An update has just been downloaded.",
            Snackbar.LENGTH_INDEFINITE
        )
        snackBar.setAction("RESTART") {  }
        snackBar.setActionTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
        snackBar.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View?) {
        val viewId = v?.id
        if (viewId == R.id.btnSubmit) {
            if (validation()) {
                if(isOnline()){
                    getDataFromLogin(binding.editTextEmail.text.toString(),binding.editTextPassword.text.toString())
                   // GoogleSignIN()
                }
            } else {
                Toast.makeText(this, "PLease fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun generateFcmToken() {

/*        Firebase.messaging.subscribeToTopic("steel_teacher_notification")
            .addOnCompleteListener { task ->
                var msg = "Subscribed"
                if (!task.isSuccessful) {
                    msg = "Subscribe failed"
                }
                Log.d("subscribeToTopic", msg)
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            }*/

        val fcmToken = Intent(this, MyFirebaseMessagingService::class.java)
        startService(fcmToken)
        FirebaseApp.initializeApp(this)
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.e("MyLogData  ", "==>  $token")
                prefs.setFcmToken(token)
                //registerApi(token)
                // verifyRegotp(token)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDataFromLogin(email: String, password: String) {
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

        var originalString = password
        var encodedString = Base64.getEncoder().encodeToString(originalString.toByteArray())

        val call = loginApi.getTeacherLogin(
           email, prefs.getSchoolCode().toString(), prefs.getSchoolId().toString() ,password, prefs.getFcmToken().toString() , versionName , "${Build.BRAND + Build.MODEL }"
        )

        call.enqueue(object : Callback<TeachersLogin> {
            override fun onResponse(
                call: Call<TeachersLogin>,
                response: Response<TeachersLogin>
            ) {
                Log.e("MyResponse" , "getDataFromLogin ==> ${response.body().toString()}")
                if (response.isSuccessful) {
                    if (response.body()?.status == true) {
                        if (response.body()?.mobile_app_default_pass == "1") {
                            prefs.setTeacherId(response.body()?.id.toString())
                            prefs.setName(response.body()?.username)
                            prefs.setEmployCode(response.body()?.employee_id)
                            prefs.setEmail(response.body()?.email)
                            prefs.setAuthorizationToken(response.body()?.token)
                            prefs.setImage(response.body()?.image)
                            prefs.setContactNo(response.body()?.contact_no)
                            prefs.setSubscribeTopicName(response.body()?.channel.toString())
                            subscribeToTopic()
                            val intent = Intent(this@SpalshScreen, PasswordResetActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            prefs.setTeacherId(response.body()?.id.toString())
                            prefs.setName(response.body()?.username)
                            prefs.setEmail(response.body()?.email)
                            prefs.setAuthorizationToken(response.body()?.token)
                            prefs.setImage(response.body()?.image)
                            prefs.setContactNo(response.body()?.contact_no)
                            prefs.setEmployCode(response.body()?.employee_id)
                            prefs.setSubscribeTopicName(response.body()?.channel.toString())
                            prefs.setLoggedIn(true)
                            dismissProgressDialog()
                            subscribeToTopic()
                            val intent = Intent(this@SpalshScreen, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        dismissProgressDialog()
                        showMessage(response.body()?.error.toString())
                        Log.e("MyResponse", " failure else registerApi Error ==>" +response.body()?.error.toString() )
                    }

                } else {
                    dismissProgressDialog()
                    showMessage("${response.body()?.error.toString()}")
                    Log.e("MyResponse", " failure main else registerApi Error ==>" +response.body()?.error.toString() )
                }
            }

            override fun onFailure(call: Call<TeachersLogin>, t: Throwable) {
                dismissProgressDialog()
                Log.e("MyResponse", " failure registerApi Error ==> $t.message")
                showMessage("Something went wrong : ${t.message.toString()} \n onFailure")
            }
        })

    }


    private fun subscribeToTopic() {
        Firebase.messaging.subscribeToTopic(prefs.getSubscribeTopicName().toString())
            .addOnCompleteListener { task ->
                var msg = "Subscribed"
                if (!task.isSuccessful) {
                    msg = "Subscribe failed"
                }
                Log.d("subscribeToTopic", msg)
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            }
    }

    private fun validation(): Boolean {
        var check = true
        if (binding.editTextEmail.text.isEmpty()) check = false
        if (binding.editTextPassword.text.isEmpty()) check = false

        return check
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
               //     Log.d("TAG", "signInWithCredential:success")
                    val user = auth.currentUser
                  //  startActivity(Intent(this,MainActivity::class.java))
                } else {
                    // If sign in fails, display a message to the user.
               //     Log.w("TAG", "signInWithCredential:failure", task.exception)
//                    updateUI(null)
                }
            }
    }

    private fun GoogleSignIN() {
        val account=GoogleSignIn.getLastSignedInAccount(this)
        if(account==null){
            val signInIntent=googleAuth.signInIntent
            startActivityForResult(signInIntent, CONST_SIGN_IN)
        }
        else{
           // startActivity(Intent(this,MainActivity::class.java))

        }
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== CONST_SIGN_IN){
            val task=GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val accout=task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(accout.idToken)
            }
            catch (e: ApiException){
                Toast.makeText(this,"${e}",Toast.LENGTH_LONG).show()
            }
        }
    }*/
}
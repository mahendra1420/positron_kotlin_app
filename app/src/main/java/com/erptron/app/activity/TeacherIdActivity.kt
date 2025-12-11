package com.positron.teachers.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.positron.teachers.R
import com.positron.teachers.SpalshScreen
import com.positron.teachers.api.ApiClass
import com.positron.teachers.databinding.ActivityTeacherIdBinding
import com.positron.teachers.model.*
import com.positron.teachers.util.ApiConstants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class TeacherIdActivity :  BaseActivity(),View.OnClickListener {
    private lateinit var binding: ActivityTeacherIdBinding

    var versionName = ""

    private fun navigateToMain() {
       // showMessage("App is Not Update")
        // handler.postDelayed(runnable, 3000)
        //startActivity(Intent(this, MainActivity::class.java))
        //finishAffinity()
    }



    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_teacher_id)
        binding = ActivityTeacherIdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            versionName = this.packageManager
                .getPackageInfo(this.packageName, 0).versionName.toString()
            Log.e("VERSION_NAME" , " ==> ${versionName}")
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()

        }
        binding.tvVersion.text= "Version: " + versionName//BuildConfig.VERSION_NAME



        if(isOnline()) {
            getAppVersion()
        }



        binding.btnSubmit.setOnClickListener(this)

    }






    fun openAppPage(context: Context, packageName: String) {
        try {
            val uri = Uri.parse("market://details?id=$packageName")
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                setPackage("com.android.vending") // Only use this with market://
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                // Fallback to browser if Play Store app not found
                val webIntent = Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }

                context.startActivity(webIntent)
            } catch (ex: Exception) {
                ex.printStackTrace()
                Toast.makeText(context, "Unable to open Play Store", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onClick(v: View?) {
        val viewId = v?.id
        if (viewId == R.id.btnSubmit) {
            if (validation()) {
                if(isOnline()){
                   // openAppPage(this, BuildConfig.APPLICATION_ID)
                    getDataFromSchoolInfo(binding.editTextSchoolId.text.toString())/*"2509")//binding.editTextSchoolId.text.toString())*/
                }
            } else {
                Toast.makeText(this, "PLease fill School code", Toast.LENGTH_SHORT).show()
            }
        }
    }


    /*override fun onResume() {
        super.onResume()
        //showMessage("onResume")
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
               // && appUpdateInfo.updatePriority() >= 4
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                try{
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        updateType,
                        this,
                        123
                    )
                  */
    /*  appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        activityResultLauncher,
                        AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build())*//*
                } catch (exception: IntentSender.SendIntentException) {
                    Toast.makeText(this, "Update Fail", Toast.LENGTH_SHORT).show()
                }
                appUpdateManager.registerListener(installStateUpdateListener)
            }
            else{
                if (prefs.isLoggedIn()) {
                    val intent = Intent(this, SpalshScreen::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }*/

    override fun onResume() {
        super.onResume()
        if(isOnline()) {
            getAppVersion()
        }
        else{
            if (prefs.isLoggedIn()) {
                val intent = Intent(this, SpalshScreen::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123 && resultCode != RESULT_OK) {
            showMessage("Update Successful")
            val intent = Intent(this, SpalshScreen::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        } else {
            showMessage("Update Failed")
        }
    }

    private fun validation(): Boolean {
        var check = true
        if (binding.editTextSchoolId.text.isEmpty()) check = false


        return check
    }

    override fun onDestroy() {
        super.onDestroy()

        //if(updateType ==  AppUpdateType.IMMEDIATE){
         //   showMessage("onDestroy" )
         //   appUpdateManager.unregisterListener(installStateUpdateListener)
       // }
    }

    private fun showUpdateDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        val customLayout = layoutInflater.inflate(R.layout.alert_dialog_update, null)
        dialogBuilder.setView(customLayout)

        dialogBuilder.setPositiveButton("Update") { _, _ ->
            openAppPage(this, "com.positron.teachers") // This now works properly
        }

        val alertDialog = dialogBuilder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun getAppVersion() {
        //showProgressDialog()

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
            .baseUrl(ApiConstants.SCHOOL_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        val loginApi = retrofit.create(ApiClass::class.java)

        val call = loginApi.getAppVersion()

        call.enqueue(object : Callback<AppVersionData> {
            @RequiresApi(Build.VERSION_CODES.S)
            override fun onResponse(
                call: Call<AppVersionData>,
                response: Response<AppVersionData>,
            ) {
                if (response.isSuccessful) {
                    if("16.0"/*BuildConfig.VERSION_NAME */== response.body()?.app_version){
//                    if(versionName/*BuildConfig.VERSION_NAME */== response.body()?.app_version){
                        Log.e("MyLogData","if versionName" + versionName )
                        Log.e("MyLogData","if " + response.body()?.app_version)


                    }else{
                        Log.e("MyLogData","else ")
                        showUpdateDialog()
                    }

                } else {
                 //   dismissProgressDialog()
                    showMessage("Something went wrong : ${response.code()} \n NULL")
                }
            }

            override fun onFailure(call: Call<AppVersionData>, t: Throwable) {
              //  dismissProgressDialog()
                Log.e("MyResponse", " failure getTransactionId Error 789 ==> ${t.message}")
                showMessage("Something went wrong : ${t.message.toString()} \n onFailure")
            }
        })
    }

    private fun getDataFromSchoolInfo(schoolCode: String) {
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
            .baseUrl(ApiConstants.SCHOOL_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        val loginApi = retrofit.create(ApiClass::class.java)

        val call = loginApi.getDataFromSchoolInfo(schoolCode)

        call.enqueue(object : Callback<SchoolInfoNew<SchoolInfo>> {
            @RequiresApi(Build.VERSION_CODES.S)
            override fun onResponse(
                call: Call<SchoolInfoNew<SchoolInfo>>,
                response: Response<SchoolInfoNew<SchoolInfo>>,
            ) {
                if (response.isSuccessful) {
                    Log.e("MyLogData", "getDataFromSchoolInfo ==>${response.body().toString()}")
                    if (response.body()?.error_message != null) {
                        dismissProgressDialog()
                        showMessage(response.body()?.error_message)
                    } else {
                        if (response.code() == 200) {


                            prefs.setSchoolCode(schoolCode)
                            prefs.setSchoolName(response.body()?.school_info?.school_name.toString())
//                            prefs.setErpUrl("https://erptron.org/api/")//info
                            prefs.setErpUrl("https://positron.cloudsoftware.website/api_teachers/")//info
//                            prefs.setErpUrl("https://schoolerp.positrononline.in/ERP/public/api/")
                            prefs.setAddress1(response.body()?.school_info?.address_line_1.toString())
                            prefs.setSchoolId(response.body()?.school_info?.school_id.toString())
                            prefs.setAddress2(response.body()?.school_info?.address_line_2.toString())
                            prefs.setAddress3(response.body()?.school_info?.address_line_3.toString())
                            prefs.setSchoolLogo(response.body()?.school_info?.school_logo.toString())
                            prefs.setSchoolTag(response.body()?.school_info?.school_tagline.toString())
                            // dismissProgressDialog()

                            if (response.body()?.school_info?.school_logo.toString()
                                    .isNullOrEmpty()
                            ) {
                                //  Log.e("MyLogData", "image  if" + response.body()?.school_info?.school_logo.toString())
                                prefs.setSchoolLogo(response.body()?.school_info?.school_logo.toString())
                                //  prefs.setImage(response.body()?.school_info?.school_logo.toString())
                            } else {
                                prefs.setSchoolLogo(response.body()?.school_info?.school_logo.toString())
                                //prefs.setImage(response.body()?.school_info?.school_logo.toString())
                                //  Log.e("MyLogData", "image else" + response.body()?.school_info?.school_logo.toString())
                            }
                            val field = ApiConstants::class.java.getDeclaredField("BASE_URL")
                            field.isAccessible = true
                            field.set(
                                null,
                                response.body()?.school_info?.erp_url + "api_teachers/"
                            ) // Replace with your desired URL
                            field.isAccessible = false

                            dismissProgressDialog()

                            val intent = Intent(this@TeacherIdActivity, SpalshScreen::class.java)
                            startActivity(intent)
                            finish()
                        }
                    else
                    {
                        dismissProgressDialog()
                        showMessage(response.body()?.error_message.toString())
                    }
                }

                } else {
                    dismissProgressDialog()
                    showMessage("Something went wrong : ${response.code()} \n NULL")
                }
            }

            override fun onFailure(call: Call<SchoolInfoNew<SchoolInfo>>, t: Throwable) {
                dismissProgressDialog()
                Log.e("MyResponse", " failure getTransactionId Error 987 ==> ${t.message}")
                Log.e("MyResponse", " failure getTransactionId Error 987 cause ==> ${t.cause.toString()}")
                showMessage("Something went wrong : ${t.message.toString()} \n onFailure")
            }
        })
    }
}
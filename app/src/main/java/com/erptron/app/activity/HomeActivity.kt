package com.positron.teachers.activity

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build

import android.os.Bundle
import android.util.Log

import android.view.View
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.positron.teachers.R

import com.positron.teachers.api.ApiClass
import com.positron.teachers.databinding.ActivityHomeBinding
import com.positron.teachers.model.Circular
import com.positron.teachers.model.CircularUpdates
import com.positron.teachers.model.ClassRoutinePDF
import com.positron.teachers.model.NoticeBoardWeb
import com.positron.teachers.model.NoticeData
import com.positron.teachers.util.ApiConstants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit


class HomeActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityHomeBinding
    private var circularDataList: MutableList<CircularUpdates> = mutableListOf()
    private val calendar = Calendar.getInstance()
    private var dayNow = calendar.get(Calendar.DAY_OF_MONTH)
    private var noticeDataList: MutableList<NoticeData> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_home)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rlAttendance.setOnClickListener(this)
        binding.rlMarksEntry.setOnClickListener(this)
        binding.rlTeacherRemarks.setOnClickListener(this)
        binding.rlStudyMatireal.setOnClickListener(this)
        binding.rlCircular.setOnClickListener(this)
        binding.rlNotice.setOnClickListener(this)
        binding.rlExamRoutine.setOnClickListener(this)
        binding.rlClassRoutine.setOnClickListener(this)
        binding.rlAttendanceHistory.setOnClickListener(this)
        binding.rlLiveClass.setOnClickListener(this)
        binding.rlVideoClasses.setOnClickListener(this)
        binding.rvPracticeWorkSheet.setOnClickListener(this)

        binding.ivLogout.setOnClickListener(this)
        binding.btnBack.setOnClickListener(this)
        binding.tvName.text = prefs.getName()
        binding.tvEmail.text = prefs.getEmail()
        binding.tvID.text = prefs.getEmployCode().toString()
        binding.tvContactNo.text ="+91 "+ prefs.getContactNo()
        Glide.with(this)
            .load(prefs.getImage().toString())
            .apply(RequestOptions())
            .placeholder(R.drawable.dummy)
            .error(R.drawable.dummy)
            .into(binding.profileImage)

        if(isOnline()){
            getCircularData()
//            getNoticeData()
        }

        binding.ivDelete.setOnClickListener {
            startActivity(Intent(this@HomeActivity , PasswordResetActivity::class.java))
        }


        /*if (PermissionManager.checkStoragePermission(this)) {
            if (PermissionManager.checkWriteStoragePermission(this)) {
                try{
                    val filename ="TeacherApp_" + dayNow + ".txt"
                    Log.e("test","test"+filename)
                    var path = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS
                    )
                    Log.e("test","test"+path)
                    //val outputFile = File(applicationContext.externalCacheDir,filename)
                    val outputFile = File(path,filename)
                    Runtime.getRuntime().exec("logcat -f" + outputFile.absolutePath)
                    getDeviceInfo()
                    Log.e("MyLogData","getDeviceInfo " +  getDeviceInfo().toString() )
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
        }*/


    }

    fun getDeviceInfo(): String {
        val androidVersion = Build.VERSION.SDK_INT
        val phoneModel = Build.MODEL

        return "Android Version: $androidVersion\nPhone Model: $phoneModel"

    }

    override fun onClick(v: View?) {
        val viewId = v?.id

        if (viewId == R.id.rlNotice){
            startActivity(Intent(this@HomeActivity, TeacherNoticeActivity::class.java))
        }else if (viewId == R.id.rlStudyMatireal){
            startActivity(Intent(this@HomeActivity, StudyMaterialsActivity::class.java))
        }
        else if (viewId == R.id.rlAttendance){
              //showMessage("Coming Soon....")
            startActivity(Intent(this@HomeActivity, AttendancetActivity::class.java))
        }else if (viewId == R.id.rlCircular){
           // startActivity(Intent(this@HomeActivity, CircularActivity::class.java))
            startActivity(Intent(this@HomeActivity, StudentPhotoListActivity::class.java))
        }
        else if(viewId == R.id.rlExamRoutine){
            //showMessage("Coming Soon....")
            startActivity(Intent(this@HomeActivity, ExamRoutineActivity::class.java))
        }
        else if(viewId == R.id.rlClassRoutine){
            //showMessage("Coming Soon....")
//            startActivity(Intent(this@HomeActivity, ClassRoutineActivity::class.java))
            getNoticeBoardDataFromDateExam();
        }
        else if(viewId == R.id.rlMarksEntry){
            //showMessage("Coming Soon....")
            startActivity(Intent(this@HomeActivity, MarksEntryActivity::class.java))
        }

        else if(viewId == R.id.rvPracticeWorkSheet){
            //showMessage("Coming Soon....")
            startActivity(Intent(this@HomeActivity, StudentPhotoListActivity::class.java))
        }
        else if(viewId == R.id.card_view_fee){
            /*val webLink = "https://www.onlinesbi.sbi/sbicollect/icollecthome.htm?corpID=2043556"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(webLink))
            startActivity(intent)*/
           // showMessage("Coming Soon....")

        }
        else if(viewId == R.id.rlTeacherRemarks){
          //  showMessage("Coming Soon....")
             startActivity(Intent(this@HomeActivity, TeacherRemarksActivity::class.java))
        }
        else if(viewId == R.id.rlLiveClass){
              showMessage("Coming Soon....")
           // startActivity(Intent(this@HomeActivity, LiveClassActivity::class.java))
        }
        else if(viewId == R.id.rlVideoClasses){
            startActivity(Intent(this@HomeActivity, StudentPhotoListActivity::class.java))
           // startActivity(Intent(this@HomeActivity, LiveClassActivity::class.java))
        }
        else if(viewId == R.id.rlAttendanceHistory){
            //  showMessage("Coming Soon....")
            startActivity(Intent(this@HomeActivity, AttendanceHistoryActivity::class.java))
        }
        else if (viewId == R.id.iv_logout) {
            //logOut()
            showLogoutConfirmationDialog()
        }else if (viewId == R.id.btnBack) {
           // onBackPressed()
           // showExitDialog()
            ExitDialog()
        }
    }

    private fun getNoticeBoardDataFromDateExam() {
        showProgressDialog()
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(ApiConstants.SCHOOL_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        val loginApi = retrofit.create(ApiClass::class.java)

        val call = loginApi.getClassRoutine1("Bearer ${prefs.getAuthorizationToken().toString()}")

        call.enqueue(object : Callback<ClassRoutinePDF> {
            override fun onResponse(
                call: Call<ClassRoutinePDF>,
                response: Response<ClassRoutinePDF>,
            ) {
                if (response.isSuccessful) {
                    if (response.body()?.status == true) {
                        dismissProgressDialog()
                        val pdfUrl = response.body()?.pdf_url.toString()
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.setDataAndType(Uri.parse(pdfUrl), "application/pdf")
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                    } else {
                        showMessage("No report card found")
                        dismissProgressDialog()
                    }
                } else {
                    dismissProgressDialog()
                    showMessage(" Something went wrong : ${response.code()} \n NULL")
                }
            }

            override fun onFailure(call: Call<ClassRoutinePDF>, t: Throwable) {
                dismissProgressDialog()
                showMessage("Something went wrong : ${t.message.toString()} \n onFailure")
            }
        })
    }




    private fun getCircularData() {
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
            .baseUrl(prefs.getErpUrl().toString())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        val loginApi = retrofit.create(ApiClass::class.java)

        val call = loginApi.getCircularData()

        call.enqueue(object : Callback<Circular<List<CircularUpdates>>> {
            override fun onResponse(
                call: Call<Circular<List<CircularUpdates>>>,
                response: Response<Circular<List<CircularUpdates>>>
            ) {
                if (response.isSuccessful) {
                //    dismissProgressDialog()
                    circularDataList.clear()
                    circularDataList = response.body()?.circular_updates as MutableList<CircularUpdates>
                    try {
                        binding.tvCircular.text = circularDataList[0].title
                    }
                    catch (e:Exception){
                        e.printStackTrace()
                    }
                } else {
               //     dismissProgressDialog()
                    showMessage(" Something went wrong : ${response.code()} \n NULL")
                }
            }

            override fun onFailure(call: Call<Circular<List<CircularUpdates>>>, t: Throwable) {
              //  dismissProgressDialog()
              //  Log.e("MyResponse", " failure registerApi Error ==> $t.message")
                showMessage("Something went wrong : ${t.message.toString()} \n onFailure")
            }
        })
    }

    private fun getNoticeData() {
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

        val call = loginApi.getNoticeData("Bearer ${prefs.getAuthorizationToken().toString()}",)

        call.enqueue(object : Callback<NoticeBoardWeb<List<NoticeData>>> {
            override fun onResponse(
                call: Call<NoticeBoardWeb<List<NoticeData>>>,
                response: Response<NoticeBoardWeb<List<NoticeData>>>
            ) {
                if (response.isSuccessful) {
                    noticeDataList.clear()
                    noticeDataList = response.body()?.personal_notice as MutableList<NoticeData>
                    if (noticeDataList.isEmpty()) {

                        binding.tvCircular.text = "The School Circulars Will be Displayed here"
                    } else {

                        try {
                            binding.tvCircular.text = noticeDataList[0].title
                        }
                        catch (e:Exception){
                            e.printStackTrace()
                        }
                    }
                    dismissProgressDialog()

                } else {
                    dismissProgressDialog()
                    showMessage(" Something went wrong : ${response.code()} \n NULL")
                }
            }

            override fun onFailure(call: Call<NoticeBoardWeb<List<NoticeData>>>, t: Throwable) {
                dismissProgressDialog()
                Log.e("MyResponse", " failure registerApi Error ==> $t.message")
                showMessage("Something went wrong : ${t.message.toString()} \n onFailure")
            }
        })
    }

    var LogoutAlert: Dialog? = null
    private fun ExitDialog() {
        if (LogoutAlert != null) {
            if (LogoutAlert!!.isShowing) {
                return
            }
        }
        val builder = AlertDialog.Builder(this@HomeActivity)
        builder.setTitle("Exit App")
        builder.setMessage("Are you sure you want to exit this application?")
        builder.setPositiveButton("Exit") { dialogInterface, which ->

            finishAffinity()
        }
        builder.setNeutralButton("Cancel") { dialogInterface, which ->

        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun showExitDialog() {
        val dialogBuilder = AlertDialog.Builder(this)


        val customLayout = layoutInflater.inflate(R.layout.alert_dialog_logout, null)
        dialogBuilder.setView(customLayout)

         dialogBuilder.setMessage("Are you sure you want to Exit?")

        dialogBuilder.setPositiveButton("OK") { dialogInterface, _ ->

            onBackPressed()
            //logOut()
        }

        dialogBuilder.setNegativeButton("Cancel") { dialogInterface, _ ->

            dialogInterface.dismiss()
        }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
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
        startActivity(Intent(this@HomeActivity, TeacherIdActivity::class.java))
        finish()
        dismissProgressDialog()
    }
}
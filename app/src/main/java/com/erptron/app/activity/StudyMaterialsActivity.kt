package com.positron.teachers.activity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.positron.teachers.adapters.StudyAdapter
import com.positron.teachers.R
import com.positron.teachers.api.ApiClass

import com.positron.teachers.databinding.ActivityStudyMaterialsBinding
import com.positron.teachers.model.SaveAttendance
import com.positron.teachers.model.StudyMaterial
import com.positron.teachers.model.StudyMaterialMain
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class StudyMaterialsActivity : BaseActivity(), View.OnClickListener, StudyAdapter.BookingDetailsAdapterInterface {
    private lateinit var binding: ActivityStudyMaterialsBinding
    private val calendar = Calendar.getInstance()
    private var yearNow = calendar.get(Calendar.YEAR)
    private var monthNow = calendar.get(Calendar.MONTH)
    private var dayNow = calendar.get(Calendar.DAY_OF_MONTH)
    private var selectedMonth: Int? = null

    private var noticeDataList: MutableList<StudyMaterial> = mutableListOf()
    private var studyAdapter: StudyAdapter? = null

    var cal = Calendar.getInstance()
    var date = ""
    var isStartDate = false
    var startDate = ""
    var endtDate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_study_materials)
        binding = ActivityStudyMaterialsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnAddNew.setOnClickListener(this)
        binding.btnView.setOnClickListener(this)
        binding.ivCalendar.setOnClickListener(this)
        binding.ivLogout.setOnClickListener(this)
        binding.btnBack.setOnClickListener(this)
        binding.editTextClass.setOnClickListener(this)
        binding.editTextSection.setOnClickListener(this)
        binding.editTextSubject.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val viewId = v?.id
        if (viewId == R.id.btnAddNew){
            startActivity(Intent(this@StudyMaterialsActivity, AddNewRecordActivity::class.java))
            binding.rvStudy.visibility = View.GONE
            noticeDataList.clear()
            try{
                prefs.setClassTeacherClassName("")
                prefs.setTeacherClassSectionName("")
                prefs.setSubjectName("")
                prefs.setClassTeacherClassId("")
                prefs.setTeacherClassSectionId("")
                prefs.setSubjectId("")
            }catch (e:Exception){
                e.printStackTrace()
            }

        } else if (viewId == R.id.btnBack) {
            onBackPressed()
        }else if (viewId == R.id.iv_logout) {

            showLogoutConfirmationDialog()
        }else if (viewId == R.id.iv_calendar) {
            if(isOnline()){
                createDialogWithoutDateField()
             //   isStartDate = true
            //    openDatePicker()
            }
        }else if (viewId == R.id.editTextClass) {
            binding.editTextSection.setText("Select")
            binding.editTextSubject.setText("Select")
            prefs.setSubjectTeacherSectionName("")
            prefs.setSubjectTeacherSectionId("")
            prefs.setSubjectName("")
            prefs.setSubjectId("")
            noticeDataList.clear()
            studyAdapter?.notifyDataSetChanged()
            startActivity(
                Intent(
                    this@StudyMaterialsActivity,
                    DialogActivity::class.java
                ).putExtra("type", 3)
            )
        }
        else if (viewId == R.id.editTextSection) {
            if (binding.editTextClass.text.isNotEmpty()) {
                binding.editTextSubject.setText("Select")
                prefs.setSubjectName("")
                prefs.setSubjectId("")
                noticeDataList.clear()
                studyAdapter?.notifyDataSetChanged()
                startActivity(
                    Intent(
                        this@StudyMaterialsActivity,
                        DialogActivity::class.java
                    ).putExtra("type", 4)
                )
            } else {
                showMessage("please select class")
            }
        }
        else if (viewId == R.id.editTextSubject) {
            if (binding.editTextSection.text.isNotEmpty()) {
                noticeDataList.clear()
                studyAdapter?.notifyDataSetChanged()
                startActivity(
                    Intent(
                        this@StudyMaterialsActivity,
                        DialogActivity::class.java
                    ).putExtra("type", 7)
                )
            } else {
                showMessage("please select section")
            }
        }
        else if(viewId == R.id.btnView){
            if (binding.editTextSubject.text.isNotEmpty()) {
            getTeacherStudyMaterialsDataFromDate((monthNow+1).toString(), yearNow.toString())
            } else
            {
                showMessage("please select subject")
            }
        }
    }

    override fun onResume() {
        super.onResume()

        try {
            binding.editTextClass.setText(prefs.getSubjectTeacherClassName())
            binding.editTextSection.setText(prefs.getSubjectTeacherSectionName())
            binding.editTextSubject.setText(prefs.getSubjectName())


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        prefs.setSubjectTeacherClassName("")
        prefs.setSubjectTeacherSectionName("")
        prefs.setSubjectName("")
        prefs.setSubjectTeacherClassId("")
         prefs.setSubjectTeacherSectionId("")
        prefs.setSubjectId("")

    }

    override fun onBackPressed() {

        prefs.setSubjectTeacherClassName("")
        prefs.setSubjectTeacherSectionName("")
        prefs.setSubjectName("")
        prefs.setSubjectTeacherClassId("")
        prefs.setSubjectTeacherSectionId("")
        prefs.setSubjectId("")
        super.onBackPressed()
    }

    private fun createDialogWithoutDateField() {
        val now = System.currentTimeMillis() - 1000
        val monthDatePickerDialog = object : DatePickerDialog(
            this, android.app.AlertDialog.THEME_HOLO_LIGHT, { view, year, month, dayOfMonth ->
                selectedMonth = month + 1
            }, yearNow, monthNow, dayNow
        ) {
            @SuppressLint("DiscouragedApi")
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                datePicker.findViewById<View>(resources.getIdentifier("day", "id", "android"))
                    .visibility = View.GONE
                datePicker.maxDate = now
            }
        }
        monthDatePickerDialog.setTitle("Select Month")
        monthDatePickerDialog.show()
    }

    private fun getTeacherStudyMaterialsDataFromDate(month: String, year: String) {
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

        val call = loginApi.getTeacherStudyMaterial(
            "Bearer ${prefs.getAuthorizationToken().toString()}",
             prefs.getSubjectTeacherClassId().toString(),
             prefs.getSubjectTeacherSectionId().toString(),
            prefs.getSubjectId().toString())

        call.enqueue(object : Callback<StudyMaterialMain> {
            override fun onResponse(
                call: Call<StudyMaterialMain>,
                response: Response<StudyMaterialMain>
            ) {

                if (response.isSuccessful) {
                    if (response.body()?.status == true) {
                        dismissProgressDialog()
                        noticeDataList.clear()
                        val data = response.body()?.studyMaterials
                        Log.e("MyLogData" ," ===== " + data.toString())
                        noticeDataList = response.body()?.studyMaterials as MutableList<StudyMaterial>
                        if (noticeDataList.isEmpty()) {
                            binding.rvStudy.visibility = View.GONE
                            binding.noDataLayout.visibility = View.VISIBLE
                        } else {
                            binding.rvStudy.visibility = View.VISIBLE
                            binding.noDataLayout.visibility = View.GONE
                            studyAdapter =
                                StudyAdapter(this@StudyMaterialsActivity, noticeDataList, this@StudyMaterialsActivity)
                            binding.rvStudy.adapter = studyAdapter

                        }
                    } else {
                        dismissProgressDialog()
                        showMessage(response.body()?.message)
                        binding.rvStudy.visibility = View.GONE
                        binding.noDataLayout.visibility = View.VISIBLE
                    }

                } else {
                    dismissProgressDialog()
                    showMessage("Something went wrong : ${response.code()} \n NULL")
                }
            }

            override fun onFailure(call: Call<StudyMaterialMain>, t: Throwable) {
                dismissProgressDialog()
                //  Log.e("MyResponse", " failure registerApi Error ==> $t.message")
                showMessage("Something went wrong : ${t.message.toString()} \n onFailure")
            }
        })
    }

    private fun openDatePicker(){
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val sdf = SimpleDateFormat("yyyy-MM-dd")
                val tvdate = sdf.format(cal.time)
                date = sdf.format(cal.time)
                if (isStartDate) {
                    startDate = tvdate
                } else {
                    endtDate = tvdate
                }
                showMessage(startDate)
               // binding.tvDate.setText(startDate)

            }

        binding.root.context?.let {
            val datePickerDialog = DatePickerDialog(
                it,
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            // Set maxDate to prevent selecting future dates
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
            datePickerDialog.show()
        }
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
        startActivity(Intent(this@StudyMaterialsActivity, TeacherIdActivity::class.java))
        finish()
        dismissProgressDialog()
    }

    override fun onDocumentClick(item: StudyMaterial?) {
      /*  if (item!!.file_location.equals("GDrive")) {
            val url = "https://drive.google.com/u/0/uc?id=${item.document}&export=download"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        } else if (item!!.file_location.equals("positron_server")) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(item.document)
            startActivity(intent)
        }
        else {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(item.document)
            startActivity(intent)
        }*/
       // deleteStudyMaterials(item!!.id.toString())
        showDeleteConfirmationDialog(item!!.id.toString())
    }


    private fun showDeleteConfirmationDialog(id :String) {
        val dialogBuilder = AlertDialog.Builder(this)


        val customLayout = layoutInflater.inflate(R.layout.alert_deletedialog_logout, null)
        dialogBuilder.setView(customLayout)

        // dialogBuilder.setMessage("Are you sure you want to logout?")

        dialogBuilder.setPositiveButton("OK") { dialogInterface, _ ->


            deleteStudyMaterials(id.toString())
        }

        dialogBuilder.setNegativeButton("Cancel") { dialogInterface, _ ->

            dialogInterface.dismiss()
        }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    private fun deleteStudyMaterials(id: String) {
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

        val call = loginApi.deleteStudyMaterials(
            "Bearer ${prefs.getAuthorizationToken().toString()}",
            id.toString())

        call.enqueue(object : Callback<SaveAttendance> {
            override fun onResponse(
                call: Call<SaveAttendance>,
                response: Response<SaveAttendance>
            ) {

                if (response.isSuccessful) {
                    Log.e("MyLogData","delete   " + response.body())
                    if(response.body()?.success == true){
                        dismissProgressDialog()
                        showMessage(response.body()?.message)
                        getTeacherStudyMaterialsDataFromDate((monthNow+1).toString(), yearNow.toString())
                    }
                    else {
                        dismissProgressDialog()
                        showMessage(response.body()?.message)

                    }


                } else {
                    dismissProgressDialog()
                    showMessage("Something went wrong : ${response.code()} \n NULL")
                }
            }

            override fun onFailure(call: Call<SaveAttendance>, t: Throwable) {
                dismissProgressDialog()
                //  Log.e("MyResponse", " failure registerApi Error ==> $t.message")
                showMessage("Something went wrong : ${t.message.toString()} \n onFailure")
            }
        })
    }

    //@RequiresApi(Build.VERSION_CODES.O)
    override fun onSelected(item: StudyMaterial?) {
        downloadFile(item!!.attachment_file, this , item!!.title.toString())
       // Log.e("MyLogData","onSelected==>  " + item.attachment_file)
    }

    //@RequiresApi(Build.VERSION_CODES.O)
    fun downloadFile(url: String, context: Context, fileName: String, ) {

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(url)
        val progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Downloading")
        progressDialog.setMessage("Please wait while the file is downloading...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        val request = DownloadManager.Request(uri).apply {
            setTitle("Study Material")
            setDescription("Downloading study material...")
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        }
        val downloadId = downloadManager.enqueue(request)
        val downloadCompleteReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == action) {
                    val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                    Log.e("DownloadReceiver", "Received download complete. ID: $id, expected: $downloadId")
                    if (id == downloadId) {
                        Handler(Looper.getMainLooper()).post {
                            try {
                                progressDialog.dismiss()
                                Toast.makeText(context, "Download completed", Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                Log.e("DownloadReceiver", "Error dismissing dialog: ${e.message}")
                            }
                        }
                    }
                }
                Log.e("DownloadReceiver", "Error if no else ")
                progressDialog.dismiss()

            }
        }
        val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(downloadCompleteReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
            Log.e("DownloadReceiver", "Error if  ")
            progressDialog.dismiss()
        } else {
            context.registerReceiver(downloadCompleteReceiver, filter)
            Log.e("DownloadReceiver", "Error else ")
            progressDialog.dismiss()
        }
        Toast.makeText(context, "Download started", Toast.LENGTH_SHORT).show()
    }

    fun downloadFiled(url: String, context: Context, fileName: String) {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(url)

        // Use WeakReference to avoid memory leaks
        val progressDialog = ProgressDialog(context).apply {
            setTitle("Downloading")
            setMessage("Please wait while the file is downloading...")
            setCancelable(false)
            show()
        }
        val weakDialog = WeakReference(progressDialog)

        val request = DownloadManager.Request(uri).apply {
            setTitle("Study Material")
            setDescription("Downloading study material...")
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        }

        val downloadId = downloadManager.enqueue(request)

        val downloadCompleteReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == intent.action) {
                    val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                    Log.e("DownloadReceiver", "Download complete. ID: $id, expected: $downloadId")

                    if (id == downloadId) {
                        Handler(Looper.getMainLooper()).post {
                            weakDialog.get()?.let { dialog ->
                                if (dialog.isShowing) {
                                    dialog.dismiss()
                                    Toast.makeText(context, "Download completed", Toast.LENGTH_SHORT).show()
                                }
                                dialog.dismiss()
                            }
                        }
                    }
                }
            }
        }

        val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(downloadCompleteReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
            progressDialog.dismiss()
        } else {
            context.registerReceiver(downloadCompleteReceiver, filter)
            progressDialog.dismiss()
        }

        Toast.makeText(context, "Download started", Toast.LENGTH_SHORT).show()
    }

}
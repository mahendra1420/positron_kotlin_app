package com.positron.teachers.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.positron.teachers.R
import com.positron.teachers.api.ApiClass
import com.positron.teachers.databinding.ActivityUploadStudentPhotoBinding
import com.positron.teachers.model.GetStudentsPhotoList
import com.positron.teachers.model.SaveStudentPhoto
import com.positron.teachers.util.FileUtils
import com.positron.teachers.util.PathUtils
import com.positron.teachers.util.PermissionManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class UploadStudentPhotoActivity : BaseActivity(), View.OnClickListener {


    private lateinit var binding: ActivityUploadStudentPhotoBinding

    private var noticeData: GetStudentsPhotoList? = null

    private val ACTIVITY_CHOOSE_FILE1 = 1004
    private val PICK_IMAGE_REQUEST = 1005
    private var imageFile: File? = null
    private var currentPhotoPath: String? = null
    private var photoURI: Uri? = null
    var pathUtils: PathUtils? = null
    private val ACTIVITY_FOR_RESULT_CHOOSE_IMAGE = 1001
    private val MY_CAMERA_PERMISSION_CODE = 1002
    private val ACTIVITY_FOR_RESULT_PICK_IMAGE = 1003

    private var csvFile: File? = null

    private var filePath: String? = null

    private var isimage = false

    private lateinit var pickFileLauncher: ActivityResultLauncher<Intent>

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openGallery()
        } else {
            showMessage("Permission denied")
        }
    }

    private fun openGallery() {
        pickImageLauncher.launch("image/*")
    }

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        val intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(
            MediaStore.Images.Media.INTERNAL_CONTENT_URI,
            "image/*"
        )
        startActivityForResult(
            Intent.createChooser(
                intent,
                "Select Picture"
            ), ACTIVITY_FOR_RESULT_CHOOSE_IMAGE
        )
        // Handle the selected image URI
        // This URI can be used to load the image using Glide or set it to an ImageView
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_upload_student_photo)
        binding = ActivityUploadStudentPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener(this)
        binding.btnSave.setOnClickListener(this)
        binding.btnSearch.setOnClickListener(this)
        binding.ivLogout.setOnClickListener(this)
        //binding.editTextClass.setOnClickListener(this)
        //binding.editTextSection.setOnClickListener(this)
        binding.btnCam.setOnClickListener(this)
        binding.btnBrowse.setOnClickListener(this)

        if (intent != null) {
            noticeData = intent.getSerializableExtra("NoticeData") as GetStudentsPhotoList?

        }
        val photoUrl = noticeData?.student_photo
        Log.d("mine", "📸 Student Photo URL: $photoUrl")  // <-- Log added here
        binding.tvName.text = noticeData?.roll_no + ". " + noticeData?.first_name + " " + noticeData?.last_name
        Glide.with(this)
            .load(photoUrl)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.dummy)
                    .error(R.drawable.dummy)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .signature(ObjectKey(System.currentTimeMillis())) // force refresh
            )
            .into(binding.profileImage)

        binding.editTextClass.setText(noticeData?.class_name)
        binding.editTextSection.setText(noticeData?.section_name)


        /*    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    // Request the permission
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.data = Uri.parse("package:" + packageName)
                    startActivity(intent)

                }
            }*/

        pickFileLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == RESULT_OK) {
                    val uri: Uri? = result.data?.data
                    // Handle the file URI as needed
                    uri?.let {
                        handlePdfFile(it)
                        // Do something with the selected file
                    }
                }
            }
        //requestPermission()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onClick(v: View?) {
        val viewId = v?.id
        if (viewId == R.id.btnBack) {
            onBackPressed()
        } else if (viewId == R.id.iv_logout) {
            //logOut()
            showLogoutConfirmationDialog()
        } else if (viewId == R.id.editTextClass) {
            startActivity(
                Intent(
                    this@UploadStudentPhotoActivity,
                    DialogActivity::class.java
                ).putExtra("type", 1)
            )
        } else if (viewId == R.id.editTextSection) {
            if (binding.editTextClass.text.isNotEmpty()) {
                startActivity(
                    Intent(
                        this@UploadStudentPhotoActivity,
                        DialogActivity::class.java
                    ).putExtra("type", 2)
                )
            } else {
                showMessage("please select class")
            }
        } else if (viewId == R.id.btnBrowse) {
            if (PermissionManager.checkStoragePermission(this)) {
                if (PermissionManager.checkWriteStoragePermission(this)) {

                    //pickFile()
                    pickImageFile()

                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                //pickFile()
                pickImageFile()
            }
        } else if (viewId == R.id.btnCam) {
            galleryOrCameraDialog()
        } else if (viewId == R.id.btn_save) {
            if (isOnline()) {
                if (validation()) {
                    if(imageFile != null|| csvFile != null){
                        UploadStudentPhoto()
                    }
                    else
                    {
                        showMessage("The student photo field is required")
                    }

                } else
                {
                    showMessage("PLease fill all fields")
                }
            }
        }

    }

    private fun validation(): Boolean {
        var check = true
        if (binding.editTextClass.text.isEmpty()) check = false
        if (binding.editTextSection.text.isEmpty()) check = false


        return check
    }

    private fun fileExt(url: String): String {
        return if (url.contains(".")) MimeTypeMap.getFileExtensionFromUrl(url) else ""
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                var photoFile: File? = null
                try {
                    photoFile = createImageFile()
                    imageFile = File(currentPhotoPath!!)
                    isimage = true
                } catch (ex: IOException) {
                    ex.printStackTrace()
                }

                photoFile?.let { file ->
                    val localPhotoURI = FileProvider.getUriForFile(
                        this,
                        "com.positron.teachers.fileProvider",
                        file
                    )
                    photoURI = localPhotoURI // Optional: Assign to class-level property if needed
                    takePictureLauncher.launch(localPhotoURI)
                }

                /* val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                 if (takePictureIntent.resolveActivity(this.packageManager) != null) {
                     var photoFile: File? = null
                     try {
                         photoFile = createImageFile()
                         imageFile = photoFile
                     } catch (ex: IOException) {
                         ex.printStackTrace()
                     }
                     if (photoFile != null) {
                         photoURI = FileProvider.getUriForFile(
                             this,
                             "com.positron.teachers.fileProvider",
                             photoFile
                         )
                         takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                         Log.e("MyLogData", "startActivityForResult photoURIE ==> $photoURI")
                         startActivityForResult(
                             takePictureIntent,
                             ACTIVITY_FOR_RESULT_PICK_IMAGE
                         )
                     }
                 }*/
            } else {
                Toast.makeText(this, "Camera permission denied.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            // Image saved successfully, handle the photoURI
            binding.profileImage.setImageURI(photoURI)
            Log.e("MyLogData", "Photo captured successfully: $photoURI")
        } else {
            Log.e("MyLogData", "Photo capture failed")
        }
    }

    private fun pickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) !== PackageManager.PERMISSION_GRANTED
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.CAMERA),
                        MY_CAMERA_PERMISSION_CODE
                    )
                }
            } else {

                var photoFile: File? = null
                try {
                    photoFile = createImageFile()
                    imageFile = File(currentPhotoPath!!)
                    isimage = true
                } catch (ex: IOException) {
                    ex.printStackTrace()
                }

                /*      val photoFile: File? = try {
                          createImageFile()
                      } catch (ex: IOException) {
                          ex.printStackTrace()
                          null
                      }*/

                photoFile?.let { file ->
                    val localPhotoURI = FileProvider.getUriForFile(
                        this,
                        "com.positron.teachers.fileProvider",
                        file
                    )
                    photoURI = localPhotoURI // Optional: Assign to class-level property if needed
                    takePictureLauncher.launch(localPhotoURI)
                }

                /*  val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                  if (takePictureIntent.resolveActivity(this.packageManager) != null) {
                      var photoFile: File? = null
                      try {
                          photoFile = createImageFile()
                          imageFile = photoFile
                      } catch (ex: IOException) {
                          ex.printStackTrace()
                      }
                      if (photoFile != null) {
                          photoURI = FileProvider.getUriForFile(
                              this,
                              "com.positron.teachers.fileProvider",
                              photoFile
                          )
                          takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                          Log.e("MyLogData", "startActivityForResult photoURIE ==> $photoURI")
                          startActivityForResult(
                              takePictureIntent,
                              ACTIVITY_FOR_RESULT_PICK_IMAGE
                          )
                      }
                  }*/
            }
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir: File? =
            getExternalFilesDir(Environment.DIRECTORY_DCIM)
        val image = File.createTempFile(
            imageFileName,  /*prefix*/
            ".jpg",  /*suffix*/
            storageDir /*directory*/
        )
        currentPhotoPath = image.absolutePath
        Log.e("MyLogData", "exists ==> " + image.exists())
        Log.e("MyLogData", "path ==> " + image.path)
        Log.e("MyLogData", "currentPhotoPath ==> " + currentPhotoPath)
        //binding.editTextDocument.setText(imageFileName)
        return image
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun galleryOrCameraDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.custom)
        dialog.setTitle("Select Image")
        val camera: LinearLayout = dialog.findViewById<View>(R.id.camera) as LinearLayout
        camera.visibility = View.VISIBLE
        val gallery: LinearLayout = dialog.findViewById<View>(R.id.gallery) as LinearLayout
        val dialogButton: TextView = dialog.findViewById<View>(R.id.cancel) as TextView

        camera.setOnClickListener {
            pickImage()
            dialog.dismiss()
        }

        gallery.setOnClickListener {
            if (PermissionManager.checkStoragePermission(this)) {
                if (PermissionManager.checkWriteStoragePermission(this)) {
                    val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
                    intent.setDataAndType(
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                        "image/*"
                    )
                    startActivityForResult(
                        Intent.createChooser(
                            intent,
                            "Select Picture"
                        ), ACTIVITY_FOR_RESULT_CHOOSE_IMAGE
                    )
                }
            }

            val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
            // starting activity on below line.
            startActivityForResult(intent, ACTIVITY_FOR_RESULT_CHOOSE_IMAGE)


            /*if (hasPermission()) {
                openGallery()
            } else {
                requestPermission()
            }*/
            dialog.dismiss()
        }

        dialogButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun getRealPathFromURI(contentURI: Uri): String? {
        val result: String?
        val cursor: Cursor =
            contentResolver.query(contentURI, null, null, null, null)!!
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.path
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }

    fun getFileFromUri(context: Context, uri: Uri?): File? {
        uri ?: return null
        uri.path ?: return null

        var newUriString = uri.toString()
        newUriString = newUriString.replace(
            "content://com.android.providers.downloads.documents/",
            "content://com.android.providers.media.documents/"
        )
        newUriString = newUriString.replace("/msf%3A", "/image%3A")
        val newUri = Uri.parse(newUriString)

        var realPath = String()
        val databaseUri: Uri
        val selection: String?
        val selectionArgs: Array<String>?
        if (newUri.path?.contains("") == true) {
            databaseUri = MediaStore.Images.Media.INTERNAL_CONTENT_URI
            selection = "_id=?"
            selectionArgs = arrayOf(DocumentsContract.getDocumentId(newUri).split(":")[1])
        } else {
            databaseUri = newUri
            selection = null
            selectionArgs = null
        }
        try {
            val column = "_data"
            val projection = arrayOf(column)
            val cursor = context.contentResolver.query(
                databaseUri,
                projection,
                selection,
                selectionArgs,
                null
            )
            cursor?.let {
                if (it.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndexOrThrow(column)
                    realPath = cursor.getString(columnIndex)
                }
                cursor.close()
            }
        } catch (e: Exception) {
            Log.i("GetFileUri Exception:", e.message ?: "")
        }
        val path = realPath.ifEmpty {
            when {
                newUri.path?.contains("/document/raw:") == true -> newUri.path?.replace(
                    "/document/raw:",
                    ""
                )

                newUri.path?.contains("/document/primary:") == true -> newUri.path?.replace(
                    "/document/primary:",
                    "/storage/emulated/0/"
                )

                else -> return null
            }
        }
        return if (path.isNullOrEmpty()) null else File(path)
    }

    private fun handlePdfFile(uri: Uri) {
        // Example: Open the PDF in a PDF viewer app
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            setDataAndType(uri, "image/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(intent)
    }

    private fun pickFile() {
        val intent: Intent
        val chooseFile = Intent(Intent.ACTION_GET_CONTENT)
        //chooseFile.type = "*/*"
        chooseFile.type = "image/*"
        //chooseFile.addCategory(Intent.CATEGORY_OPENABLE)
        intent = Intent.createChooser(chooseFile, "Choose a File")
        startActivityForResult(intent, ACTIVITY_CHOOSE_FILE1)
        //   pickFileLauncher.launch(intent)
    }

    private fun pickImageFile() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    @SuppressLint("Range")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d("mine", "onActivityResult() called with requestCode=$requestCode, resultCode=$resultCode")

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            val selectedImageUri: Uri? = data?.data
            Log.d("mine", "PICK_IMAGE_REQUEST: selectedImageUri = $selectedImageUri")

            binding.profileImage.setImageURI(selectedImageUri)

            if (selectedImageUri != null) {
                try {
                    val filePath = FileUtils.getPath(this, selectedImageUri)
                    Log.d("mine", "PICK_IMAGE_REQUEST: filePath = $filePath")
                    csvFile = File(filePath)
                    isimage = false
                } catch (e: Exception) {
                    Log.e("mine", "PICK_IMAGE_REQUEST: error converting URI to File", e)
                }
            }
        }

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                ACTIVITY_FOR_RESULT_CHOOSE_IMAGE -> {
                    val resultUri: Uri? = data?.data
                    Log.d("mine", "ACTIVITY_FOR_RESULT_CHOOSE_IMAGE: resultUri = $resultUri")

                    try {
                        if (resultUri != null) {
                            val realPath = getRealPathFromURI(resultUri)
                            Log.d("mine", "ACTIVITY_FOR_RESULT_CHOOSE_IMAGE: realPath = $realPath")

                            imageFile = File(realPath!!)
                            binding.profileImage.setImageURI(resultUri)
                            isimage = true
                        }
                    } catch (e: IOException) {
                        Log.e("mine", "ACTIVITY_FOR_RESULT_CHOOSE_IMAGE: error", e)
                    }
                }

                ACTIVITY_FOR_RESULT_PICK_IMAGE -> {
                    Log.d("mine", "ACTIVITY_FOR_RESULT_PICK_IMAGE: currentPhotoPath = $currentPhotoPath")

                    try {
                        imageFile = File(currentPhotoPath!!)
                        binding.profileImage.setImageURI(photoURI)
                        isimage = true
                    } catch (e: Exception) {
                        Log.e("mine", "ACTIVITY_FOR_RESULT_PICK_IMAGE: error", e)
                    }
                }

                ACTIVITY_CHOOSE_FILE1 -> {
                    val resultUri: Uri? = data?.data
                    Log.d("mine", "ACTIVITY_CHOOSE_FILE1: resultUri = $resultUri")

                    if (resultUri != null) {
                        try {
                            csvFile = getFileFromUri(this, resultUri)
                            Log.d("mine", "ACTIVITY_CHOOSE_FILE1: csvFile = ${csvFile?.absolutePath}")
                            binding.profileImage.setImageURI(resultUri)
                            isimage = false
                        } catch (e: IOException) {
                            Log.e("mine", "ACTIVITY_CHOOSE_FILE1: error", e)
                        }
                    } else {
                        Log.w("mine", "ACTIVITY_CHOOSE_FILE1: No file selected")
                        showMessage("No file selected")
                    }
                }
            }
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
        startActivity(Intent(this@UploadStudentPhotoActivity, TeacherIdActivity::class.java))
        finish()
        dismissProgressDialog()
    }

    override fun onResume() {
        super.onResume()
        try {
            binding.editTextClass.setText(prefs.getClassTeacherClassName())
            binding.editTextSection.setText(prefs.getTeacherClassSectionName())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        /* prefs.setSubjectTeacherClassName(null)
         prefs.setSubjectTeacherSectionName(null)
         prefs.setClassTeacherClassName("")
         prefs.setTeacherClassSectionName("")

         prefs.setClassTeacherClassId("")
         prefs.setTeacherClassSectionId("")*/

    }

    /*override fun onBackPressed() {
        prefs.setClassTeacherClassName("")
        prefs.setTeacherClassSectionName("")
        prefs.setClassTeacherClassId("")
        prefs.setTeacherClassSectionId("")
        super.onBackPressed()
    }*/

    var postfixToRemove = "api_teachers/"

    private fun removePostfix(originalString: String, postfixToRemove: String): String {
        if (originalString.endsWith(postfixToRemove)) {
            return originalString.substring(0, originalString.length - postfixToRemove.length)
        } else {
            return originalString
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun UploadStudentPhoto() {
        showProgressDialog()
        Log.e("MyLogData", "UploadStudentPhotoimg " + imageFile?.exists())
        Log.e("MyLogData", "UploadStudentPhoto csv " + csvFile?.exists())
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
            .baseUrl(prefs.getErpUrl().toString())//"https://positronschoolerp.com/api/"
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        val loginApi = retrofit.create(ApiClass::class.java)

        var multipartBody: MultipartBody.Part? = null

        if (isimage == false) {
            if (csvFile != null) {

                val requestFile: RequestBody =
                    csvFile!!.asRequestBody("image/*".toMediaTypeOrNull())
                multipartBody =
                    MultipartBody.Part.createFormData("student_photo", csvFile!!.name, requestFile)
            }
        } else {
            if (imageFile != null) {
                val requestFile: RequestBody =
                    imageFile!!.asRequestBody("image/*".toMediaTypeOrNull())
                multipartBody = MultipartBody.Part.createFormData(
                    "student_photo",
                    imageFile!!.name,
                    requestFile
                )
            }
        }

        val call = loginApi.UploadStudentPhoto(
            "Bearer ${
                prefs.getAuthorizationToken().toString()
            }",
            noticeData?.student_id.toString().toInt(),
            multipartBody
        )


        call.enqueue(object : Callback<SaveStudentPhoto> {
            override fun onResponse(
                call: Call<SaveStudentPhoto>,
                response: Response<SaveStudentPhoto>,
            ) {

                if (response.isSuccessful) {
                    if (response.body()?.response == true) {
                        showMessage(response.body()?.message)
                        prefs.setUpdatedStudentId(noticeData?.student_id ?: "")
                        binding.editTextSection.text.clear()
                        binding.editTextClass.text.clear()
                        csvFile = null
                        imageFile = null
                        onBackPressed()
                        dismissProgressDialog()
                    } else {
                        dismissProgressDialog()
                        showMessage(response.body()?.message)
                    }
                } else {
                    dismissProgressDialog()
                    Log.e("OnImageUpload", "on else ==> ${response.code()}")
                    showMessage(" Something went wrong : ${response.code()} \n NULL")
                }
            }

            override fun onFailure(call: Call<SaveStudentPhoto>, t: Throwable) {
                dismissProgressDialog()
                Log.e("OnImageUpload", " failure UploadStudentPhoto Error ==> ${t.message}")
                showMessage("Something went wrong : ${t.message.toString()} \n onFailure")
            }
        })

    }



}
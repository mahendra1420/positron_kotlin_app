package com.positron.teachers.activity

import android.Manifest
import android.annotation.SuppressLint

import android.app.Activity
import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.NetworkOnMainThreadException
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
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
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.FileContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import java.io.File as JavaFile
import com.google.firebase.auth.FirebaseAuth
import com.positron.teachers.R

import com.positron.teachers.api.ApiClass
import com.positron.teachers.api.GoogleOAuthApiService
import com.positron.teachers.databinding.ActivityAddNewRecordBinding
import com.positron.teachers.drive.DriveServiceHelper
import com.positron.teachers.model.*
import com.positron.teachers.util.PathUtils
import com.positron.teachers.util.PermissionManager.checkStoragePermission
import com.positron.teachers.util.PermissionManager.checkWriteStoragePermission
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class AddNewRecordActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityAddNewRecordBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var googleAuth: GoogleSignInClient
    companion object {
        const val CONST_SIGN_IN = 34
        private const val REQUEST_CODE_SIGN_IN = 100
        lateinit var driveServiceHelper: DriveServiceHelper
    }
    lateinit var mDrive: Drive
    private val ACTIVITY_CHOOSE_FILE1 = 1004
    private var imageFile: java.io.File? = null
    private var currentPhotoPath: String? = null
    private var photoURI: Uri? = null
    var pathUtils: PathUtils? = null
    private val ACTIVITY_FOR_RESULT_CHOOSE_IMAGE = 1001
    private val MY_CAMERA_PERMISSION_CODE = 1002
    private val ACTIVITY_FOR_RESULT_PICK_IMAGE = 1003


    private var csvFile: JavaFile? = null

    private val REQUEST_CODE_SIGN_IN = 1
    private val MIME_TYPE = "application/octet-stream"
    private lateinit var mGoogleApiClient: GoogleApiClient
    private  var mDriveService: Drive? = null
    private var filePath : String? = null
    private val TAG = "MainActivity"
    private val RC_SIGN_IN = 123
    private  var isimage = false

    private lateinit var pickFileLauncher: ActivityResultLauncher<Intent>

    private var mGoogleSignInClient: GoogleSignInClient? = null
    //private var mDriveClient: DriveClient? = null
    //private var mDriveResourceClient: DriveResourceClient? = null

/*        private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openGallery()
        } else {
            showMessage("Permission denied")
        }
    }*/

    private fun openGallery() {
        pickImageLauncher.launch("image/*")
    }

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        val intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
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

    /*private fun pickImagfe() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false) // Set to true for multiple image selection
        photoPickerLauncher.launch(intent)
    }

    private val photoPickerLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uris ->
        if (uris != null) {
            val selectedImageUri = uris // Get the first selected image URI
            // Use ContentResolver to access the image data
            val contentResolver = contentResolver
            val cursor = contentResolver.query(selectedImageUri, null, null, null, null)
            cursor?.moveToFirst()
            val columnIndex = cursor?.getColumnIndex(MediaStore.Images.Media.DATA)
            val filePath = cursor?.getString(columnIndex!!)
            cursor?.close()


            // Load the image into an ImageView or perform other operations
            val bitmap = BitmapFactory.decodeFile(filePath)
            binding.ivImage.setImageBitmap(bitmap)
        }
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_add_new_record)
        binding = ActivityAddNewRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)


      //  requestForSignIn()
        pathUtils = PathUtils()
     //   mDrive = getDriveService("Positron")
      //  binding.btnAddNew.setOnClickListener(this)
        binding.btnBack.setOnClickListener(this)
        binding.btnSave.setOnClickListener(this)
        binding.btnChoosefile.setOnClickListener(this)
        binding.btnRemove.setOnClickListener(this)
        binding.ivLogout.setOnClickListener(this)
        binding.editTextClass.setOnClickListener(this)
        binding.editTextSection.setOnClickListener(this)
        binding.editTextSubject.setOnClickListener(this)
        binding.ivImage.setOnClickListener(this)
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                // Request the permission
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:" + packageName)
                startActivity(intent)

            }
        }*/

        pickFileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val uri: Uri? = result.data?.data
                // Handle the file URI as needed
                uri?.let {
                    handlePdfFile(it)
                    // Do something with the selected file
                }
            }
        }
    }

    fun requestForSignIn(){
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(getString(R.string.clientid))
            .requestScopes(Scope(DriveScopes.DRIVE_FILE))
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, 400)
        Log.e("MyLogData","requestForSignIn")
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onClick(v: View?) {
        val viewId = v?.id
       if (viewId == R.id.btnBack) {
            onBackPressed()
        }
       else if (viewId == R.id.iv_logout) {
           //logOut()
           showLogoutConfirmationDialog()
       }
       else if(viewId == R.id.btn_choosefile){
           if (checkStoragePermission(this)) {
               if (checkWriteStoragePermission(this)) {

             pickFile()

               }
           }
           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
           //pickPdfFile()
               pickFile()
           }
       }
       else if(viewId == R.id.iv_image){
           galleryOrCameraDialog()
       }
       else if(viewId == R.id.btn_Remove){
           binding.editTextDocument.text=""
           binding.editTextTITLE.text.clear()
           binding.editTextDescription.text.clear()
           binding.ivImage.setImageResource(R.drawable.ic_gallary)

       }

       else if (viewId == R.id.editTextClass) {
           startActivity(
               Intent(
                   this@AddNewRecordActivity,
                   DialogActivity::class.java
               ).putExtra("type", 3)
           )
       }
       else if (viewId == R.id.editTextSection) {
           if (binding.editTextClass.text.isNotEmpty()) {
               startActivity(
                   Intent(
                       this@AddNewRecordActivity,
                       DialogActivity::class.java
                   ).putExtra("type", 4)
               )
           } else {
               showMessage("please select class")
           }
       }
       else if (viewId == R.id.editTextSubject) {
           if (binding.editTextSection.text.isNotEmpty()) {
               startActivity(
                   Intent(
                       this@AddNewRecordActivity,
                       DialogActivity::class.java
                   ).putExtra("type", 7)
               )
           } else {
               showMessage("please select section")
           }
       }
        else if (viewId == R.id.btn_save){

           /*val gso: GoogleSignInOptions =
               GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                   .requestEmail()
                   .setAccountName("rajeshdroid374@gmail.com") // Specify the fixed account
                   .build()*/
         //  mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
           //signIn()
         //  uploadFileToDrive()
           if(isOnline()) {
               if (validation()) {
                   saveStudyMaterials()
               } else
               {
                   showMessage("PLease fill all fields")
               }
           }

          // val file = JavaFile(filePath)
          // uploadFile(file)
        }

    }

    private fun validation(): Boolean {
        var check = true
        if (binding.editTextClass.text.isEmpty()) check = false
        if (binding.editTextSection.text.isEmpty()) check = false
        if (binding.editTextSubject.text.isEmpty()) check = false
        if (binding.editTextDocument.text.isEmpty()) check = false
        if (binding.editTextTITLE.text.isEmpty()) check = false
        if (binding.editTextDescription.text.isEmpty()) check = false

        return check
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun uploadFileToDrive() {
        val realFolderId = "123" // Replace with actual folder ID
        val fileToUpload = csvFile
        val uploadedFile = fileToUpload?.let { uploadToFolder(realFolderId,csvFile.toString()) }

        if (uploadedFile != null) {
            // Handle successful upload
            Toast.makeText(this, "File uploaded successfully", Toast.LENGTH_SHORT).show()
        } else {
            // Handle upload failure
            Toast.makeText(this, "File upload failed", Toast.LENGTH_SHORT).show()
        }
    }



    /*fun getMimeType(filename: String): String {
        // Implement logic to determine MIME type based on file extension
        // You can use libraries like MimeTypeMap for this
    }*/

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
        prefs.setClassTeacherClassName("")
        prefs.setTeacherClassSectionName("")
        prefs.setSubjectName("")
        prefs.setClassTeacherClassId("")
        prefs.setTeacherClassSectionId("")
        prefs.setSubjectId("")

    }

    override fun onBackPressed() {

        prefs.setClassTeacherClassName("")
        prefs.setTeacherClassSectionName("")
        prefs.setSubjectName("")
        prefs.setClassTeacherClassId("")
        prefs.setTeacherClassSectionId("")
        prefs.setSubjectId("")
        super.onBackPressed()
    }

    private fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

/*    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }*/

    private fun pickFile() {
        val intent: Intent
        val chooseFile = Intent(Intent.ACTION_OPEN_DOCUMENT)
        //chooseFile.type = "*/*"
        chooseFile.type = "application/pdf"
        //chooseFile.addCategory(Intent.CATEGORY_OPENABLE)
        intent = Intent.createChooser(chooseFile, "Choose a File")
        startActivityForResult(intent, ACTIVITY_CHOOSE_FILE1)
     //   pickFileLauncher.launch(intent)
    }

    private fun pickPdfFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
           // addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf" // To pick only PDF files
        }
        pickFileLauncher.launch(intent)
    }

    private fun handlePdfFile(uri: Uri) {
        // Example: Open the PDF in a PDF viewer app
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(intent)
    }

    @SuppressLint("Range")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //when (requestCode) {
            //400 -> {
                if (resultCode == 400) {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    //handleSignInIntent(data)
                    handleSignInResult(task)
                    Log.e("MyLogData","handleSignInIntent")
                }
        //    }
       // }
        if (requestCode == 111 && resultCode == RESULT_OK) {
            val selectedFile = data!!.data //The uri with the location of the file
            Toast.makeText(this,selectedFile.toString(),Toast.LENGTH_LONG).show()
        }

        /*if (requestCode == 1) {
            GoogleSignIn.getSignedInAccountFromIntent(data)
                .addOnSuccessListener { googleAccount ->
                    // Build a Drive client with the Google account
                    buildDriveClient(googleAccount)
                }
                .addOnFailureListener { e ->
                    // Handle sign-in failure
                    showMessage("Google Sign InFail")
                }
        }*/
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                ACTIVITY_FOR_RESULT_CHOOSE_IMAGE -> {
                    Log.e("MyLogData" , "ACTIVITY_FOR_RESULT_CHOOSE_IMAGE ==> ")
                    val resultUri: Uri? = data!!.data
                    try {
                        imageFile = java.io.File(getRealPathFromURI(resultUri!!)!!)
                        binding.ivImage.setImageURI(resultUri)
                        Log.e("MyLogData" , "ACTIVITY_FOR_RESULT_CHOOSE_IMAGE ==> " + resultUri.toString())
                        isimage = true
                        binding.editTextDocument.setText(resultUri.toString())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                ACTIVITY_FOR_RESULT_PICK_IMAGE -> {

                    imageFile = java.io.File(currentPhotoPath!!)
                   // imageFile = java.io.File(getRealPathFromURI(photoURI!!)!!)
                    binding.ivImage.setImageURI(photoURI)
                    Log.e("MyLogData" , "ACTIVITY_FOR_RESULT_PICK_IMAGE ==> " + photoURI  )
                    Log.e("MyLogData" , "ACTIVITY_FOR_RESULT_PICK_IMAGE ==> " + currentPhotoPath  )

                    isimage = true
                }

                /*ACTIVITY_CHOOSE_FILE1 -> {
                    if (data != null){
                        try {
                            val resultUri: Uri? = data.data
                            val uri: Uri = Uri.parse(resultUri.toString())
                             filePath = uri.path

                            try {
                                val filePathColumn = arrayOf(MediaStore.MediaColumns.DATA)
                                val cursor = contentResolver.query(uri!!, filePathColumn, null, null, null)
                                cursor?.moveToFirst()
                                val filePath = cursor?.getString(filePathColumn.indexOf(MediaStore.MediaColumns.DATA))
                                cursor?.close()

                                csvFile = JavaFile(filePath!!)
                                //val name = csvFile!!.name
                               // binding.editTextDocument.setText(name)
                                binding.editTextDocument.setText(filePath)
                               // Log.e("MyLogData" , " ==> " + csvFile!!.exists())
                                //Log.e("MyLogData" , " ==> " + csvFile!!.name)
                               // Log.e("MyLogData" , " ==> " + filePath)
                                val fileName: String? = contentResolver.query(
                                    resultUri!!,
                                    null,
                                    null,
                                    null,
                                    null
                                )?.use { cursor ->
                                    if (cursor.moveToFirst()) {
                                        cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                                    } else {
                                        null
                                    }
                                }

                                if (fileName != null) {
                                    binding.editTextDocument.setText(fileName)
                                    // Use the extracted filename
                                } else {
                                    binding.editTextDocument.text = ""
                                    // Handle cases where the filename cannot be retrieved
                                }

                            } catch (e: IOException) {
                                println("Error creating the file: ${e.message}")
                            }
                          //  uploadFileToGDrive(applicationContext)
                          //  makeCopy(resultUri!!)
                            //uploadToFolder(filePath!!)

                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }

                }*/

                ACTIVITY_CHOOSE_FILE1 -> {

                    if (data != null){
                        try {
                            val resultUri: Uri? = data!!.data
                            Log.e("MyLogData","ACTIVITY_CHOOSE_FILE1  " + resultUri)
                            csvFile = getFileFromUri(this, resultUri)
                            binding.editTextDocument.text=resultUri.toString()
                            isimage = false
                            /*val name = csvFile!!.name
                            Log.e("MyLogData","ACTIVITY_CHOOSE_FILE1 " + name)
                            val extension = name.substring(name.lastIndexOf(".") + 1, name.length)
                            if (extension != "pdf") {
                                showMessage("File must be in pdf format")
                                binding.editTextDocument.text = ""
                                csvFile = null
                            } else {
                                binding.editTextDocument.text = name
                            }*/
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    else {
                        // Handle the case where no file was selected
                        showMessage("No file selected")
                        binding.editTextDocument.text = ""
                    }

                }

               /* ACTIVITY_CHOOSE_FILE1 -> {
                    val uri = data?.data
                    val filePathColumn = arrayOf(MediaStore.MediaColumns.DATA)
                    val cursor = contentResolver.query(uri!!, filePathColumn, null, null, null)
                    cursor?.moveToFirst()
                     filePath = cursor?.getString(filePathColumn.indexOf(MediaStore.MediaColumns.DATA))
                    cursor?.close()
                    csvFile= java.io.File(filePath)

                }*/
            }
        }
    }



    private fun handleSignInIntent(data: Intent?) {
        Log.e("MyLogData","handleSignInIntent")
        GoogleSignIn.getSignedInAccountFromIntent(data)
            .addOnSuccessListener {
                val googleAccountCredential = GoogleAccountCredential.usingOAuth2(this, Collections.singleton(DriveScopes.DRIVE_FILE))
                googleAccountCredential.selectedAccount = it.account

                val googleDriveServices = Drive.Builder(
                    AndroidHttp.newCompatibleTransport(),
                    GsonFactory(),
                    googleAccountCredential
                )

                googleDriveServices.applicationName = "Positron"


                driveServiceHelper = DriveServiceHelper(googleDriveServices.build())
            }
            .addOnFailureListener {
                Log.e("TAG", "Unable to sign in.")
            }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val jsonFactory: JsonFactory = GsonFactory.getDefaultInstance()
            val account = completedTask.getResult(ApiException::class.java)
            val credential = GoogleAccountCredential.usingOAuth2(
                this,
                Collections.singleton(DriveScopes.DRIVE_FILE)
            )
            credential.selectedAccount = account.account
            mDriveService = Drive.Builder(
                NetHttpTransport(),
                jsonFactory,
                credential
            ).setApplicationName("POSITRON Teacher Application").build()
        } catch (e: ApiException) {
            e.printStackTrace()
            //Log.e("MyLogData","handleSignInResult==." + e.message)
            // Handle authentication errors
        }
    }

    private fun uploadFile(file: JavaFile) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val fileContent = FileContent("application/octet-stream", file) // Use appropriate MIME type
                val gfile = File()
                gfile.name = file.name

                val fileMetadata = mDriveService?.files()?.create(gfile, fileContent)?.execute()
                if (fileMetadata != null) {
                    fileMetadata.mimeType = "application/vnd.google-apps.file"
                }
               // Log.d("TAG", "File uploaded: ${fileMetadata?.name}")
            } catch (e: Exception) {
                e.printStackTrace()
              //  Log.e("MyLogData","uploadFile==." + e.message)
                // Handle upload errors
            }catch (e:GoogleJsonResponseException ) {

                System.err.println("Unable to move file: " + e.getDetails());
                throw e
            }
        }
    }

    /*private fun buildDriveClient(googleAccount: GoogleSignInAccount) {
        val credential = GoogleAccountCredential.usingOAuth2(this, Collections.singleton(DriveScopes.DRIVE_FILE))
        credential.selectedAccount = googleAccount.account
        val drive = Drive.Builder(
            AndroidHttp.newCompatibleTransport(), GsonFactory(), credential
        ).setApplicationName("Your App Name").build()
        // Use this drive instance to make API calls
    }*/

    private fun buildDriveClient(googleAccount: GoogleSignInAccount) {
        val credential = GoogleAccountCredential.usingOAuth2(this, Collections.singleton(DriveScopes.DRIVE_FILE))
        credential.selectedAccount = googleAccount.account
        val drive = Drive.Builder(
            AndroidHttp.newCompatibleTransport(), GsonFactory(), credential
        ).setApplicationName(R.string.app_name.toString()).build()

        driveServiceHelper = DriveServiceHelper(drive)

        // Use this drive instance to make API calls
    }


    /*@RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun initializeDriveServiceHelper(accountName: String) {
        val googleApiClient = GoogleApiClient.Builder(this)
            .addApi(com.google.android.gms.drive.Drive.API)
            .addScope(Scope(Drive.SCOPE_FILE))
            .build()

        googleApiClient.connect()

        mDriveServiceHelper = DriveServiceHelper(getDriveService(accountName))
        // Replace 'filePath' with the actual path of the file you want to upload
        uploadFile("filePath")
    }*/


    private fun getDriveService(accountName: String): Drive {
        val transport = AndroidHttp.newCompatibleTransport()
        val jsonFactory: JsonFactory = GsonFactory.getDefaultInstance()

        return Drive.Builder(
            transport,
            jsonFactory,
            GoogleAccountCredential.usingOAuth2(
                applicationContext,
                Collections.singleton(DriveScopes.DRIVE_FILE)
            ).setSelectedAccountName(accountName)
        )
            .setApplicationName("Positron")
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    /*private fun uploadFile(filePath: String) {
        try {
            val fileStream = FileInputStream(filePath)
            val outputStream = driveServiceHelper.createFile("fileName", MIME_TYPE,csvFile!! ,fileStream)
            // Close the streams
            fileStream.close()
            outputStream.close()

            runOnUiThread {
                Toast.makeText(this, "File uploaded successfully", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            runOnUiThread {
                Toast.makeText(this, "Error uploading file", Toast.LENGTH_SHORT).show()
            }
        }
    }*/


    @Throws(IOException::class)

    fun uploadToFolder(realFolderId: String, csvFile: String):com.google.api.services.drive.model.File?  = runBlocking {
        launch(Dispatchers.IO) {
            try {
               // val credential = GoogleCredentials.fromStream(FileInputStream("app/credentials.json"))

               /* val credentials = GoogleCredentials.getApplicationDefault()
                    .createScoped(Arrays.asList(DriveScopes.DRIVE_FILE))
                val requestInitializer: HttpRequestInitializer = HttpCredentialsAdapter(credentials)

                // Build a new authorized API client service.
                val service = Drive.Builder(
                    com.google.api.client.http.javanet.NetHttpTransport(),
                    GsonFactory.getDefaultInstance(),
                    requestInitializer
                )
                    .setApplicationName("Drive samples")
                    .build()*/

                // File's metadata.
                val fileMetadata = File()
                fileMetadata.name = "csvFile"
                fileMetadata.parents = listOf(realFolderId)

                // Provide the correct file path here
                val filePath = java.io.File(filePath)
                val mediaContent = FileContent("*/*", filePath)

                val file = mDrive.files().create(fileMetadata, mediaContent)
                    .setFields("id, parents")
                    .execute()

                println("File ID: " + file.id)

                // You may want to return or handle the file object here

            } catch (e: GoogleJsonResponseException) {
                System.err.println("Unable to upload file: " + e.details)
                // Handle the exception, throw it again, or log it as needed
            } catch (e: NetworkOnMainThreadException) {
                System.err.println("Network operation on main thread: " + e.message)
                // Handle the exception, throw it again, or log it as needed
            }catch (e:java.lang.IllegalArgumentException){
                e.printStackTrace()
            }
        }

        return@runBlocking null
    }


   /* private fun uploadFile(file: File) {
        val fileMetadata = File()
        fileMetadata.name = file.name
        fileMetadata.mimeType = getMimeType(file.path)

        val fileContent = FileContent(fileMetadata.mimeType, file)
        val request = mDriveService.files().create(fileMetadata, fileContent)
        request.execute()

        Toast.makeText(this, "File uploaded successfully", Toast.LENGTH_SHORT).show()
    }*/

    private fun getMimeType(path: String): String {
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExt(path))
        return mimeType ?: "application/octet-stream"
    }

    private fun fileExt(url: String): String {
        return if (url.contains(".")) MimeTypeMap.getFileExtensionFromUrl(url) else ""
    }

    private fun pickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) !== PackageManager.PERMISSION_GRANTED
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                        arrayOf(Manifest.permission.CAMERA),
                        MY_CAMERA_PERMISSION_CODE
                    )
                }
            } else {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (takePictureIntent.resolveActivity(this.packageManager) != null) {
                    var photoFile: java.io.File? = null
                    try {
                        photoFile = createImageFile()
                        imageFile= photoFile
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
                        startActivityForResult(
                            takePictureIntent,
                            ACTIVITY_FOR_RESULT_PICK_IMAGE
                        )
                    }
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): java.io.File? {
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir: java.io.File? =
            getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = java.io.File.createTempFile(
            imageFileName,  /*prefix*/
            ".jpg",  /*suffix*/
            storageDir /*directory*/
        )
        currentPhotoPath = image.absolutePath
        binding.editTextDocument.setText(imageFileName)
        return image
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun galleryOrCameraDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.custom)
        dialog.setTitle("Select Image")
        val camera: LinearLayout = dialog.findViewById<View>(R.id.camera) as LinearLayout
        val gallery: LinearLayout = dialog.findViewById<View>(R.id.gallery) as LinearLayout
        val dialogButton: TextView = dialog.findViewById<View>(R.id.cancel) as TextView

        camera.setOnClickListener {
            pickImage()
            dialog.dismiss()
        }

        gallery.setOnClickListener {
            if (checkStoragePermission(this)) {
                if (checkWriteStoragePermission(this)) {
                    val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
                    intent.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
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

            val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13
                Intent(MediaStore.ACTION_PICK_IMAGES)
            } else { // Android 10 and below
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            }
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

    fun getFileFromUri(context: Context, uri: Uri?): java.io.File? {
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
            databaseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
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
        return if (path.isNullOrEmpty()) null else java.io.File(path)
    }

    private fun getFileName(contentResolver: ContentResolver, fileUri: Uri): String {

        var name = ""
        val returnCursor = contentResolver.query(fileUri, null, null, null, null)
        if (returnCursor != null) {
            val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor.moveToFirst()
            name = returnCursor.getString(nameIndex)
            returnCursor.close()
        }

        return name
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
        startActivity(Intent(this@AddNewRecordActivity, TeacherIdActivity::class.java))
        finish()
        dismissProgressDialog()
    }


    private suspend fun registerApi(token: String) {
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
            .baseUrl("https://oauth2.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        val loginApi = retrofit.create(GoogleOAuthApiService::class.java)

        val call = loginApi.getAccessToken(
           "611992607122-0l3bfi3n1uovfn0obavb9437uj92rvlj.apps.googleusercontent.com",""
        )

        call.enqueue(object : Callback<AccessTokenResponse> {
            override fun onResponse(
                call: Call<AccessTokenResponse>,
                response: Response<AccessTokenResponse>
            ) {
                if (response.isSuccessful) {

                } else {
                    dismissProgressDialog()
                    showMessage(" Something went wrong : ${response.code()} \n NULL")
                }
            }

            override fun onFailure(call: Call<AccessTokenResponse>, t: Throwable) {
                dismissProgressDialog()
              //  Log.e("MyResponse", " failure registerApi Error ==> $t.message")
                showMessage("Something went wrong : ${t.message.toString()} \n onFailure")
            }
        })
    }


    //private  val originalString = prefs.getErpUrl().toString()
     var postfixToRemove = "api_teachers/"

    private fun removePostfix(originalString: String, postfixToRemove: String): String {
        if (originalString.endsWith(postfixToRemove)) {
            return originalString.substring(0, originalString.length - postfixToRemove.length)
        } else {
            return originalString
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveStudyMaterials() {
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
            .baseUrl(prefs.getErpUrl().toString())//prefs.getErpUrl().toString()
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        val loginApi = retrofit.create(ApiClass::class.java)

        /*val call = loginApi.SaveStudyMaterials(prefs.getClassTeacherClassId().toString(),prefs.getTeacherClassSectionId().toString(),prefs.getSubjectId().toString()
           ,prefs.getTeacherId().toString(),
            csvFile!!,binding.editTextTITLE.text.toString()
        )*/
        var multipartBody: MultipartBody.Part? = null

        if (isimage == false){
            if (csvFile != null) {
                Log.e("MyLogData", "saveStudyMaterials " + csvFile!!.exists())
                val requestFile: RequestBody =
                    csvFile!!.asRequestBody("application/pdf".toMediaTypeOrNull())
                multipartBody =
                    MultipartBody.Part.createFormData("attachment_file", csvFile!!.name, requestFile)
            }
            }
        else{

        if (imageFile != null) {
            val requestFile: RequestBody = imageFile!!.asRequestBody("*/*".toMediaTypeOrNull())
            multipartBody = MultipartBody.Part.createFormData("attachment_file", imageFile!!.name, requestFile)
        }
        }

      /*  val call = loginApi.addComplain(prefs.getClassTeacherClassId().toString(),prefs.getTeacherClassSectionId().toString(),
            prefs.getSubjectId().toString(),prefs.getTeacherId().toString(),binding.editTextTITLE.text.toString(),
            multipartBody)*/

        val newString = removePostfix(prefs.getErpUrl().toString(), postfixToRemove)
        Log.e("MyLogData","base url " + newString)

        val titleRequestBody = binding.editTextTITLE.text.toString().toRequestBody("text/plain".toMediaType())
        val descriptionRequestBody = binding.editTextDescription.text.toString().toRequestBody("text/plain".toMediaType())

        val call = loginApi.uploadStudyMaterial("Bearer ${prefs.getAuthorizationToken().toString()}" ,prefs.getSubjectTeacherClassId().toString().toInt(),prefs.getSubjectTeacherSectionId().toString().toInt(),
            prefs.getSubjectId().toString().toInt(),titleRequestBody, descriptionRequestBody,
            multipartBody)


        //Log.e("MyLogData","base url " + prefs.getErpUrl().toString())

        /*Log.e("MyLogData", " Param ==> " + prefs.getSubjectTeacherClassId().toString() + prefs.getTeacherClassSectionId().toString() +
            prefs.getSubjectId().toString() + prefs.getTeacherId().toString() + binding.editTextTITLE.text.toString())*/


        /*Log.e("MyLogData","saveStudyMaterials param ===  " +prefs.getClassTeacherClassId().toString() + prefs.getTeacherClassSectionId().toString()
             +prefs.getSubjectId().toString()
            + prefs.getTeacherId().toString())*/

        call.enqueue(object : Callback<SaveStudyMaterials> {
            override fun onResponse(
                call: Call<SaveStudyMaterials>,
                response: Response<SaveStudyMaterials>
            ) {
                if (response.isSuccessful) {
                    showMessage("Record Saved")
                   // Log.e("MyLogData","if no upar" + response.body())
                  //  Log.e("MyLogData","if no upar" + response.body()!!.result)
                    /*if(response.body()?.response?.equals("Data Posted") == true){*/
                        dismissProgressDialog()
                        showMessage(response.body()?.response)
                        binding.editTextDocument.text=""
                        binding.editTextTITLE.text.clear()
                        binding.editTextDescription.text.clear()
                        binding.ivImage.setImageResource(R.drawable.ic_gallary)
                        binding.editTextSection.text.clear()
                        binding.editTextSubject.text.clear()
                        binding.editTextClass.text.clear()
                        csvFile=null
                        imageFile=null
                        prefs.setClassTeacherClassName("")
                        prefs.setTeacherClassSectionName("")
                        prefs.setSubjectName("")
                        prefs.setClassTeacherClassId("")
                        prefs.setTeacherClassSectionId("")
                        prefs.setSubjectId("")
                  /*  }else{
                        dismissProgressDialog()
                        showMessage("Record Not Saved")
                    }*/
                } else {
                    dismissProgressDialog()
                    showMessage(" Something went wrong : ${response.code()} \n NULL")
                }
            }

            override fun onFailure(call: Call<SaveStudyMaterials>, t: Throwable) {
                dismissProgressDialog()
                Log.e("MyLogData", " failure registerApi Error ==> ${t.message}")
                showMessage("Something went wrong : ${t.message.toString()} \n onFailure")
            }
        })




    }





}
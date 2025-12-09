package com.positron.teachers.activity

import GetSubCategorySubject
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.SearchView

import com.positron.teachers.adapters.DestinationCountryAdapter
import com.positron.teachers.R
import com.positron.teachers.api.ApiClass
import com.positron.teachers.databinding.ActivityDialogBinding
import com.positron.teachers.model.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class DialogActivity : BaseActivity(),  View.OnClickListener, DestinationCountryAdapter.FilterSelectionInterface {

    private lateinit var binding: ActivityDialogBinding

    private var destinationCountryAdapter: DestinationCountryAdapter? = null
    private var classDataList: MutableList<Teacherclass> = mutableListOf()
    private var getClassTeacherclassList: MutableList<getClassTeacherClass> = mutableListOf()
    private var getClassTeacherSectionList: MutableList<getClassTeacherSection> = mutableListOf()
    private var getSubjectTeacherclassList: MutableList<getSubjectTeacherclass> = mutableListOf()
    private var getSubjectTeacherSectionList: MutableList<getSubjectTeacherSection> = mutableListOf()

    private var getExamList: MutableList<GetExamList> = mutableListOf()
    private var getSubjectList: MutableList<GetSubjects> = mutableListOf()
    private var getSubjectListNew: MutableList<GetSubjectsNew> = mutableListOf()
    private var getFormatList: MutableList<FormatData> = mutableListOf()
    private var getSubSubjectList: MutableList<GetSubCategorySubject> = mutableListOf()

    private var commonList: MutableList<String> = mutableListOf()
    var type: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)



        type = intent.getIntExtra("type", 0)
        initializeViews()
    }

    override fun onClick(v: View?) {
        val viewid = v?.id

        if (viewid == R.id.iv_close){
            finish()
        }
    }

    private fun getTeacherClass(search:String) {
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

        val call = loginApi.getTeacherClass("Bearer ${prefs.getAuthorizationToken().toString()}")

        call.enqueue(object : Callback<List<Teacherclass>> {
            override fun onResponse(
                call: Call<List<Teacherclass>>,
                response: Response<List<Teacherclass>>
            ) {
                Log.e("MyLogData","if upar")
                    if (response.isSuccessful) {
                        Log.e("MyLogData","if " + response.body())
                    classDataList.clear()
                    commonList.clear()
                    val data = response.body()
                    Log.e("MyLogData" ,"getTeacherClass ===== " + data.toString())
                    classDataList = response.body() as MutableList<Teacherclass>



                    if (classDataList.isEmpty()) {

                        showMessage("No Class Found")

                    } else {

                       // classDataList.addAll(response.body()?)
                        for (item in classDataList.map { it.class_name }) {
                            commonList.add(item)
                        }

                        destinationCountryAdapter = DestinationCountryAdapter(
                            this@DialogActivity,
                            commonList,
                            this@DialogActivity
                        )
                        binding.recyclerViewList.adapter = destinationCountryAdapter

                    }

                    val matchingItem = classDataList.find { it.class_name == search }

                    if (matchingItem != null) {
                        // Found the item
                    } else {
                        // Item not found
                    }
                    dismissProgressDialog()
                } else {
                    dismissProgressDialog()
                        Log.e("MyLogData","if " + response.body())
                  //  showMessage("Something went wrong : ${response.code()} \n NULL")
                }
            }

            override fun onFailure(call: Call<List<Teacherclass>>, t: Throwable) {
                dismissProgressDialog()
                  Log.e("MyLogData", "getTeacherClass ==> $t.message")
                showMessage("Something went wrong : ${t.message.toString()} \n onFailure")
            }
        })
    }

    private fun getClassTeacherSection(search:String) {
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

        val call = loginApi.getClassTeacherSection("Bearer ${prefs.getAuthorizationToken().toString()}",prefs.getClassTeacherClassId().toString())

        call.enqueue(object : Callback<List<getClassTeacherSection>> {
            override fun onResponse(
                call: Call<List<getClassTeacherSection>>,
                response: Response<List<getClassTeacherSection>>
            ) {

                if (response.isSuccessful) {
                    getClassTeacherSectionList.clear()
                    commonList.clear()
                    val data = response.body()
                    Log.e("MyLogData" ,"getClassTeacherSection ===== " + data.toString())
                    getClassTeacherSectionList = response.body() as MutableList<getClassTeacherSection>
                    if (getClassTeacherSectionList.isEmpty()) {
                        showMessage("No Section Found")

                    } else {
                        //Log.e("MyLogData","else" + getClassTeacherSectionList.get(0).section)

                        // classDataList.addAll(response.body()?)
                        for (item in getClassTeacherSectionList.map { it.section }) {
                            commonList.add(item!!)
                        }

                        destinationCountryAdapter = DestinationCountryAdapter(
                            this@DialogActivity,
                            commonList,
                            this@DialogActivity
                        )
                        binding.recyclerViewList.adapter = destinationCountryAdapter

                    }
                    val matchingItem = getClassTeacherSectionList.find { it.section == search }
                    if (matchingItem != null) {
                        // Found the item
                    } else {
                        // Item not found
                    }
                    dismissProgressDialog()
                } else {
                    dismissProgressDialog()
                    showMessage("Something went wrong : ${response.code()} \n NULL")
                }
            }

            override fun onFailure(call: Call<List<getClassTeacherSection>>, t: Throwable) {
                dismissProgressDialog()
                //  Log.e("MyResponse", " failure registerApi Error ==> $t.message")
                showMessage("Something went wrong : ${t.message.toString()} \n onFailure")
            }
        })
    }

    private fun getSubjectTeacherClass(search:String) {
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

        val call = loginApi.getSubjectTeacherClass("Bearer ${prefs.getAuthorizationToken().toString()}")

        call.enqueue(object : Callback<List<getSubjectTeacherclass>> {
            override fun onResponse(
                call: Call<List<getSubjectTeacherclass>>,
                response: Response<List<getSubjectTeacherclass>>
            ) {
                Log.e("MyLogData" ,"getSubjectTeacherClass ===== " + response.body().toString())
                if (response.isSuccessful) {
                    getSubjectTeacherclassList.clear()
                    commonList.clear()
                    val data = response.body()
                    Log.e("MyLogData" ,"getSubjectTeacherClass ===== " + data.toString())
                    getSubjectTeacherclassList = response.body() as MutableList<getSubjectTeacherclass>



                    if (getSubjectTeacherclassList.isEmpty()) {

                        showMessage("No Class Found")

                    } else {

                        // classDataList.addAll(response.body()?)
                        for (item in getSubjectTeacherclassList.map { it.class_name }) {
                            commonList.add(item!!)
                        }

                        destinationCountryAdapter = DestinationCountryAdapter(
                            this@DialogActivity,
                            commonList,
                            this@DialogActivity
                        )
                        binding.recyclerViewList.adapter = destinationCountryAdapter

                    }

                    val matchingItem = getSubjectTeacherclassList.find { it.class_name == search }

                    if (matchingItem != null) {
                        // Found the item
                    } else {
                        // Item not found
                    }
                    dismissProgressDialog()
                } else {
                    dismissProgressDialog()
                    showMessage("Something went wrong : ${response.code()} \n NULL")
                }
            }

            override fun onFailure(call: Call<List<getSubjectTeacherclass>>, t: Throwable) {
                dismissProgressDialog()
                //  Log.e("MyResponse", " failure registerApi Error ==> $t.message")
                showMessage("Something went wrong : ${t.message.toString()} \n onFailure")
            }
        })
    }

    private fun getSubjectTeacherSection(search:String) {
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

        val call = loginApi.getSubjectTeacherSection("Bearer ${prefs.getAuthorizationToken().toString()}",prefs.getSubjectTeacherClassId().toString())

        call.enqueue(object : Callback<List<getSubjectTeacherSection>> {
            override fun onResponse(
                call: Call<List<getSubjectTeacherSection>>,
                response: Response<List<getSubjectTeacherSection>>
            ) {

                if (response.isSuccessful) {
                    getSubjectTeacherSectionList.clear()
                    commonList.clear()
                    val data = response.body()
                    Log.e("MyLogData" ,"getSubjectTeacherSection ===== " + data.toString())
                    getSubjectTeacherSectionList = response.body() as MutableList<getSubjectTeacherSection>
                    if (getSubjectTeacherSectionList.isEmpty()) {

                        showMessage("No Section Found")
                    } else {
                        //Log.e("MyLogData","else" + getClassTeacherSectionList.get(0).section)

                        // classDataList.addAll(response.body()?)
                        for (item in getSubjectTeacherSectionList.map { it.section }) {
                            commonList.add(item!!)
                        }

                        destinationCountryAdapter = DestinationCountryAdapter(
                            this@DialogActivity,
                            commonList,
                            this@DialogActivity
                        )
                        binding.recyclerViewList.adapter = destinationCountryAdapter

                    }
                    val matchingItem = getSubjectTeacherSectionList.find { it.section == search }
                    if (matchingItem != null) {
                        // Found the item
                    } else {
                        // Item not found
                    }
                    dismissProgressDialog()
                } else {
                    dismissProgressDialog()
                    showMessage("Something went wrong : ${response.code()} \n NULL")
                }
            }

            override fun onFailure(call: Call<List<getSubjectTeacherSection>>, t: Throwable) {
                dismissProgressDialog()
                //  Log.e("MyResponse", " failure registerApi Error ==> $t.message")
                showMessage("Something went wrong : ${t.message.toString()} \n onFailure")
            }
        })
    }

    private fun getExam(search:String) {
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

        val call = loginApi.getExamList("Bearer ${prefs.getAuthorizationToken().toString()}" , prefs.getSubjectTeacherClassId().toString(),)

        call.enqueue(object : Callback<List<GetExamList>> {
            override fun onResponse(
                call: Call<List<GetExamList>>,
                response: Response<List<GetExamList>>
            ) {

                if (response.isSuccessful) {
                    getExamList.clear()
                    commonList.clear()
                    val data = response.body()
                    Log.e("MyLogData" ," ===== " + data.toString())
                    getExamList = response.body() as MutableList<GetExamList>



                    if (getExamList.isEmpty()) {

                        showMessage("No Exam Found")

                    } else {

                        // classDataList.addAll(response.body()?)
                        for (item in getExamList.map { it.title }) {
                            commonList.add(item!!)
                        }

                        destinationCountryAdapter = DestinationCountryAdapter(
                            this@DialogActivity,
                            commonList,
                            this@DialogActivity
                        )
                        binding.recyclerViewList.adapter = destinationCountryAdapter

                    }

                    val matchingItem = getExamList.find { it.title == search }

                    if (matchingItem != null) {
                        // Found the item
                    } else {
                        // Item not found
                    }
                    dismissProgressDialog()
                } else {
                    dismissProgressDialog()
                    showMessage("Something went wrong : ${response.code()} \n NULL")
                }
            }

            override fun onFailure(call: Call<List<GetExamList>>, t: Throwable) {
                dismissProgressDialog()
                //  Log.e("MyResponse", " failure registerApi Error ==> $t.message")
                showMessage("Something went wrong : ${t.message.toString()} \n onFailure")
            }
        })
    }

    private fun getSubjects(search:String) {
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
       // Log.e("MyLogData" , "getSubjects param ==== "+ prefs.getClassTeacherClassId().toString() + " " +prefs.getTeacherClassSectionId().toString())
        val call = loginApi.getSubjects("Bearer ${prefs.getAuthorizationToken().toString()}" , prefs.getSubjectTeacherClassId().toString(),prefs.getSubjectTeacherSectionId().toString(),/* prefs.getTeacherId().toString(),prefs.getExamId().toString(),prefs.getSubjectTeacherClassId().toString(),prefs.getSubjectTeacherSectionId().toString()*/)
       // Log.e("MyLogData" , "getSubjects param ==== " + prefs.getClassTeacherClassId().toString() + " " +prefs.getTeacherClassSectionId().toString())
        call.enqueue(object : Callback<List<GetSubjects>> {
            override fun onResponse(
                call: Call<List<GetSubjects>>,
                response: Response<List<GetSubjects>>
            ) {

                if (response.isSuccessful) {
                    getSubjectList.clear()
                    commonList.clear()
                    val data = response.body()
                    Log.e("MyLogData" ," ===== " + data.toString())
                    getSubjectList = response.body() as MutableList<GetSubjects>



                    if (getSubjectList.isEmpty()) {

                        showMessage("No Subject Found")

                    } else {

                        // classDataList.addAll(response.body()?)
                        for (item in getSubjectList.map { it.name }) {
                            commonList.add(item!!)
                        }

                        destinationCountryAdapter = DestinationCountryAdapter(
                            this@DialogActivity,
                            commonList,
                            this@DialogActivity
                        )
                        binding.recyclerViewList.adapter = destinationCountryAdapter

                    }

                    val matchingItem = getSubjectList.find { it.name == search }

                    if (matchingItem != null) {
                        // Found the item
                    } else {
                        // Item not found
                    }
                    dismissProgressDialog()
                } else {
                    dismissProgressDialog()
                    showMessage("Something went wrong : ${response.code()} \n NULL")
                }
            }

            override fun onFailure(call: Call<List<GetSubjects>>, t: Throwable) {
                dismissProgressDialog()
                //  Log.e("MyResponse", " failure registerApi Error ==> $t.message")
                showMessage("Something went wrong : ${t.message.toString()} \n onFailure")
            }
        })
    }

    private fun getSubjectsGeneral(search:String) {
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

        val call = loginApi.getSubjectsGeneral("Bearer ${prefs.getAuthorizationToken().toString()}",prefs.getSubjectTeacherClassId().toString(),prefs.getSubjectTeacherSectionId().toString())

        call.enqueue(object : Callback<List<GetSubjectsNew>> {
            override fun onResponse(
                call: Call<List<GetSubjectsNew>>,
                response: Response<List<GetSubjectsNew>>
            ) {

                if (response.isSuccessful) {
                    getSubjectListNew.clear()
                    commonList.clear()
                    val data = response.body()
                    Log.e("MyLogData" ," ===== " + data.toString())
                    getSubjectListNew = response.body() as MutableList<GetSubjectsNew>



                    if (getSubjectListNew.isEmpty()) {

                        showMessage("No Subject Found")

                    } else {

                        // classDataList.addAll(response.body()?)
                        for (item in getSubjectListNew.map { it.subject_name }) {
                            commonList.add(item!!)
                        }

                        destinationCountryAdapter = DestinationCountryAdapter(
                            this@DialogActivity,
                            commonList,
                            this@DialogActivity
                        )
                        binding.recyclerViewList.adapter = destinationCountryAdapter

                    }

                    val matchingItem = getSubjectListNew.find { it.subject_name == search }

                    if (matchingItem != null) {
                        // Found the item
                      //  println("Found item: $matchingItem")
                       // commonList.add(matchingItem.toString())
                     //   Log.e("MyLogData","commonList " + matchingItem)
                    } else {
                      //  println("Item not found.")
                        // Item not found
                    }
                    dismissProgressDialog()
                } else {
                    dismissProgressDialog()
                    showMessage("Something went wrong : ${response.code()} \n NULL")
                }
            }

            override fun onFailure(call: Call<List<GetSubjectsNew>>, t: Throwable) {
                dismissProgressDialog()
                //  Log.e("MyResponse", " failure registerApi Error ==> $t.message")
                showMessage("Something went wrong : ${t.message.toString()} \n onFailure")
            }
        })
    }

    private fun getFormat(search:String) {
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

        val call = loginApi.getFormat("Bearer ${prefs.getAuthorizationToken().toString()}" , prefs.getClassTeacherClassId().toString(),prefs.getTeacherClassSectionId().toString())
        //Log.e("MyLogData"," dssds" + prefs.getClassTeacherClassId().toString()  +  prefs.getTeacherClassSectionId().toString() )

        call.enqueue(object : Callback<getFormat> {
            override fun onResponse(
                call: Call<getFormat>,
                response: Response<getFormat>
            ) {

                if (response.isSuccessful) {
                    getFormatList.clear()
                    commonList.clear()
                    val data = response.body()
                    Log.e("MyLogData" ," ===== " + data.toString())
                    getFormatList = response.body()!!.format as MutableList<FormatData>



                    if (getFormatList.isEmpty()) {

                        showMessage("No Exam Found")

                    } else {

                        // classDataList.addAll(response.body()?)
                        for (item in getFormatList.map { it.name }) {
                            commonList.add(item!!)
                        }

                        destinationCountryAdapter = DestinationCountryAdapter(
                            this@DialogActivity,
                            commonList,
                            this@DialogActivity
                        )
                        binding.recyclerViewList.adapter = destinationCountryAdapter

                    }

                    val matchingItem = getFormatList.find { it.name == search }

                    if (matchingItem != null) {
                        // Found the item
                    } else {
                        // Item not found
                    }
                    dismissProgressDialog()
                } else {
                    dismissProgressDialog()
                    showMessage("Something went wrong : ${response.code()} \n NULL")
                }
            }

            override fun onFailure(call: Call<getFormat>, t: Throwable) {
                dismissProgressDialog()
                //  Log.e("MyResponse", " failure registerApi Error ==> $t.message")
                showMessage("Something went wrong : ${t.message.toString()} \n onFailure")
            }
        })
    }

    private fun getSubSubject(search: String) {
        showProgressDialog()

        // Clear previous data
        getSubSubjectList.clear()
        commonList.clear()

        // Static entries
        getSubSubjectList = mutableListOf(
            GetSubCategorySubject("Third Language"),
            GetSubCategorySubject("Second Language"),
        )


        // Fill names into commonList
        for (item in getSubSubjectList.map { it.categorySubjectName }) {
            commonList.add((item ?: "").toString())
        }

        // Set adapter
        destinationCountryAdapter = DestinationCountryAdapter(
            this@DialogActivity,
            commonList,
            this@DialogActivity
        )
        binding.recyclerViewList.adapter = destinationCountryAdapter

        // Optional: Find selected item
        val matchingItem = getSubSubjectList.find { it.categorySubjectName == search }
        if (matchingItem != null) {
            // Found match
        } else {
            // No match found
        }

        dismissProgressDialog()
    }


    private fun initializeViews() {

        if (type == 1) {
            title = "Class"
            getTeacherClass("")
        } else if (type == 2) {
            title = "Section"
            getClassTeacherSection("")
        } else if (type == 3) {
            title = "Class"
            getSubjectTeacherClass("")
        }else if (type == 4) {
            title = "Section"
            getSubjectTeacherSection("")
        }else if (type == 5) {
            title = "Exam"
            getExam("")
        }else if (type == 6) {
            title = "Subject"
            getSubjects("")
        }
        else if (type == 7) {
            title = "Subject"
            getSubjectsGeneral("")
        }
        else if (type == 8) {
            title = "Format"
            getFormat("")
        } else if (type == 9) {
            title = "Sub Category Subject"
            getSubSubject("")
        }

        binding.ivClose.setOnClickListener(this)

        binding.searchViewList.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (type == 1) {
                    getTeacherClass(newText!!)
                } else if (type == 2) {
                    getClassTeacherSection(newText!!)
                } else if (type == 3) {
                    getSubjectTeacherClass(newText!!)
                }
                else if (type == 4) {
                    getSubjectTeacherSection(newText!!)
                }
                else if (type == 5) {
                    getExam(newText!!)
                }
                else if (type == 6) {
                    getSubjects(newText!!)
                }
                else if (type == 7) {
                    getSubjectsGeneral(newText!!)
                }
                else if (type == 8) {
                    getFormat(newText!!)
                } else if (type == 9) {
                    getSubSubject(newText!!)
                }
                return false
            }
        })
    }

    override fun onSelected(item: String?) {
        if (type == 1) {
            classDataList.forEach {
                if (it.class_name == item.toString()) {
                    prefs.setClassTeacherClassName(item)
                    prefs.setClassTeacherClassId(it.class_id)
                }
            }
            finish()
        } else if (type == 2) {
            getClassTeacherSectionList.forEach {
                if (it.section == item.toString()) {
                    prefs.setTeacherClassSectionName(item)
                    prefs.setTeacherClassSectionId(it.id)
                }
            }
            finish()
        } else if (type == 3) {
            getSubjectTeacherclassList.forEach {
                if (it.class_name == item.toString()) {
                    prefs.setSubjectTeacherClassName(item)
                    prefs.setSubjectTeacherClassId(it.class_id)
                }
            }
            finish()
        } else if (type == 4) {
            getSubjectTeacherSectionList.forEach {
                if (it.section == item.toString()) {
                    prefs.setSubjectTeacherSectionName(item)
                    //prefs.setSubjectTeacherSectionId(it.section_id)
                    prefs.setSubjectTeacherSectionId(it.id)
                }
            }
            finish()
        } else if (type == 5) {
            getExamList.forEach {
                if (it.title == item.toString()) {
                    prefs.setExamName(item)
                    prefs.setExamId(it.id)
                }
            }
            finish()
        } else if (type == 6) {
            getSubjectList.forEach {
                if (it.name == item.toString()) {
                    prefs.setSubjectName(item)
                    prefs.setSubjectId(it.id)
                    prefs.setSubjectCategoryId(it.category_id.toString())
                    Log.d(
                        "MyLogData",
                        "Subject selected: ${item}, ID = ${it.id}, CategoryID = ${it.category_id}"
                    )
                }
            }
            finish()
        } else if (type == 7) {
            getSubjectListNew.forEach {
                if (it.subject_name == item.toString()) {
                    prefs.setSubjectName(item)
                    prefs.setSubjectId(it.subject_id)
                }
            }
            finish()
        } else if (type == 8) {
            getFormatList.forEach {
                if (it.name == item.toString()) {
                    prefs.setFormatName(item)
                    prefs.setFormatId(it.id.toString())
                    prefs.setSubSubjectCategoryName("")
                }
            }
            finish()
        }

        if (type == 9) {
            getSubSubjectList.forEach {
                if (it.categorySubjectName == item.toString()) {
                    prefs.setSubSubjectCategoryName(item)
                }
            }
            finish()

        }
    }
}
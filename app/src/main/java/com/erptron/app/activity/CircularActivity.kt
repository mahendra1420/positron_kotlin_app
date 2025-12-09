package com.positron.teachers.activity

import android.os.Bundle
import android.view.View
import com.positron.teachers.adapters.CircularAdapter
import com.positron.teachers.R
import com.positron.teachers.api.ApiClass
import com.positron.teachers.databinding.ActivityCircularBinding
import com.positron.teachers.model.Circular
import com.positron.teachers.model.CircularUpdates
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class CircularActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityCircularBinding
    private var circularDataList: MutableList<CircularUpdates> = mutableListOf()
    private var circularAdapter: CircularAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_circular)
        binding = ActivityCircularBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnBack.setOnClickListener(this)
        if(isOnline()){

            getNoticeData()
        }
    }

    override fun onClick(v: View?) {
        val viewId = v?.id
       if (viewId == R.id.btnBack) {
            onBackPressed()
        }
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

        val call = loginApi.getCircularData()

        call.enqueue(object : Callback<Circular<List<CircularUpdates>>> {
            override fun onResponse(
                call: Call<Circular<List<CircularUpdates>>>,
                response: Response<Circular<List<CircularUpdates>>>
            ) {
                if (response.isSuccessful) {
                    dismissProgressDialog()
                    circularDataList.clear()
                    circularDataList = response.body()?.circular_updates as MutableList<CircularUpdates>
                    if (circularDataList.isEmpty()) {

                        binding.rvNoticeBoard.visibility = View.GONE
                        binding.noDataLayout.visibility = View.VISIBLE

                    } else {

                        binding.rvNoticeBoard.visibility = View.VISIBLE
                        binding.noDataLayout.visibility = View.GONE


                        circularAdapter =
                            CircularAdapter(this@CircularActivity, circularDataList)
                        binding.rvNoticeBoard.adapter = circularAdapter
                    }
                } else {
                    dismissProgressDialog()
                    showMessage(" Something went wrong : ${response.code()} \n NULL")
                }
            }

            override fun onFailure(call: Call<Circular<List<CircularUpdates>>>, t: Throwable) {
                dismissProgressDialog()
               // Log.e("MyResponse", " failure registerApi Error ==> $t.message")
                showMessage("Something went wrong : ${t.message.toString()} \n onFailure")
            }
        })
    }
}
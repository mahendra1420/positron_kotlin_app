package com.positron.teachers.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.positron.teachers.R
import com.positron.teachers.databinding.ActivityHomePageBinding

class HomePageActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityHomePageBinding
    //Steel Carmel DGP
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cardViewNotice.setOnClickListener(this)
        binding.cardViewAttendance.setOnClickListener(this)
        binding.cardViewResult.setOnClickListener(this)
        binding.cardViewNotice.setOnClickListener(this)
        binding.cardViewFee.setOnClickListener(this)
        binding.cardViewExam.setOnClickListener(this)
        binding.cardViewStudy.setOnClickListener(this)
        binding.cardViewPernotice.setOnClickListener(this)
        binding.cardViewFeerecept.setOnClickListener(this)
        binding.ivLogout.setOnClickListener(this)

        var imageUrl = "https://carmelsteeldgp2.cloudsoftware.website/" // Replace with your image URL


        Glide.with(this)
            .load(prefs.getImage().toString())
            .apply(RequestOptions())
            .placeholder(R.drawable.logonew)
            .error(R.drawable.logonew)
            .into(binding.profileImage)

    }

    override fun onClick(v: View?) {
        val viewId = v?.id

        if (viewId == R.id.card_view_notice){
            startActivity(Intent(this@HomePageActivity, NoticeActivity::class.java))
        }else if (viewId == R.id.card_view_pernotice){
            startActivity(Intent(this@HomePageActivity, PersonalNoticeActivity::class.java))
        }
        else if (viewId == R.id.card_view_attendance){
            //showMessage("Coming Soon....")
            startActivity(Intent(this@HomePageActivity, AttendanceActivity::class.java))
        } else if(viewId == R.id.card_view_study){
            //showMessage("Coming Soon....")
            startActivity(Intent(this@HomePageActivity, StudyMaterialActivity::class.java))
        }
        else if(viewId == R.id.card_view_exam){
            //showMessage("Coming Soon....")
            startActivity(Intent(this@HomePageActivity, ExamActivity::class.java))
        }
        else if(viewId == R.id.card_view_result){
            //showMessage("Coming Soon....")
            startActivity(Intent(this@HomePageActivity, ResultActivity::class.java))
        }
        else if(viewId == R.id.card_view_fee){
            /*val webLink = "https://www.onlinesbi.sbi/sbicollect/icollecthome.htm?corpID=2043556"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(webLink))
            startActivity(intent)*/
            showMessage("Coming Soon....")

        }
        else if(viewId == R.id.card_view_feerecept){
            showMessage("Coming Soon....")
            // startActivity(Intent(this@HomePageActivity, FeePaymentHistoryActivity::class.java))
        }
        else if (viewId == R.id.iv_logout) {
            //logOut()
            showLogoutConfirmationDialog()
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
        startActivity(Intent(this@HomePageActivity, RegisterActivity::class.java))
        finish()
        dismissProgressDialog()
    }

}
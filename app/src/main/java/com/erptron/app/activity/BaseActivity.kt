package com.positron.teachers.activity

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.snackbar.Snackbar
import com.positron.teachers.R
import com.positron.teachers.util.ApplicationPrefs
import com.positron.teachers.util.MyProgressBar

open class BaseActivity : AppCompatActivity() {

    private var TAG = "BaseActivity"
    private lateinit var context: Context
    private lateinit var viewContext: Context
    lateinit var pDialog: MyProgressBar
    private lateinit var snackbar: Snackbar
    var folderName = "Steel Carmel DGP"
    lateinit var prefs: ApplicationPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TAG = this::class.toString()
        context = applicationContext
        prefs = ApplicationPrefs(context)
        initProgressDialog()
        folderName = getString(R.string.app_name)
        viewContext = this
    }

    private var doubleBackToExitPressedOnce = false

    fun askForExit() {
        if (doubleBackToExitPressedOnce) {
            setResult(RESULT_CANCELED)
            finishAffinity()
            return
        }
        doubleBackToExitPressedOnce = true
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    /** USE FOR CHANGE STATUS BAR COLOR*/
    @SuppressLint("NewApi")
    fun GetWindowStatusBarColor(activity: AppCompatActivity, color: Int) {
        val window: Window = activity.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(activity, color)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
    }

    private fun initProgressDialog() {
        pDialog = MyProgressBar(this)
        pDialog.setCancelable(false)
    }

    fun showProgressDialog() {
        //if (!pDialog.isShowing) pDialog.show()
        if (!isFinishing && !pDialog.isShowing) {
            pDialog.show()
        }
    }

    fun dismissProgressDialog() {
        if (!isFinishing) {
        if (pDialog.isShowing) pDialog.dismiss()}
    }

    fun showMessage(msg: String?) {
        if (msg != null) Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    protected fun hashData(text: String?): Boolean {
        return text != null && text != ""
    }

    fun isEmpty(text: String?): Boolean {
        return text == null || text.trim { it <= ' ' }.isEmpty()
    }

    fun isNotEmpty(text: String?): Boolean {
        return !isEmpty(text)
    }

    fun showSnackBar(msg: String) {
        if (msg != null)
            snackbar = Snackbar.make(View(context), msg, Snackbar.LENGTH_SHORT)
    }
    fun isOnline(): Boolean {
        val conMgr =
            applicationContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = conMgr.activeNetworkInfo
        if (netInfo == null || !netInfo.isConnected || !netInfo.isAvailable) {
            val snackbar = Snackbar.make(getRootView()!!, "You're Offline", Snackbar.LENGTH_LONG)
            val textView = snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
            textView.setTextColor(resources.getColor(R.color.white))
            textView.setTypeface(ResourcesCompat.getFont(this, R.font.roboto_regular))
            textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
            snackbar.view.setBackgroundColor(resources.getColor(R.color.primary))
            hideKeyBoard()
            snackbar.show()
            return false
        }
        return true
    }
    fun hideKeyBoard() {
        try {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            if (imm != null) {
                val view = currentFocus
                if (view != null) imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        } catch (ignored: Exception) {
        }
    }
    private fun getRootView(): View? {
        val contentViewGroup = findViewById<ViewGroup>(android.R.id.content)
        var rootView: View? = null
        if (contentViewGroup != null) rootView = contentViewGroup.getChildAt(0)
        if (rootView == null) rootView = window.decorView.rootView
        return rootView
    }

    fun downloadFileFromUrl(url: String, fileName: String) {

        val request = DownloadManager.Request(Uri.parse(url))
        request.setTitle(fileName)
        request.setMimeType("application/pdf")
        request.allowScanningByMediaScanner()
        request.setAllowedOverMetered(true)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            "Steel Carmel DGP/$fileName.pdf"
        )
        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }

    fun openFileFromUrl(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

}
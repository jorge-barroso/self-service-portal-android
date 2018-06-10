package com.premfina.esig.selfserviceportal

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.Log
import android.view.View
import kotlin.concurrent.thread
import kotlinx.android.synthetic.main.activity_document_details.*


class DocumentDetailsActivity : Drawer() {

    private lateinit var agreementUrl: String
    private lateinit var agreementNumber: String
    private lateinit var tabsView: MyCustomTabsView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addToFrame(R.layout.activity_document_details)

        val intent = intent
        agreementNumber = intent.getStringExtra("agreement")
        agreementUrl = intent.getStringExtra("agreement_url")

        agreement_number_view.text = agreementNumber
        if(TextUtils.isEmpty(agreementUrl))
            download_button.isEnabled = false
        else
        {
            thread {
                tabsView = MyCustomTabsView(this, agreementUrl)
            }
        }
    }

    fun viewAgreement(view: View)
    {
        tabsView.show()
    }

    fun downloadAgreement(view: View)
    {
        //val file = File(this@DocumentDetailsActivity.filesDir, agreementUrl)
        Log.w("DOWNLOAD", "Clicked!")
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this@DocumentDetailsActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            Log.w("DOWNLOAD", "Requesting permissions")
            ActivityCompat.requestPermissions(this@DocumentDetailsActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PermissionsRequests.REQUEST_WRITE_EXTERNAL_STORAGE_REQUEST)
        }
        else
        {
            Log.w("DOWNLOAD", "No need to request permissions")
            downloadFile()
        }

        Log.w("DOWNLOAD", "After if statements")
    }

    private fun emailAgreement(view: View)
    {
        //TODO request the backend to email the secci agreement
    }

    //TODO remember what the fourth button was for
    private fun unknown(view: View)
    {

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode)
        {
            PermissionsRequests.REQUEST_WRITE_EXTERNAL_STORAGE_REQUEST -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) downloadFile()
            }
        }
    }

    private fun downloadFile()
    {
        val request = DownloadManager.Request(Uri.parse(agreementUrl))
        request.setTitle("$agreementNumber.pdf")
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "$agreementNumber.pdf")
        (getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager).enqueue(request)
    }
}

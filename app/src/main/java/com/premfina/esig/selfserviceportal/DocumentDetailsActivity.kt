package com.premfina.esig.selfserviceportal

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.View
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.premfina.selfservice.dto.AgreementDto
import kotlinx.android.synthetic.main.activity_document_details.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import kotlin.concurrent.thread


class DocumentDetailsActivity : Drawer() {

    private lateinit var agreementUrl: String
    private lateinit var agreementNumber: String
    private lateinit var tabsView: MyCustomTabsView
    private val nf = NumberFormat.getCurrencyInstance()
    private val df = SimpleDateFormat.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addToFrame(R.layout.activity_document_details)

        val intent = intent
        val agreementDtoString = intent.getStringExtra(ViewAgreementsActivity.PlaceholderFragment.AGREEMENT)
        val agreementDto = jacksonObjectMapper().readValue(agreementDtoString, AgreementDto::class.java)

        agreementDto.documentDtos.map { documentDto -> if (documentDto.signedDocumentUrl != null) agreementUrl = documentDto.signedDocumentUrl }
        //agreementUrl = agreementDto
        agreementNumber = agreementDto.agreementNumber

        broker_name.text = agreementDto.brokerName
        agreement_number.text = agreementDto.agreementNumber
        policy_number.text = agreementDto.policyNumber
        number_of_payments.text = agreementDto.numberOfPayments.toString()
        agreement_status.text = agreementDto.agreementStatus
        live_date.text = df.format(agreementDto.liveDate)
        maturity_date.text = df.format(agreementDto.maturityDate)
        next_due_date.text = df.format(agreementDto.nextDueDate)
        apr.text = "${agreementDto.apr}%"
        monthly_amount.text = nf.format(agreementDto.monthlyAmount)
        cleared_balance.text = nf.format(agreementDto.clearedBalance)
        total_amount.text = nf.format(agreementDto.totalAmount)

        agreement_number.text = agreementNumber
        if(TextUtils.isEmpty(agreementUrl))
            download_documents.isEnabled = false
        else
        {
            thread {
                tabsView = MyCustomTabsView(this, agreementUrl)
            }
        }

    }

    fun viewAgreement(view: View)
    {
        if (sharedPreferences.getBoolean(SettingsActivity.ownbrowserPreferenceKey, false))
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(agreementUrl)))
        else
            tabsView.show()
    }

    fun downloadAgreement(view: View)
    {
        //val file = File(this@DocumentDetailsActivity.filesDir, agreementUrl)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this@DocumentDetailsActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this@DocumentDetailsActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PermissionsRequests.REQUEST_WRITE_EXTERNAL_STORAGE_REQUEST)
        else
            downloadFile()
    }

    fun emailAgreement(view: View)
    {
        /*val url = sharedPReferences.getString("ssp.self-service-portal.url", "")!!+"/email/agreement/"+agreementNumber
        val user = sharedPReferences.getString("ssp.self-service-portal.user", "")!!
        val pw = sharedPReferences.getString("ssp.self-service-portal.password", "")!!

        val userPreferences = getSharedPreferences("User", Context.MODE_PRIVATE)
        val userDto = UserDto(userPreferences.getString("email", "")!!, sharedPReferences.getString("ssp.self-service-portal.brokersource", "")!!)
        userDto.agreementDtos = arrayOf(AgreementDto(agreementNumber, arrayOf()))*/

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

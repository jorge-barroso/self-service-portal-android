package com.premfina.esig.selfserviceportal

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.premfina.esig.selfserviceportal.com.premfina.esig.selfserviceportal.DocumentsListAdapter
import com.premfina.selfservice.dto.*
import kotlinx.android.synthetic.main.activity_document_actions.*
import org.jetbrains.anko.longToast
import org.jetbrains.annotations.NotNull
import java.io.File
import kotlin.concurrent.thread

class DocumentActions : Drawer() {

    private lateinit var documentsListAdapter: DocumentsListAdapter
    private lateinit var tabsView: MyCustomTabsView
    private lateinit var agreementNumber: String
    private lateinit var agreementUrl: String
    private lateinit var documentDtos: Array<DocumentDto>
    private lateinit var requestQueue: RequestQueue
    private lateinit var baseUrl: String
    private lateinit var brokerDto: BrokerDto
    private lateinit var userDto: UserDto
    private var notificationCounter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addToFrame(R.layout.activity_document_actions)

        agreementUrl = intent.getStringExtra("agreementUrl")

        if (TextUtils.isEmpty(agreementUrl))
            download_documents.isEnabled = false
        else {
            thread {
                tabsView = MyCustomTabsView(this, agreementUrl)
            }
        }

        agreementNumber = intent.getStringExtra("agreementNumber")
        documentDtos = jacksonObjectMapper().readValue(intent.getStringExtra("documents"))

        baseUrl = sharedPreferences.getString("ssp.self-service-portal.url", "")!!
        brokerDto = BrokerDto(sharedPreferences.getString("ssp.self-service-portal.brokersource", "")!!)
        userDto = jacksonObjectMapper().readValue(userPreferences.getString("userDto", "")!!)

        documents_agreement_number.text = agreementNumber

        documents_list.layoutManager = LinearLayoutManager(this)
        documentsListAdapter = DocumentsListAdapter(documentDtos, this)
        documents_list.adapter = documentsListAdapter

        requestQueue = Volley.newRequestQueue(this)
    }

    fun viewAgreement(view: View) {
        if (sharedPreferences.getBoolean(SettingsActivity.ownbrowserPreferenceKey, false))
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(agreementUrl)))
        else
            tabsView.show()
    }

    fun downloadDocuments(view: View) {
        if (ContextCompat.checkSelfPermission(this@DocumentActions, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this@DocumentActions, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PermissionsRequests.WRITE_EXTERNAL_STORAGE_REQUEST)
        else
            downloadFile()
        //val file = File(this@DocumentDetailsActivity.filesDir, agreementUrl)
    }

    fun emailDocuments(view: View) {
        userDto.selectedAgreementDtos = documentsListAdapter.getSelectedDocuments().map { documentId ->
            AgreementDto(agreementNumber, null, null, null, arrayOf(documentDtos[documentId]))
        }.toTypedArray()

        val request = StringAuthRequest("$baseUrl/email/agreement", Request.Method.POST, userDto,
                Response.Listener {
                    longToast(getString(R.string.docs_emailed))
                },
                Response.ErrorListener { response ->
                    val message = when (response.networkResponse.statusCode) {
                        400 -> getString(R.string.status_400)
                        404 -> getString(R.string.status_404)
                        else -> getString(R.string.status_500)
                    }
                    longToast(message)
                })

        requestQueue.add(request)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PermissionsRequests.WRITE_EXTERNAL_STORAGE_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) downloadFile()
            }
        }
    }

    private fun downloadFile() {
        documentsListAdapter.getSelectedDocuments().map {
            val notificationDto = NotificationDto()
            notificationDto.brokerDto = brokerDto
            notificationDto.agreementDtos = arrayOf(AgreementDto(agreementNumber, null, null, null, arrayOf(documentDtos[it])))

            getDocument(notificationDto)
            { fileBytes ->
                val file = storeFile(fileBytes, notificationDto.agreementDtos[0].documentDtos[0].name)
                notify(file.path)
            }
        }
    }

    private fun getDocument(@NotNull notificationDto: NotificationDto, @NotNull onSuccess: (fileBytes: ByteArray) -> Unit) {
        val url = if (notificationDto.agreementDtos[0].documentDtos[0].signedDocumentUrl == null)
            "$baseUrl/retrieve/document"
        else
            "$baseUrl/retrieve/signedDocument"

        val request = BinaryAuthRequest(url, Request.Method.POST, notificationDto,
                Response.Listener { response ->
                    onSuccess(response)
                },
                Response.ErrorListener {
                    val message = when (it.networkResponse.statusCode) {
                        400 -> getString(R.string.status_400)
                        404 -> getString(R.string.status_404)
                        else -> getString(R.string.status_500)
                    }
                    longToast(message)
                })

        requestQueue.add(request)
    }

    private fun storeFile(fileBytes: ByteArray, fileName: String): File {
        val filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/${agreementNumber}_$fileName.pdf"
        val file = File(filePath)
        if (!file.exists())
            file.createNewFile()
        file.writeBytes(fileBytes)

        return file
    }

    private fun notify(path: String) {

        resetChannel(getString(R.string.file_downloaded_text))

        val notificationBuilder = NotificationCompat.Builder(this, "PremFina")
                .setSmallIcon(R.mipmap.app_logo)
                .setContentTitle(getString(R.string.file_downloaded_notif))
                .setContentText(getString(R.string.file_downloaded_text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)

        val intent = Intent(Intent.ACTION_VIEW).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        intent.setDataAndType(Uri.parse(path), "application/pdf")

        val activities: List<ResolveInfo> = packageManager.queryIntentActivities(intent, 0)
        if (activities.isNotEmpty()) {
            val intentChooser = Intent.createChooser(intent, getString(R.string.title_select_app))
            notificationBuilder.setContentIntent(PendingIntent.getActivity(this, 0, intentChooser, 0))
        }

        with(NotificationManagerCompat.from(this))
        {
            notify(++notificationCounter, notificationBuilder.build())
        }
    }
}
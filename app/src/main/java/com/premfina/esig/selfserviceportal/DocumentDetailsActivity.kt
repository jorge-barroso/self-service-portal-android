package com.premfina.esig.selfserviceportal

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.premfina.selfservice.dto.AgreementDto
import com.premfina.selfservice.dto.DocumentDto
import kotlinx.android.synthetic.main.activity_document_details.*
import java.text.NumberFormat
import java.text.SimpleDateFormat


class DocumentDetailsActivity : Drawer() {

    private lateinit var agreementUrl: String
    private lateinit var agreementNumber: String
    private lateinit var documentDtos: Array<DocumentDto>
    private lateinit var tabsView: MyCustomTabsView
    private val nf = NumberFormat.getCurrencyInstance()
    private val df = SimpleDateFormat.getDateInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addToFrame(R.layout.activity_document_details)

        //bottom_menu.selectedItemId = R.id.agreements_bottom

        val agreementDtoString = intent.getStringExtra(AgreementsDetailsActivity.PlaceholderFragment.AGREEMENT)
        val agreementDto = jacksonObjectMapper().readValue(agreementDtoString, AgreementDto::class.java)

        agreementDto.documentDtos.map { documentDto -> if (documentDto.signedDocumentUrl != null) agreementUrl = documentDto.signedDocumentUrl }
        //agreementUrl = agreementDto
        agreementNumber = agreementDto.agreementNumber
        documentDtos = agreementDto.documentDtos

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
    }

    fun goToDocumentsActions(view: View) {
        val intent = Intent(this, DocumentActions::class.java)
        intent.putExtra("agreementUrl", agreementUrl)
                .putExtra("agreementNumber", agreementNumber)
                .putExtra("documents", jacksonObjectMapper().writeValueAsString(documentDtos))
                .putExtra("element_id", R.id.agreements_bottom)
        startActivity(intent)
    }
}

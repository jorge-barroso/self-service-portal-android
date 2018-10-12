package com.premfina.esig.selfserviceportal

import android.content.Intent
import android.os.Bundle
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.premfina.selfservice.dto.UserDto
import kotlinx.android.synthetic.main.activity_main.*
import lecho.lib.hellocharts.model.PieChartData
import lecho.lib.hellocharts.model.SliceValue
import java.text.NumberFormat

class MainActivity : Drawer() {

    /**
     * TODO implement functionality for multiple agreements:
     * - Store an Map<String, List<DocumentDto>> with agreement numbers as key, as we populate and add the cards and spinners
     * - Store an Map<String, List<DocumentDto>> with agreement numbers as key, as we populate and add the cards and spinners
     * - Give the spinners an identification (don't remember property for this extra value) matching the index of the agreement the spinner is for
     * - By this we can get the exact document
     */
    //private lateinit var agreementsSpinner: AgreementsSpinner
    //val CLASSNAME = this::class.simpleName
    private val nf = NumberFormat.getCurrencyInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addToFrame(layoutResID = R.layout.activity_main)

        val userDto = jacksonObjectMapper().readValue<UserDto>(userPreferences.getString("userDto", "")!!)
        val agreements = userDto.agreementDtos
        val amountOfAgreements = agreements.size

        var totalToPay = 0.0
        var totalPaid = 0.0
        for (i in 0 until amountOfAgreements) {
            totalToPay += agreements[i].totalAmount.toFloat()
            totalPaid += agreements[i].clearedBalance.toFloat()
        }

        val pieData: ArrayList<SliceValue> = ArrayList(2)
        pieData.add(SliceValue(totalToPay.toFloat(), getColor(R.color.light_orange)).setLabel(getString(R.string.to_pay))) //To pay
        pieData.add(SliceValue(totalPaid.toFloat(), getColor(R.color.colorPrimary)).setLabel(getString(R.string.paid))) //Already paid

        balance_chart.pieChartData = PieChartData(pieData)

        agreements_amount.text = getString(R.string.agreements_amount, amountOfAgreements)
        to_pay.text = getString(R.string.to_pay, nf.format(totalToPay))
        paid.text = getString(R.string.paid, nf.format(totalPaid))

        balance_chart.setOnClickListener { startActivity(Intent(this, ViewAgreementsActivity::class.java)) }
    }
    /*val intent = intent
    if(intent.getStringExtra("signatureUrl") != null)
    {
        val customTabsView = MyCustomTabsView(this, intent.getStringExtra("signatureUrl"))
        customTabsView.show()
    }

    val userDto = jacksonObjectMapper().readValue<UserDto>(userPreferences.getString("userDto", "")!!)
    agreementsSpinner = AgreementsSpinner(this, userDto, agreements_spinner, documents_view)

    //Extra element to go to agreement details
    included_view.isFocusable = false
    val recyclerTextView: TextView = included_view.recycler_text_view
    recyclerTextView.text = getString(R.string.agreement_details)

    recyclerTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white))
    //recyclerTextView.isClickable = true
    recyclerTextView.isFocusableInTouchMode = false
    recyclerTextView.setOnClickListener { moreDetails() }
}

private fun moreDetails() {
    val intent = Intent(this@MainActivity, DocumentDetailsActivity::class.java)
    intent.putExtra("agreement", agreementsSpinner.currentAgreement.agreementNumber)
    val agreement = agreementsSpinner.currentAgreement
    val dateParser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    val dateFormatter = SimpleDateFormat.getDateTimeInstance()
    for(document in agreement!!.documentDtos)
    {
        if(!TextUtils.isEmpty(document.signedDocumentUrl))
        {
            intent.putExtra("agreement_url", document.signedDocumentUrl)
            val date = dateParser.parse(document.createdOn)
            intent.putExtra("creation_date", dateFormatter.format(date))
        }
    }

    intent.putExtra("cleared_balance", agreement.clearedBalance)
    startActivity(intent)
}*/
}
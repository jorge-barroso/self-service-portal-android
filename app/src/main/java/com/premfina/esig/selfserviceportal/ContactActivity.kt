package com.premfina.esig.selfserviceportal

import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.premfina.esig.selfserviceportal.com.premfina.esig.selfserviceportal.AgreementsSpinner
import com.premfina.selfservice.dto.AgreementDto
import com.premfina.selfservice.dto.CustomerQueryDto
import com.premfina.selfservice.dto.UserDto
import kotlinx.android.synthetic.main.activity_contact.*
import kotlinx.android.synthetic.main.app_bar_drawer.*

class ContactActivity : Drawer() {

    private var mainScreen = true
    private lateinit var spinner: AgreementsSpinner
    private lateinit var userDto: UserDto
    private var includesAgreement = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addToFrame(R.layout.activity_contact)

        bottom_menu.menu.setGroupCheckable(0, false, true)

        userDto = jacksonObjectMapper().readValue(userPreferences.getString("userDto", "")!!)
        val agreementDtos = jacksonObjectMapper().readValue<Array<AgreementDto>>(agreementsDetails.getString("agreements", "")!!)

        user_email_view.text = userDto.email

        spinner = AgreementsSpinner(this, agreementDtos, spinner2)

        submit_button.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                submit_button.isEnabled = (subject.text?.isBlank() ?: true || query.text?.isBlank() ?: true || (!includesAgreement))
            }
        })
    }

    fun yesClicked(view: View)
    {
        buttonPressed()
        includesAgreement = true
        ageement_numbers_label.visibility = View.VISIBLE
        agreement_numbers_view.visibility = View.VISIBLE
    }

    fun noClicked(view: View)
    {
        buttonPressed()
        includesAgreement = false
        ageement_numbers_label.visibility = View.GONE
        agreement_numbers_view.visibility = View.GONE
    }

    private fun buttonPressed(){
        mainScreen = false
        question_layout.visibility = View.GONE
        form_layout.visibility = View.VISIBLE
    }

    override fun onBackPressed() {
        if(mainScreen)
            super.onBackPressed()
        else
            backToMainContactScreen()
    }

    private fun backToMainContactScreen() {
        form_layout.visibility = View.GONE
        agreement_numbers_view.visibility = View.GONE
        question_layout.visibility = View.VISIBLE
        mainScreen = true
    }

    fun sendQuery(view: View) {
        val sharePreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val url = sharePreferences.getString("ssp.self-service-portal.url", "")!! + "/customer/query"
        /*val user = sharePreferences.getString("ssp.self-service-portal.user", "")!!
        val pw = sharePreferences.getString("ssp.self-service-portal.password", "")!!*/

        val customerQuery = CustomerQueryDto()
        customerQuery.agreementNumber = spinner.currentAgreement.agreementNumber
        customerQuery.email = user_email_view.text.toString()
        customerQuery.fullName = userDto.fullName
        customerQuery.subject = subject.text.toString()
        customerQuery.query = query.text.toString()

        val newUserDto = UserDto(userDto.email, userDto.brokerSource)
        newUserDto.customerQueryDto = customerQuery
        newUserDto.agreementDtos = userDto.agreementDtos

        val requestQueue = Volley.newRequestQueue(this)

        val request = StringAuthRequest(url, Request.Method.POST, newUserDto,
                Response.Listener {
                    Toast.makeText(this, getString(R.string.emailed_successful), Toast.LENGTH_LONG).show()
                    onBackPressed()
                },
                Response.ErrorListener { response ->
                    val message: String = when (response.networkResponse.statusCode) {
                        300 -> getString(R.string.something_went_wrong)

                        else -> getString(R.string.something_went_wrong)
                    }

                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                }
        )
        requestQueue.add(request)
    }
}

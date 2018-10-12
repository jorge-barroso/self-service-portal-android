package com.premfina.esig.selfserviceportal

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.premfina.esig.selfserviceportal.com.premfina.esig.selfserviceportal.AgreementsSpinner
import com.premfina.selfservice.dto.CustomerQueryDto
import com.premfina.selfservice.dto.UserDto
import kotlinx.android.synthetic.main.activity_contact.*

class ContactActivity : Drawer() {

    private var mainScreen = true
    private lateinit var spinner: AgreementsSpinner
    private lateinit var userDto: UserDto
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addToFrame(R.layout.activity_contact)

        userDto = jacksonObjectMapper().readValue<UserDto>(userPreferences.getString("userDto", "")!!)

        user_email_view.text = userDto.email

        spinner = AgreementsSpinner(this, userDto, spinner2)
    }

    fun yesClicked(view: View)
    {
        buttonPressed()
        ageement_numbers_label.visibility = View.VISIBLE
        agreement_numbers_view.visibility = View.VISIBLE
    }

    fun noClicked(view: View)
    {
        buttonPressed()
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

        val request = AuthRequest(url, Request.Method.POST, newUserDto,
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

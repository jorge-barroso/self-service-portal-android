package com.premfina.esig.selfserviceportal

import android.os.Bundle
import android.support.v7.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.premfina.selfservice.dto.RegistrationDto
import kotlinx.android.synthetic.main.activity_registration.*
import kotlinx.android.synthetic.main.registration_fragment.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask

class RegistrationActivity : MyBaseApplication() {
    private val ukPostCodeRegex: Regex = "^((([A-PR-UWYZ][0-9])|([A-PR-UWYZ][0-9][0-9])|([A-PR-UWYZ][A-HK-Y][0-9])|([A-PR-UWYZ][A-HK-Y][0-9][0-9])|([A-PR-UWYZ][0-9][A-HJKSTUW])|([A-PR-UWYZ][A-HK-Y][0-9][ABEHMNPRVWXY]))\\s?([0-9][ABD-HJLNP-UW-Z]{2})|(GIR)\\s?(0AA))\$".toRegex()
    private val agreementNumberRegex: Regex = "^([0-9]{9})(/[0-9]{1,2})?\$".toRegex()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        setSupportActionBar(registration_toolbar)
        //supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (dob_day.isFocused && dob_day.text!!.length == 2)
                    dob_month.requestFocus()
                else if (dob_month.isFocused && dob_month.text!!.length == 2)
                    dob_year.requestFocus()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val blankSurname = surname.text!!.isBlank()
                val blankPostCode = postcode.text!!.isBlank()
                val blankAgrNumber = agreement_number.text!!.isBlank()
                val wrongDobDay = dob_day.text!!.length != 2
                val wrongDobMonth = dob_month.text!!.length != 2
                val wrongDobYear = dob_year.text!!.length != 4
                submit_registration_button.isEnabled = !(blankSurname || blankPostCode || blankAgrNumber || wrongDobDay || wrongDobMonth || wrongDobYear)
            }
        }

        surname.addTextChangedListener(textWatcher)
        postcode.addTextChangedListener(textWatcher)
        agreement_number.addTextChangedListener(textWatcher)
        dob_day.addTextChangedListener(textWatcher)
        dob_month.addTextChangedListener(textWatcher)
        dob_year.addTextChangedListener(textWatcher)


    }

    fun submit(view: View)
    {
        if (!ukPostCodeRegex.matches(postcode.text.toString())) {
            postcode.error = getString(R.string.invalid_postode)
            return
        }
        if (!agreementNumberRegex.matches(agreement_number.text.toString())) {
            agreement_number.error = getString(R.string.invalid_agreementNumber)
            return
        }

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val url = sharedPreferences.getString("ssp.self-service-portal.url", "")!! + "/create/registration"
        /*val user = sharedPreferences.getString("ssp.self-service-portal.user", "")!!
        val pw = sharedPreferences.getString("ssp.self-service-portal.password", "")!!

        val auth = Base64.encode("$user:$pw".toByteArray(), android.util.Base64.DEFAULT)*/

        //TODO send registration request
        val registrationDto = RegistrationDto()
        registrationDto.brokerSource = ""
        registrationDto.surname = surname.text.toString()
        registrationDto.agreementNumber = agreement_number.text.toString()
        registrationDto.postCode = postcode.text.toString()
        registrationDto.dateOfBirth = "${dob_year.text.toString()}-${dob_month.text.toString()}-${dob_day.text.toString()}"
        registrationDto.brokerSource = sharedPreferences.getString("ssp.self-service-portal.brokersource", "")!!
        registrationDto.isPersonal = true

        val requestQueue = Volley.newRequestQueue(this)
        val jsonRequest = StringAuthRequest(url, Request.Method.POST, registrationDto,
                Response.Listener {
                    startActivity(intentFor<LoginActivity>("message" to R.string.reg_success)
                            .newTask()
                            .clearTask()
                    )
                },
                Response.ErrorListener { response ->
                    Log.e("RegistrationActivity", String(response.networkResponse.data))
                    Toast.makeText(this, String(response.networkResponse.data), Toast.LENGTH_LONG).show()
                }
        )
        requestQueue.add(jsonRequest)
    }
}

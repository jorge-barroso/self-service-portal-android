package com.premfina.esig.selfserviceportal

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.preference.PreferenceManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.premfina.selfservice.dto.RegistrationDto
import kotlinx.android.synthetic.main.activity_registration.*
import kotlinx.android.synthetic.main.registration_fragment.*
import java.text.SimpleDateFormat
import java.util.*

class RegistrationActivity : MyBaseApplication() {
    private val ukPostCodeRegex: Regex = "^((([A-PR-UWYZ][0-9])|([A-PR-UWYZ][0-9][0-9])|([A-PR-UWYZ][A-HK-Y][0-9])|([A-PR-UWYZ][A-HK-Y][0-9][0-9])|([A-PR-UWYZ][0-9][A-HJKSTUW])|([A-PR-UWYZ][A-HK-Y][0-9][ABEHMNPRVWXY]))\\s?([0-9][ABD-HJLNP-UW-Z]{2})|(GIR)\\s?(0AA))\$".toRegex()
    private val agreementNumberRegex: Regex = "^([0-9]{9})(\\/[0-9]{1,2})?\$".toRegex()
    private val df = SimpleDateFormat.getDateInstance()
    private val calendar = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private lateinit var datePicker: DatePickerDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        setSupportActionBar(registration_toolbar)
        //supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            calendar.set(year, month, day)
            Log.i("RegistrationActivity", "Date set listened")
            updateDate()
        }

        datePicker = DatePickerDialog(this@RegistrationActivity, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val blankSurname = surname.text!!.isBlank()
                val blankPostCode = postcode.text!!.isBlank()
                val blankAgrNumber = agreement_number.text!!.isBlank()
                val blankDob = dob.text!!.isBlank()
                submit_registration_button.isEnabled = !(blankSurname || blankPostCode || blankAgrNumber || blankDob)
            }
        }

        surname.addTextChangedListener(textWatcher)
        postcode.addTextChangedListener(textWatcher)
        agreement_number.addTextChangedListener(textWatcher)
        dob.addTextChangedListener(textWatcher)


    }

    fun showDatePicker(view: View) = datePicker.show()

    fun submit(view: View)
    {
        var error = false

        if (!ukPostCodeRegex.matches(postcode.text.toString())) {
            postcode.error = getString(R.string.invalid_postode)
            error = true
        }
        if (!agreementNumberRegex.matches(agreement_number.text.toString())) {
            agreement_number.error = getString(R.string.invalid_agreementNumber)
            error = true
        }

        for (element in arrayOf(surname, postcode, agreement_number, dob))
        {
            if(TextUtils.isEmpty(element.text)) {
                element.error = getString(R.string.mandatory_field)
                error = true
            }
        }

        if (error)
            return

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
        registrationDto.dateOfBirth = SimpleDateFormat("yyyy-MM-dd", Locale.UK).format(df.parse(dob.text.toString()))
        registrationDto.brokerSource = sharedPreferences.getString("ssp.self-service-portal.brokersource", "")!!
        registrationDto.isPersonal = true

        val requestQueue = Volley.newRequestQueue(this)
        val jsonRequest = AuthRequest(url, Request.Method.POST, registrationDto,
                Response.Listener {
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.putExtra("registration-success", true)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                },
                Response.ErrorListener { response ->
                    Log.e("RegistrationActivity", String(response.networkResponse.data))
                    Toast.makeText(this, String(response.networkResponse.data), Toast.LENGTH_LONG).show()
                }
        )
        requestQueue.add(jsonRequest)
    }

    private fun updateDate() = dob.setText(df.format(calendar.time))

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
                true
            }
            else -> {
                false
            }
        }
    }
}

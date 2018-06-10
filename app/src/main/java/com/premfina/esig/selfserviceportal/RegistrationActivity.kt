package com.premfina.esig.selfserviceportal

import android.app.DatePickerDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_registration.*
import java.text.SimpleDateFormat
import java.util.*

class RegistrationActivity : AppCompatActivity() {
    private val ukPostCodeRegex: Regex = "^((([A-PR-UWYZ][0-9])|([A-PR-UWYZ][0-9][0-9])|([A-PR-UWYZ][A-HK-Y][0-9])|([A-PR-UWYZ][A-HK-Y][0-9][0-9])|([A-PR-UWYZ][0-9][A-HJKSTUW])|([A-PR-UWYZ][A-HK-Y][0-9][ABEHMNPRVWXY]))\\s?([0-9][ABD-HJLNP-UW-Z]{2})|(GIR)\\s?(0AA))\$".toRegex()
    private val df = SimpleDateFormat.getDateInstance()
    private val calendar = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private lateinit var datePicker: DatePickerDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            calendar.set(year, month, day)
            Log.i("RegistrationActivity", "Date set listened")
            updateDate()
        }

        datePicker = DatePickerDialog(this@RegistrationActivity, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
    }

    fun goToLogin(view: View)
    {
        startActivity(Intent(this@RegistrationActivity, LoginActivity::class.java))
    }

    fun showDatePicker(view: View) = datePicker.show()

    fun submit(view: View)
    {

        if(ukPostCodeRegex.matches(postcode.text))
            postcode.error = getString(R.string.invalid_postode)

        //TODO implement all the validations present on web frontend
        for(element in arrayOf(surname, postcode, agreement_number_view, dob))
        {
            if(TextUtils.isEmpty(element.text))
                element.error = getString(R.string.mandatory_field)
        }
    }

    private fun updateDate() {
        Log.i("RegistrationActivity", "Updating date")
        dob.setText(df.format(calendar.time))
    }
}

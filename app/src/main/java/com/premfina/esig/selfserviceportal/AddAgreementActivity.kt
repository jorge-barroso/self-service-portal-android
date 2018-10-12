package com.premfina.esig.selfserviceportal

import android.os.Bundle
import android.view.Menu
import android.view.View
import com.premfina.selfservice.dto.RegistrationDto
import kotlinx.android.synthetic.main.registration_fragment.*

class AddAgreementActivity : Drawer() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addToFrame(R.layout.activity_add_agreement)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return true
    }

    fun addAgreement(view: View) {
        val registrationDto = RegistrationDto()
        registrationDto.isPersonal = true
        //TODO implement functionality
        registrationDto.surname = surname.text!!.toString()
        registrationDto.postCode = postcode.text!!.toString()
        registrationDto.dateOfBirth = dob.text!!.toString()
        registrationDto.brokerSource = sharedPreferences.getString("ssp.self-service-portal.brokersource", "")
        registrationDto.agreementNumber = agreement_number.text!!.toString()
    }
}

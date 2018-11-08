package com.premfina.esig.selfserviceportal

import android.os.Bundle
import android.view.Menu
import android.view.View
import com.premfina.selfservice.dto.RegistrationDto
import kotlinx.android.synthetic.main.app_bar_drawer.*
import kotlinx.android.synthetic.main.registration_fragment.*

class AddAgreementActivity : Drawer() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addToFrame(R.layout.activity_add_agreement)

        bottom_menu.menu.setGroupCheckable(0, false, true)
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
        registrationDto.dateOfBirth = "${dob_year.text.toString()}-${dob_month.text.toString()}-${dob_day.text.toString()}"
        registrationDto.brokerSource = sharedPreferences.getString("ssp.self-service-portal.brokersource", "")
        registrationDto.agreementNumber = agreement_number.text!!.toString()
    }
}

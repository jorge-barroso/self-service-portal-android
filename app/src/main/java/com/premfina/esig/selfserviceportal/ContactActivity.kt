package com.premfina.esig.selfserviceportal

import android.os.Bundle
import android.view.View

class ContactActivity : Drawer() {

    private var mainScreen = true
    private lateinit var questionLayout: View
    private lateinit var formLayout: View
    private lateinit var agreementNumbers: View
    private lateinit var agreementNumbersLabel: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addToFrame(R.layout.activity_contact)
        questionLayout = findViewById<View>(R.id.question_layout)
        formLayout = findViewById<View>(R.id.form_layout)
        agreementNumbers = findViewById<View>(R.id.agreement_numbers_view)
        agreementNumbersLabel =findViewById<View>(R.id.ageement_numbers_label)
    }

    fun yesClicked(view: View)
    {
        buttonPressed()
        agreementNumbersLabel.visibility = View.VISIBLE
        agreementNumbers.visibility = View.VISIBLE
    }

    fun noClicked(view: View)
    {
        buttonPressed()
        agreementNumbersLabel.visibility = View.INVISIBLE
        agreementNumbers.visibility = View.INVISIBLE
    }

    private fun buttonPressed(){
        mainScreen = false
        questionLayout.visibility = View.INVISIBLE
        formLayout.visibility = View.VISIBLE
    }

    override fun onBackPressed() {
        if(mainScreen)
            super.onBackPressed()
        else
            backToMainContactScreen()
    }

    private fun backToMainContactScreen() {
        formLayout.visibility = View.INVISIBLE
        questionLayout.visibility = View.VISIBLE
        agreementNumbers.visibility = View.INVISIBLE
    }
}

package com.premfina.esig.selfserviceportal

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_contact.*

class ContactActivity : Drawer() {

    private var mainScreen = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addToFrame(R.layout.activity_contact)
        user_email_view.text = intent.getStringExtra("email")
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
        question_layout.visibility = View.GONE
        agreement_numbers_view.visibility = View.GONE
    }
}

package com.premfina.esig.selfserviceportal

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : MyBaseApplication() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (intent.getBooleanExtra("registration-success", false))
            reg_success_card.visibility = View.VISIBLE

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                login_submit_button.isEnabled = !(email.text!!.isBlank() || password.text!!.isBlank())
            }
        }

        email.addTextChangedListener(textWatcher)
        password.addTextChangedListener(textWatcher)
    }

    fun goToRegistration(view: View) = startActivity(Intent(this@LoginActivity, RegistrationActivity::class.java))

    fun goToForgottenPassword(view: View) = startActivity(Intent(this@LoginActivity, ForgottenPasswordActivity::class.java))

    fun submitLogin(view: View)
    {
        if (email.text!!.isBlank()) {
            email.error = getString(R.string.mandatory_field)
            return
        }

        if (password.text!!.isBlank()) {
            password.error = getString(R.string.mandatory_field)
            return
        }

        val intent = Intent(this, LoadingActivity::class.java)
        intent.putExtra("email", email.text.toString())
        intent.putExtra("password", password.text.toString())
        startActivity(intent)
        finish()
    }
}

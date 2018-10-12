package com.premfina.esig.selfserviceportal

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_forgotten_password.*

class ForgottenPasswordActivity : MyBaseApplication() {

    private lateinit var requestQueue: RequestQueue
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgotten_password)

        requestQueue = Volley.newRequestQueue(this)

        fp_email.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                send_reset_password.isEnabled = !fp_email.text!!.isBlank()
            }

        })
    }

    fun sendResetPassword(view: View) {
        val request = AuthRequest("", Request.Method.POST, null,
                Response.Listener {

                },
                Response.ErrorListener {

                })

        requestQueue.add(request)
    }
}

package com.premfina.esig.selfserviceportal

import android.os.Bundle
import android.support.v7.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.premfina.selfservice.dto.ForgottenPasswordDto
import kotlinx.android.synthetic.main.activity_forgotten_password.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.longToast

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

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
    }

    fun sendResetPassword(view: View) {
        val url = sharedPreferences.getString("ssp.self-service-portal.url", "")!! + "/forgotten/password"

        val forgottenPasswordDto = ForgottenPasswordDto()
        forgottenPasswordDto.email = fp_email.text.toString()
        forgottenPasswordDto.brokerSource = sharedPreferences.getString("ssp.self-service-portal.brokersource", "")!!

        val request = StringAuthRequest(url, Request.Method.POST, forgottenPasswordDto,
                Response.Listener {
                    startActivity(intentFor<LoginActivity>("message" to getString(R.string.fp_email_sent)))
                },
                Response.ErrorListener {
                    val message = when (it.networkResponse.statusCode) {
                        400 -> getString(R.string.status_400)
                        else -> getString(R.string.status_500)
                    }

                    longToast(message)
                })

        requestQueue.add(request)
    }
}

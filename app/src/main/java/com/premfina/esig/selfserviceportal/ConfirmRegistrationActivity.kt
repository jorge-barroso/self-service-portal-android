package com.premfina.esig.selfserviceportal

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.premfina.selfservice.dto.MFADto
import com.premfina.selfservice.dto.UserDto
import com.premfina.selfservice.dto.enumeration.UserType
import kotlinx.android.synthetic.main.activity_confirm_registration.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.longToast
import org.jetbrains.annotations.NotNull

class ConfirmRegistrationActivity : MyBaseApplication() {

    private var isMfaVisible = false
    private lateinit var requestQueue: RequestQueue
    private lateinit var userDto: UserDto
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_registration)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val brokerSource = sharedPreferences.getString("ssp.self-service-portal.brokersource", "")!!
        val baseUrl = sharedPreferences.getString("ssp.self-service-portal.url", "")!!
        val path = "${intent.data?.path}/$brokerSource"

        requestQueue = Volley.newRequestQueue(this)

        //val t1 = thread {
        val request = StringAuthRequest(baseUrl + path, Request.Method.GET,
                listener = Response.Listener { response ->
                    userDto = jacksonObjectMapper().readValue(response)
                },
                errorListener = Response.ErrorListener { response ->
                    val message = when (response.networkResponse.statusCode) {
                        400 -> getString(R.string.conf_reg_bad_request)
                        else -> getString(R.string.status_500)
                    }

                    longToast(message)
                    startActivity(intentFor<LoginActivity>())
                    finishAfterTransition()
                })

        requestQueue.add(request)
        //}

        //t1.join()

        // request confirm password and get user details
    }

    fun validateAndNext(view: View) {
        val password = create_password.text.toString()
        val confirmPassword = confirm_password.text.toString()

        // We here set the possible error message for the create password boxes
        // We will set a different error message on create_password depending on the failed validation, and in confirm_password in case they don't match
        // I pass "true" to "when" to
        create_password.error = when (true) {
            password != confirmPassword -> {
                val errorMessage = getString(R.string.pw_not_match) //TODO
                confirm_password.error = errorMessage
                errorMessage
            }
            password.length < 8 -> getString(R.string.pw_short)
            password.toUpperCase() == password || password.toLowerCase() == password || !password.contains(Regex("\\d")) -> getString(R.string.pw_format)
            else -> null
        }

        if (create_password.error == null)
            toggleLayouts()
    }

    fun textToken(view: View) {
        mfaRequest("/mfa/token/text", getString(R.string.mfa_texted))

    }

    fun emailToken(view: View) {
        mfaRequest("/mfa/token/email", getString(R.string.mfa_emailed))
    }

    fun submitRegistrationConfirm(view: View) {
        //api request to validate token

        //if valid
        // create password
        //go to doLogin with current credentials

        //if not
        // display error message
    }

    private fun toggleLayouts() {
        create_password_layout.visibility = if (isMfaVisible) View.VISIBLE else View.GONE
        mfa_layout.visibility = if (isMfaVisible) View.GONE else View.VISIBLE

        isMfaVisible = !isMfaVisible
    }

    private fun mfaRequest(@NotNull url: String, @NotNull successMessage: String) {
        // Fill in with the user details we have retrieved on the confirm registration request made in the onCreate function
        val mfaDto = MFADto(0, null, "07752319217", UserType.FULL_SIGN)

        val request = StringAuthRequest(url, Request.Method.POST, mfaDto,
                Response.Listener {
                    longToast(successMessage)
                },
                Response.ErrorListener { response ->
                    val errorMessage = when (response.networkResponse?.statusCode) {
                        404 -> ""
                        else -> ""
                    }

                    longToast(errorMessage)
                })
    }
}

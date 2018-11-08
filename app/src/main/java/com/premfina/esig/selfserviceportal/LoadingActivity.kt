package com.premfina.esig.selfserviceportal

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceManager
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.premfina.selfservice.dto.UserDto
import kotlinx.android.synthetic.main.activity_loading.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import java.util.*

class LoadingActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userDetails: SharedPreferences
    private lateinit var agreementsDetails: SharedPreferences
    private lateinit var brokerPreferences: SharedPreferences
    private lateinit var requestQueue: RequestQueue
    private val objectMapper = jacksonObjectMapper()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        progressBar.indeterminateDrawable.setColorFilter(getColor(R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY)

        requestQueue = Volley.newRequestQueue(this)

        userDetails = getSharedPreferences("User", Context.MODE_PRIVATE)
        agreementsDetails = getSharedPreferences("Agreements", Context.MODE_PRIVATE)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        if (intent.getStringExtra("email") != null) {
            doLogin(intent.getStringExtra("email"), intent.getStringExtra("password"))
            return
        }

        val properties = Properties()
        properties.load(assets.open("selfservice-portal.properties"))

        brokerPreferences = getSharedPreferences("Broker", Context.MODE_PRIVATE)
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)

        val generalEditor = sharedPreferences.edit()
        for (entry in properties) {
            generalEditor.putString(entry.key.toString(), entry.value.toString())
        }
        generalEditor.apply()

        val url = properties["ssp.self-service-portal.url"].toString() + "/broker/getdata/" + properties["ssp.self-service-portal.brokersubdomain"].toString()
        val request = StringAuthRequest(url, Request.Method.GET,
                listener = Response.Listener { response ->
                    val brokerEditor = brokerPreferences.edit()
                    brokerEditor.clear()
                    val brokerData: Map<String, Any> = objectMapper.readValue(response)
                    brokerData.entries.asSequence().mapNotNull { entry -> brokerEditor.putString(entry.key, entry.value.toString()) }.toList()
                    brokerEditor.apply()
                },
                errorListener = Response.ErrorListener {
                    Toast.makeText(this, getString(R.string.error_loading_broker), Toast.LENGTH_LONG).show()
                    finish()
                    moveTaskToBack(true)
                    System.exit(0)
                }
        )
        requestQueue.add(request)

        when {
            userDetails.contains("email") -> doLogin(userDetails.getString("email", "")!!, userDetails.getString("password", "")!!)
            else -> startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun doLogin(email: String, password: String) {
        progressBar.visibility = View.VISIBLE
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        val url = sharedPreferences.getString("ssp.self-service-portal.url", "")!! + "/login"
        //val user = sharedPreferences.getString("ssp.self-service-portal.user", "")!!
        //val pw = sharedPreferences.getString("ssp.self-service-portal.password", "")!!
        //TODO implement: val auth = Base64.encode("$user:$pw".toByteArray(), Base64.NO_WRAP)

        val userDto = UserDto(email, sharedPreferences.getString("broker_source", ""))
        userDto.password = password
        userDto.brokerSource = sharedPreferences.getString("ssp.self-service-portal.brokersource", "")

        val jsonRequest = StringAuthRequest(url, Request.Method.POST, userDto,
                Response.Listener { response ->
                    val finalUserDto = objectMapper.readValue(response.toString(), UserDto::class.java)
                    val agreementDtos = finalUserDto.agreementDtos

                    finalUserDto.agreementDtos = null

                    val userEditor = userDetails.edit()
                    userEditor.putString("email", finalUserDto.email)
                    userEditor.putString("password", userDto.password)
                    userEditor.putString("username", finalUserDto.fullName)
                    userEditor.putString("userDto", objectMapper.writeValueAsString(finalUserDto))
                    userEditor.apply()

                    agreementsDetails.edit()
                            .putString("agreements", objectMapper.writeValueAsString(agreementDtos))
                            .apply()

                    val intent = intentFor<DashboardActivity>("element_id" to R.id.dashboard_bottom)

                    if (finalUserDto.redirectTo != "home")
                        intent.putExtra("signatureUrl", finalUserDto.redirectTo)

                    startActivity(intent.newTask().clearTask())
                },
                Response.ErrorListener { response ->
                    val message = when (response.networkResponse?.statusCode) {
                        401 -> "Wrong credentials"
                        else -> "Problem logging in"
                    }

                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()

                    startActivity(Intent(this, LoginActivity::class.java))
                }
        )
        requestQueue.add(jsonRequest)
    }
}

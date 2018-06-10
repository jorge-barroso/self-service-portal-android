package com.premfina.esig.selfserviceportal

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.util.*

class LoginActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val properties = Properties()
        properties.load(assets.open("selfservice-portal.properties"))

        sharedPreferences = getSharedPreferences("Properties", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        for(entry in properties)
        {
            editor.putString(entry.key.toString(), entry.value.toString())
        }

        editor.apply()
    }

    fun goToRegistration(view: View)
    {
        val intent = Intent(this@LoginActivity, RegistrationActivity::class.java)
        startActivity(intent)
    }

    fun submitLogin(view: View)
    {
        /*val url = sharedPreferences.getString("ssp.self-service-portal.url", "")
        val extraHeaders: Map<String, String> = hashMapOf("Authorization" to "Basic YWRtaW46NzQwOTU0ZDA0YjkzNDlmOGE5ZTFlYjI5NmFiNzRhYjc=")
        val userDto = UserDto(email.text.toString(), sharedPreferences.getString("broker_source", ""))
        val response = khttp.get(url = url, headers = extraHeaders, data = userDto)

        //TODO implement message to show
        if(response.statusCode != 200)
            Toast.makeText(this@LoginActivity, R.string.something_went_wrong, Toast.LENGTH_LONG).show()

        val finalUserDto: UserDto = jacksonObjectMapper().readValue(response.text)

        if(finalUserDto.active!=null && !finalUserDto.active)
            Toast.makeText(this@LoginActivity, R.string.wrong_credentials, Toast.LENGTH_LONG).show()
        if(finalUserDto.password != "")
            Toast.makeText(this@LoginActivity, R.string.wrong_credentials, Toast.LENGTH_LONG).show()

        val sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("username", finalUserDto.fullName)
        editor.putString("email", finalUserDto.email)
        editor.apply()*/

        val sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("username", "Mr Jorge Barroso Barea")
        editor.putString("email", "jorge.barroso@premfina.com")
        editor.apply()
        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        finish()
    }
}

package com.premfina.esig.selfserviceportal

import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

class AuthRequest(url: String?, method: Int, private val bodyObj: Any? = Object(), listener: Response.Listener<String>? = null, errorListener: Response.ErrorListener?) : StringRequest(method, url, listener, errorListener) {

    //val mAuth: String = auth

    override fun getHeaders(): MutableMap<String, String> {
        return mutableMapOf("Content-Type" to "application/json")//, "Authorization" to "Basic $mAuth")
    }

    override fun getBody(): ByteArray {
        return jacksonObjectMapper().writeValueAsString(bodyObj).toByteArray()
    }
}
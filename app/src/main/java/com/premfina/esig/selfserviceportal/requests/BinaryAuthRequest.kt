package com.premfina.esig.selfserviceportal

import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

class BinaryAuthRequest(url: String?, method: Int, private val bodyObj: Any? = Object(), private val listener: Response.Listener<ByteArray>? = null, errorListener: Response.ErrorListener?) : Request<ByteArray>(method, url, errorListener) {
    override fun parseNetworkResponse(response: NetworkResponse?): Response<ByteArray> {
        val code = response?.statusCode!!
        return when (code / 100) {
            1, 2 -> Response.success<ByteArray>(response.data, HttpHeaderParser.parseCacheHeaders(response))
            else -> Response.error<ByteArray>(VolleyError(response))
        }
    }

    override fun deliverResponse(response: ByteArray?) {
        listener?.onResponse(response)
    }


    override fun getHeaders(): MutableMap<String, String> {
        return mutableMapOf("Content-Type" to "application/json")//, "Authorization" to "Basic $mAuth")
    }

    override fun getBody(): ByteArray {
        return jacksonObjectMapper().writeValueAsString(bodyObj).toByteArray()
    }
}
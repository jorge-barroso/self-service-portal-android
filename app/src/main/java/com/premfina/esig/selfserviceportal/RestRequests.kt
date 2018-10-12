package com.premfina.esig.selfserviceportal

import android.os.AsyncTask
import android.util.Log
import java.net.URL
import java.nio.charset.Charset
import javax.net.ssl.HttpsURLConnection

class RestRequests : AsyncTask<String, Void, String>() {
    override fun doInBackground(vararg params: String?): String {
        return when (params.size) {
            2 -> {
                postRequest(params[0]!!, params[1]!!)
            }
            3 -> {
                postRequest(params[0]!!, params[1]!!, params[2]!!)
            }
            else -> {
                throw Exception("At least url and body must be specified")
            }
        }
    }

    private fun postRequest(urlString: String, body: String, auth: String? = ""): String {
        val url = URL(urlString)
        val con = url.openConnection() as HttpsURLConnection
        con.requestMethod = "POST"
        con.setRequestProperty("Content-Type", "application/json")
        if (!auth.isNullOrBlank()) {
            Log.i("RestRequests", "Basic $auth")
            con.setRequestProperty("Authorization", "Basic $auth")
        }

        con.doOutput = true

        val os = con.outputStream
        os.write(body.toByteArray(Charset.defaultCharset()))
        os.flush()
        os.close()

        if (con.responseCode != 200) {
            Log.e("RestRequest", "An error ${con.responseCode} happened when making an api request")
            throw Exception("Error code ${con.responseCode}")
        }

        return con.inputStream.bufferedReader().use { it.readText() }
    }
}
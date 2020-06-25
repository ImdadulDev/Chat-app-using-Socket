package com.cbc.networkutils

import android.os.AsyncTask
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit


class JSONArrayPost(
    val url: String,
    val params: JSONArray,
    val header: HashMap<String, Any>?,
    val listner: Responselistener
) : AsyncTask<Void, String, String?>() {
    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        Klog.d("## REQ", "$params")
        Klog.d("## RES", result.toString())
        if (result == null) {
            listner.OnError("Something Went Wrong please try again")
        } else {

            try {
                val respCode = JSONObject(result).optInt("response_code")
                when (respCode) {
                    2000 -> listner.OnSucess(result)
                    5002 -> listner.OnLogicError(
                        respCode,
                        result,
                        JSONObject(result).optString("response_message")
                    )
                    5005 -> listner.OnError("INTERNAL DB ERROR")
                    2008 -> listner.OnLogicError(
                        respCode,
                        result,
                        JSONObject(result).optString("response_message")
                    )
                    4000 -> listner.OnError("Authentication failed.")
                    5010 -> listner.OnError("Not verified account.")
                    5015 -> listner.OnError("Already verified your account.")
                    5020 -> listner.OnError("Password is wrong.")
                    5025 -> listner.OnError("User account is inactive.")
                    2002 -> listner.OnError("Already sent request.")
                    2001 -> listner.OnError("No provider found.")
                    else -> listner.OnError("Unknown error please try again later.")
                }


            } catch (e: JSONException) {
                e.printStackTrace()
                listner.OnError("Something Went Wrong please try again later.")
            }

        }
    }

    override fun onPreExecute() {
        super.onPreExecute()
        listner.Start()
    }

    override fun doInBackground(vararg p0: Void?): String? {
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()


        val MEDIA_TYPE = "application/json".toMediaTypeOrNull()
//        final MediaType MEDIA_TYPE_PNG = MediaType.parse("*/*");

        val bodybuilder = MultipartBody.Builder()
        bodybuilder.setType(MultipartBody.FORM)


        val body = RequestBody.create(MEDIA_TYPE, params.toString())

        val builder = Headers.Builder()
        if (header != null)
            for (entry in header.entries) {
                println(entry.key + "/" + entry.value)
                builder.add(entry.key, entry.value.toString())
            }
        val h = builder.build()

        val request = Request.Builder()
            .url(url)
            .post(body)
            .addHeader("content-type", "application/json")
            .headers(h)
            .build()


        try {
            val response = client.newCall(request).execute()

            return response.body!!.string()
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

    }
}



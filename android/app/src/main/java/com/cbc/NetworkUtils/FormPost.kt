package com.cbc.NetworkUtils

import android.os.AsyncTask
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class FormPost(
    val url: String,
    val params: HashMap<String, Any>,
    val header: HashMap<String, Any>?,
    val listner: Responselistener
) : AsyncTask<Void, String, String?>() {
    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        if (result == null) {
            listner.OnError("Something Went Wrong please try again")
        } else {

            try {
                //Klog.d("## FORM RESPONSE_", result)
                val respCode = JSONObject(result).optInt("response_code")
                when (respCode) {
                    2000 -> listner.OnSucess(result)
                    5002 -> listner.OnError(JSONObject(result).optString("response_message"))
                    5005 -> listner.OnError("INTERNAL DB ERROR")
                    2008 -> listner.OnError(JSONObject(result).optString("response_message"))
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


        val MEDIA_TYPE_PNG = "image/jpeg".toMediaTypeOrNull()
//        final MediaType MEDIA_TYPE_PNG = MediaType.parse("*/*");

        val bodybuilder = MultipartBody.Builder()
        bodybuilder.setType(MultipartBody.FORM)


        for (entry in params.entries) {
            if (entry.value is File) {
                val Requploadfile = entry.value as File
                Klog.d("## PATH FI-", Requploadfile.path)
                bodybuilder.addFormDataPart(
                    entry.key,
                    Requploadfile.name,
                    RequestBody.create(MEDIA_TYPE_PNG, Requploadfile)
                )
                //            /storage/emulated/0/Pictures/1564988965316.jpg
            } else
                bodybuilder.addFormDataPart(entry.key, entry.value.toString())
        }
        val requestBody = bodybuilder
            .build()

        val builder = Headers.Builder()
        if (header != null)
            for (entry in header.entries) {
                println(entry.key + "/" + entry.value)
                builder.add(entry.key, entry.value.toString())
            }
        val h = builder.build()

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            //                .addHeader("content-type", "multipart/form-data")
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



package com.cbc.utils

import android.content.Context
import com.cbc.R
import com.cbc.models.country.Countrydata
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.StringWriter

object CuntryHelper {

    fun GetAllList(context: Context): Countrydata {
        val iss = context.resources.openRawResource(R.raw.countrycallingcodes)
        val writer = StringWriter()
        val buffer = CharArray(1024)
        try {
            val reader = BufferedReader(InputStreamReader(iss, "UTF-8"))
            var n: Int
            n = reader.read(buffer)
            while (n != -1) {
                writer.write(buffer, 0, n)
                n = reader.read(buffer)
            }
        } finally {
            iss.close()
        }

        val countryjson = writer.toString()
        val countryList = Gson().fromJson(countryjson, Countrydata::class.java)
        return countryList
    }

    fun getCallingCode(context: Context, countrycode: String): String {
        val iss = context.resources.openRawResource(R.raw.countrycallingcodes)
        val writer = StringWriter()
        val buffer = CharArray(1024)
        try {
            val reader = BufferedReader(InputStreamReader(iss, "UTF-8"))
            var n: Int
            n = reader.read(buffer)
            while (n != -1) {
                writer.write(buffer, 0, n)
                n = reader.read(buffer)
            }
        } finally {
            iss.close()
        }

        val countryjson = writer.toString()
        val countryList = Gson().fromJson(countryjson, Countrydata::class.java)
        countryList.country.forEach {
            if (it.code.equals(countrycode, true))
                return "+${it.callingCode}"
        }
        return "+91"
    }
}
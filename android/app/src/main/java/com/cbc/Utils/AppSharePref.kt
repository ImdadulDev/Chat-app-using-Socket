package com.app.RitoRideSharingAppPassenger.Utils

import android.content.Context
import com.cbc.models.LoginResponse
import com.google.gson.Gson

const val PREFNAME = "cbc"
const val PASENGERDATA = "passengerdata"
const val CONTACTSYNC = "contactsync"

class AppSharePref(val mContext: Context) {
    val sharedPreferences = mContext.getSharedPreferences(PREFNAME, Context.MODE_PRIVATE)


    fun isContactBackedUp(boolean: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(CONTACTSYNC, boolean)
        editor.apply()
    }

    fun isContactbackedUp(): Boolean {
        return sharedPreferences.getBoolean(CONTACTSYNC, false)
    }

    fun SetUSERDATA(userData: LoginResponse) {
        val editor = sharedPreferences.edit()
        editor.putString(PASENGERDATA, Gson().toJson(userData))
        editor.apply()
    }

    fun GetUSERDATA(): LoginResponse? {
        val res = sharedPreferences.getString(PASENGERDATA, "")
        return Gson().fromJson(res, LoginResponse::class.java)
    }

    /*fun GetAuthHeader(): HashMap<String, Any> {
        val res = sharedPreferences.getString(PASENGERDATA, "")
        val user = Gson().fromJson(res, LoginResponse::class.java)
        val param = HashMap<String, Any>()
        param.put("authtoken", LoginResponse.authToken)
        param.put("user_id", user.responseData.id)
        return param
    }*/

    fun LogOUT(): Boolean {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.commit()
        return true
    }
}
package com.cbc.utils

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

    fun setDeviceId(deviceId: String) {
        val editor = sharedPreferences.edit()
        editor.putString("dev_id", deviceId)
        editor.apply()
    }

    fun getDeviceId(): String? {
        return sharedPreferences.getString("dev_id", "")
    }


    fun SetDeviceRegUserId(_id: String) {
        val editor = sharedPreferences.edit()
        editor.putString("dev_userId", _id)
        editor.apply()
    }

    fun GetDeviceRegId(): String? {
        return sharedPreferences.getString("dev_userId", null)
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

    fun GetAuthHeader(): HashMap<String, Any> {
        val res = sharedPreferences.getString(PASENGERDATA, "")
        val user = Gson().fromJson(res, LoginResponse::class.java)
        val param = HashMap<String, Any>()
        param.put("authtoken", user.authToken)
//         param.put("user_id", user.responseData.id)
        return param
    }

    fun setUserChatToken(chatToken: String) {
        val editor = sharedPreferences.edit()
        editor.putString("chatToken", chatToken)
        editor.apply()
    }

    fun getUserChatToken(): String? {
        return sharedPreferences.getString("chatToken", "not found")
        //return Constants.CHAT_TOKEN
    }

    fun LogOUT(): Boolean {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.commit()
        return true
    }
}
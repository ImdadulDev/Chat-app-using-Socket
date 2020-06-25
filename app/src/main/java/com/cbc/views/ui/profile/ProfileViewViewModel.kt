package com.cbc.views.ui.profile

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.cbc.networkutils.FormPost
import com.cbc.networkutils.Responselistener
import com.cbc.networkutils.URLs
import com.cbc.utils.AppSharePref
import com.cbc.utils.AuthExpire
import com.cbc.views.ui.profile.model.Profile
import com.google.gson.Gson

class ProfileViewViewModel(application: Application) : AndroidViewModel(application) {
    val mContext = application.applicationContext

    val header = AppSharePref(mContext).GetAuthHeader()
    val profiledata = MutableLiveData<Profile>().apply {
        val param = HashMap<String, Any>()
        param.put("rr", "ee") // JUST FOR FormPost handle
        FormPost(URLs.GETPROFILE, param, header, object : Responselistener {
            override fun OnSucess(Responseobject: String?) {

                value = Gson().fromJson(Responseobject, Profile::class.java)
            }

            override fun OnError(Error: String?) {

                Toast.makeText(mContext, Error, Toast.LENGTH_LONG).show()
            }

            override fun Start() {

            }

            override fun OnLogicError(Code: Int, response: String?, msg: String?) {
                Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show()
                value = null
                if (Code == 4000) {
                    AuthExpire(mContext).logout()
                }
            }

        }).execute()
    }
}

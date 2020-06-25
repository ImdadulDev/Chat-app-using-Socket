package com.cbc.localdb

import android.content.Context
import android.util.Log
import com.cbc.interfaces.OnServerFetchListener
import com.cbc.networkutils.FormPost
import com.cbc.networkutils.Responselistener
import com.cbc.utils.Klog
import com.cbc.models.contactResponse.GetContactResponse
import com.cbc.networkutils.URLs
import com.cbc.utils.AppSharePref
import com.google.gson.Gson
import java.util.concurrent.Executors

class CBCgetContactFromServer(val mContext: Context) {
    val header = AppSharePref(mContext).GetAuthHeader()
    val cbcDatabase = CBCDatabase.getDatabase(mContext)
    val contactdao = cbcDatabase.contctDao()
    val excecuterService = Executors.newSingleThreadExecutor()

    fun get(listner: OnServerFetchListener) {
        val userId = AppSharePref(mContext).GetUSERDATA()?.id
        val param = HashMap<String, Any>()
        if (userId != null) {
            param["user_id"] = userId
        }
        FormPost(URLs.GETCONTCATLIST, param, header, object : Responselistener {
            override fun OnSucess(Responseobject: String?) {
                //Log.d("---RESPONSE-", "Header-$header   RES-$Responseobject!!")
                val responselist = Gson().fromJson(
                    Responseobject,
                    GetContactResponse::class.java
                )
                excecuterService.execute {
                    contactdao.deleteAllBackedUp()
                    contactdao.insertAllDeviceContact(responselist.responseData)
                }
                listner.onServerComplete()
            }

            override fun OnError(Error: String?) {
                Klog.D("## Error-", Error!!)
                listner.onFetchError(Error)
            }

            override fun Start() {

            }

            override fun OnLogicError(Code: Int, response: String?, msg: String?) {
                Klog.D("## LogicError-", response!!)
                listner.onFetchError(msg!!)
            }

        }).execute()

    }

}
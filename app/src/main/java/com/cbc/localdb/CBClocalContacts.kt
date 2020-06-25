package com.cbc.localdb

import android.content.Context
import android.os.AsyncTask
import com.cbc.views.ui.home.OnFetchDeviceContactListener

class CBClocalContacts(val mcontext: Context, val listener: OnFetchDeviceContactListener) :
    AsyncTask<Void, Void, List<ContactEntity>>() {
    val cbcDatabase = CBCDatabase.getDatabase(mcontext)
    val contactdao = cbcDatabase.contctDao()

    override fun doInBackground(vararg p0: Void?): List<ContactEntity> {
        val list = contactdao.getDeviceData(false)
        list.forEach {
            it.is_cbc_backedup = true
        }

        return list
    }

    override fun onPostExecute(result: List<ContactEntity>?) {
        super.onPostExecute(result)
        listener.onFetchLocalContacts(result!!, false)
    }


}
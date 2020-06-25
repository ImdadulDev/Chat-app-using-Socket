package com.cbc.View.ui.home

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cbc.Localdb.CBCDatabase
import com.cbc.Localdb.ContactEntity
import com.cbc.NetworkUtils.Klog

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val valueofcontact = MutableLiveData<ContactEntity>().apply {
        val context = getApplication<Application>().applicationContext
        val cbcDatabase = CBCDatabase.getDatabase(context)
        object : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg p0: Void?): Void? {

                cbcDatabase.contctDao().getAll().forEach {
                    Klog.d("## name-", "${it.displayname}")
                }
                return null
            }

        }.execute()
    }
    val text: LiveData<ContactEntity> = valueofcontact
}
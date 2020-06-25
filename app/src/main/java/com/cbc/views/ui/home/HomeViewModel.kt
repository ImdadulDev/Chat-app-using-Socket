package com.cbc.views.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.cbc.localdb.CBCDatabase
import com.cbc.localdb.ContactEntity
import com.cbc.utils.AppSharePref
import java.util.concurrent.Executors

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    val context = getApplication<Application>().applicationContext
    val cbcDatabase = CBCDatabase.getDatabase(context)
    val contactdao = cbcDatabase.contctDao()
    val excecuterService = Executors.newSingleThreadExecutor()
    val header = AppSharePref(context).GetAuthHeader()
    val text: LiveData<List<ContactEntity>> = contactdao.getAll()


    fun isTrustedDevice(): Boolean {
        val appSharePref = AppSharePref(context)
        if (appSharePref.GetDeviceRegId() == null)
            return false
        else if (appSharePref.GetDeviceRegId().equals(appSharePref.GetUSERDATA()!!.id))
            return true
        else
            return false
    }


    fun updateServerToUpdateAll() {
        excecuterService.execute {
            contactdao.UpdateAlltoUploaded()
        }
    }

    fun update(it: ContactEntity) {
        excecuterService.execute {
            contactdao.UpdateOne(it)
        }
    }

    fun updateAll(it: List<ContactEntity>) {
        excecuterService.execute {
            contactdao.UpdateAll(it)
        }
    }


}
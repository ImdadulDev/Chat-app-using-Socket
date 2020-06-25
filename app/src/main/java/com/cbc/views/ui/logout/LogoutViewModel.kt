package com.cbc.views.ui.logout

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cbc.localdb.CBCDatabase
import java.util.concurrent.Executors

class LogoutViewModel(application: Application) : AndroidViewModel(application) {
    val context = getApplication<Application>().applicationContext
    val cbcDatabase = CBCDatabase.getDatabase(context)
    val contactdao = cbcDatabase.contctDao()
    val excecuterService = Executors.newSingleThreadExecutor()
    private val _text = MutableLiveData<String>().apply {
        value = " Logout "
    }
    val text: LiveData<String> = _text

    fun logout() {
        excecuterService.execute {
            contactdao.deleteall()
        }

    }
}
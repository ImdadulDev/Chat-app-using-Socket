package com.cbc.utils

import android.content.Context
import android.content.Intent
import com.cbc.localdb.CBCDatabase
import com.cbc.views.MainActivity
import com.cbc.views.SplashActivity
import java.util.concurrent.Executors

class AuthExpire(val mContext: Context) {
    val cbcDatabase = CBCDatabase.getDatabase(mContext)
    val contactdao = cbcDatabase.contctDao()
    val excecuterService = Executors.newSingleThreadExecutor()

    fun logout() {
        excecuterService.execute {
            contactdao.deleteall()
        }
        AppSharePref(mContext).LogOUT()
        when (mContext) {
            mContext as MainActivity -> {
                mContext.startActivity(Intent(mContext, SplashActivity::class.java))
                (mContext as MainActivity).finish()
            }
        }

    }
}
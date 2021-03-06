package com.cbc.views

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.cbc.contactsyncutils.AutoSyncToServer
import com.cbc.interfaces.OnServerFetchListener
import com.cbc.localdb.CBCDatabase
import com.cbc.localdb.CBCgetContactFromServer
import com.cbc.R
import com.cbc.utils.AppSharePref
import com.cbc.utils.ManagePermissions
import java.util.concurrent.Executors

class SplashActivity : AppCompatActivity() {
    private val PermissionsRequestCode = 123
    val excecuterService = Executors.newSingleThreadExecutor()
    private lateinit var managePermissions: ManagePermissions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()

        getAndSaveDeviceToken()

        val reqlist = listOf(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )

        val userdao = CBCDatabase.getDatabase(this).userDao()
        excecuterService.execute {
            val reg_user = userdao.getDeviceRegisterUserID()
            if (reg_user != null)
                AppSharePref(this).SetDeviceRegUserId(reg_user._id)
        }

        managePermissions = ManagePermissions(this, reqlist, PermissionsRequestCode)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ) {
                managePermissions.checkPermissions()
            } else
                continueNext()
        else
            continueNext()
    }

    private fun continueNext() {
        Handler().postDelayed({
            if (AppSharePref(this).GetUSERDATA() == null)
                startActivity(Intent(this, MapActivity::class.java))
            else {
                CBCgetContactFromServer(this@SplashActivity).get(object : OnServerFetchListener {
                    override fun onServerComplete() {
                        if (AppSharePref(this@SplashActivity).GetDeviceRegId() != null && AppSharePref(
                                this@SplashActivity
                            ).GetDeviceRegId()
                                .equals(AppSharePref(this@SplashActivity).GetUSERDATA()!!.id)
                        ) {
                            AutoSyncToServer(this@SplashActivity).execute()
                            /*** BACKUP will execute when condition is satisfied ***/
                        }
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        finish()
                    }

                    override fun onFetchError(errormsg: String) {
//                        Toast.makeText(this@SplashActivity, errormsg,Toast.LENGTH_LONG).show()
                        startActivity(Intent(this@SplashActivity, MapActivity::class.java))
                        finish()
                    }
                })
            }

        }, 2000)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PermissionsRequestCode -> {
                val isPermissionsGranted = managePermissions
                    .processPermissionsResult(requestCode, permissions, grantResults)

                if (isPermissionsGranted) {
                    continueNext()
                } else {
                    managePermissions.checkPermissions()
                }
                return
            }
        }
    }

    private fun getAndSaveDeviceToken(){
        try {
            val androidId = Settings.Secure.getString(applicationContext.getContentResolver(), Settings.Secure.ANDROID_ID)
            Log.d("----", "Android Id: $androidId")
            AppSharePref(applicationContext).setDeviceId(androidId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}

package com.cbc.View

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.app.RitoRideSharingAppPassenger.Utils.AppSharePref
import com.cbc.Localdb.saveDevicedata
import com.cbc.R
import com.cbc.utils.ManagePermissions
import okio.Source

class SplashActivity : AppCompatActivity() {
    private val PermissionsRequestCode = 123
    private lateinit var managePermissions: ManagePermissions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()
        val reqlist = listOf<String>(Manifest.permission.READ_CONTACTS)

        val deviceid = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)


        managePermissions = ManagePermissions(this, reqlist, PermissionsRequestCode)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                managePermissions.checkPermissions()
            } else
                continuenext()
        else
            continuenext()


    }

    private fun continuenext() {
        saveDevicedata(this).execute()
        Handler().postDelayed({
            if (AppSharePref(this).GetUSERDATA() == null)
                startActivity(Intent(this, MapActivity::class.java))
            else
                startActivity(Intent(this, MainActivity::class.java))
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            finish()
        }, 3000)
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
                    continuenext()
                } else {
                    managePermissions.checkPermissions()
                }
                return
            }
        }
    }
}

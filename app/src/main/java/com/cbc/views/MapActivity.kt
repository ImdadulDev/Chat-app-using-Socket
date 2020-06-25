package com.cbc.views

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.cbc.R
import kotlinx.android.synthetic.main.activity_map.*

class MapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        Log.i("TAG", "SERIAL: " + Build.SERIAL)
        Log.i("TAG", "MODEL: " + Build.MODEL)
        Log.i("TAG", "ID: " + Build.ID)
        Log.i("TAG", "Manufacture: " + Build.MANUFACTURER)
        Log.i("TAG", "brand: " + Build.BRAND)
        Log.i("TAG", "type: " + Build.TYPE)
        Log.i("TAG", "user: " + Build.USER)
        Log.i("TAG", "BASE: " + Build.VERSION_CODES.BASE)
        Log.i("TAG", "INCREMENTAL " + Build.VERSION.INCREMENTAL)
        Log.i("TAG", "SDK  " + Build.VERSION.SDK_INT)
        Log.i("TAG", "BOARD: " + Build.BOARD)
        Log.i("TAG", "BRAND " + Build.BRAND)
        Log.i("TAG", "HOST " + Build.HOST)
        Log.i("TAG", "FINGERPRINT: " + Build.FINGERPRINT)
        Log.i("TAG", "Version Code: " + Build.VERSION.RELEASE)

        TXT_SignIn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        TXT_SignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}

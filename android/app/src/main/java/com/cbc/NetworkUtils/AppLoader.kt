package com.cbc.NetworkUtils

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.cbc.R

class AppLoader(val mcontext: Context) : Dialog(mcontext) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loader)
        setCanceledOnTouchOutside(false)
        setCancelable(false)
    }
}
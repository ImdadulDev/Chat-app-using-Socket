package com.cbc.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.cbc.R
import com.cbc.dialoglistener.SimpleOkListener
import kotlinx.android.synthetic.main.simplealert.*

class SimpleAlertDialog(context: Context, val msg: String, val lis: SimpleOkListener) :
    Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCancelable(false)
        setContentView(R.layout.simplealert)
        AlertMsg.text = msg
        BTN_OK.setOnClickListener {
            lis.OnOk()
            dismiss()
        }

    }
}
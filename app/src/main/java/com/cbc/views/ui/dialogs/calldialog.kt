package com.cbc.views.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import com.cbc.localdb.ContactEntity
import com.cbc.R
import com.cbc.twilo.VoiceActivity
import kotlinx.android.synthetic.main.dialog_call.*

class calldialog(context: Context, val item: ContactEntity) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_call)

        CALL_PHONE.setOnClickListener {
            val callintent = Intent(context, VoiceActivity::class.java)
            callintent.putExtra("mob_no", item.mobile_no)
            context.startActivity(callintent)
            dismiss()
        }

        CALL_CBC.setOnClickListener {
            val toast_msg = Toast.makeText(context, "Under Development", Toast.LENGTH_LONG)
            toast_msg.setGravity(Gravity.CENTER, 0, 0)
            toast_msg.show()
            dismiss()
        }
    }
}
package com.cbc.views

import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.cbc.networkutils.AppLoader
import com.cbc.networkutils.FormPost
import com.cbc.networkutils.Responselistener
import com.cbc.R
import com.cbc.dialoglistener.SimpleOkListener
import com.cbc.dialogs.SimpleAlertDialog
import com.cbc.networkutils.URLs
import com.cbc.utils.AppSharePref
import com.cbc.utils.Vaidator
import com.github.florent37.viewtooltip.ViewTooltip
import kotlinx.android.synthetic.main.activity_change_password.*
import org.json.JSONObject

class ChangePasswordActivity : AppCompatActivity(), View.OnClickListener {
    val info_password =
        "Password length will be 6 to 8\n" +
                "characters. One Upper Case One\n" +
                "Lower Case along with the special character."
    lateinit var appshre: AppSharePref
    lateinit var loader: AppLoader
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        supportActionBar!!.title = "Change password"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        appshre = AppSharePref(this)
        loader = AppLoader(this)
        EDX_PASSWORD.setOnFocusChangeListener { view, b ->
            if (!b) {
                ViewTooltip.on(this, EDX_PASSWORD)
                    .position(ViewTooltip.Position.TOP)
                    .corner(30)
//                    .customView(R.layout.tooltips)
                    .clickToHide(true)
                    .autoHide(true, 3000)
                    .text(info_password)
                    .color(Color.WHITE)
                    .textColor(Color.BLACK)
                    .show()
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(p0: View?) {
        if (EDX_OLDPASSWORD.text.isNullOrEmpty()) {
            EDX_OLDPASSWORD.requestFocus()
            EDX_OLDPASSWORD.setHintTextColor(Color.RED)
            return
        }

        if (EDX_PASSWORD.text.isNullOrEmpty()) {
            EDX_PASSWORD.requestFocus()
            EDX_PASSWORD.setHintTextColor(Color.RED)
            return
        }

        if (!Vaidator.isValidPassword(EDX_PASSWORD, false)) {
            EDX_PASSWORD.requestFocus()
            return
        }

        if (EDX_CPASS.text.isNullOrEmpty()) {
            EDX_CPASS.requestFocus()
            EDX_CPASS.setHintTextColor(Color.RED)
            return
        }
        if (!EDX_PASSWORD.text.toString().equals(EDX_CPASS.text.toString())) {
            EDX_CPASS.requestFocus()
            EDX_CPASS.setError("Password mismatch.")
            return
        }

        ChangePASSWORD()
    }

    private fun ChangePASSWORD() {
        val header = HashMap<String, Any>()
        header.put("authtoken", appshre.GetUSERDATA()!!.authToken)
        header.put("Content-Type", "application/x-www-form-urlencoded")

        val param = HashMap<String, Any>()
        param.put("_id", appshre.GetUSERDATA()!!.id)
        param.put("currentpassword", EDX_OLDPASSWORD.text.toString())
        param.put("password", EDX_PASSWORD.text.toString())
        FormPost(URLs.CHANGEPASSWORD, param, header, object : Responselistener {
            override fun OnSucess(Responseobject: String?) {
                loader.dismiss()
                val msg = JSONObject(Responseobject).optString("response_message")
                SimpleAlertDialog(this@ChangePasswordActivity, msg, object : SimpleOkListener {
                    override fun OnOk() {
                        finish()
                    }

                }).show()
            }

            override fun OnError(Error: String?) {
                loader.dismiss()
                SimpleAlertDialog(this@ChangePasswordActivity, Error!!, object : SimpleOkListener {
                    override fun OnOk() {

                    }

                }).show()
            }

            override fun Start() {
                loader.show()
            }

            override fun OnLogicError(Code: Int, response: String?, msg: String?) {
                loader.dismiss()
                SimpleAlertDialog(this@ChangePasswordActivity, msg!!, object : SimpleOkListener {
                    override fun OnOk() {

                    }

                }).show()
            }

        }).execute()
    }
}

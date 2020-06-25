package com.cbc.views

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cbc.R
import com.cbc.dialoglistener.OnOTPListener
import com.cbc.dialoglistener.OnPasswordChangeListener
import com.cbc.dialoglistener.SimpleOkListener
import com.cbc.dialogs.CHANGEbottomSheetDialog
import com.cbc.dialogs.OTPbottomSheetDialog
import com.cbc.dialogs.SimpleAlertDialog
import com.cbc.networkutils.*
import com.cbc.utils.Vaidator
import kotlinx.android.synthetic.main.activity_forgot_password.*
import org.json.JSONObject

class ForgotPasswordActivity : AppCompatActivity(), OnOTPListener, OnPasswordChangeListener {
    override fun SuccessfullyPasswordChnaged(response: String?) {
        Klog.d("## TEST-", response!!)
        val JOBJ = JSONObject(response)
        SimpleAlertDialog(this, JOBJ.optString("response_message"), object : SimpleOkListener {
            override fun OnOk() {
                finish()
            }

        }).show()
    }

    override fun OnOTPVerified(response: String?) {
        val OPTOBJECT = JSONObject(response).optJSONObject("response_data")
        val changepassword = CHANGEbottomSheetDialog.newInstance(
            OPTOBJECT.optString("token"),
            OPTOBJECT.optString("userId"),
            1
        )
        changepassword.show(supportFragmentManager, "")

    }

    override fun OnOTPFailed(error: String?) {

    }


    lateinit var loader: AppLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        loader = AppLoader(this)
        EDX_MOB.setOnClickListener {
            startActivityForResult(Intent(this, MobileActivity::class.java), REQUEST_PHONE)
        }



        IMG_BACK.setOnClickListener {
            finish()
        }

        SEND.setOnClickListener {
//            if (EDX_EMAIL.text.isNullOrEmpty() and  EDX_MOB.text.isNullOrEmpty())
//            {
//                EDX_MOB.requestFocus()
//                EDX_MOB.setHintTextColor(Color.RED)
//                return@setOnClickListener
//            }
//
//            if (EDX_MOB.text.isNotEmpty())
//            {
//                CallForOtp()
//            }

            if (EDX_EMAIL.text.isNullOrEmpty()) {
                EDX_EMAIL.requestFocus()
                EDX_EMAIL.setHintTextColor(Color.RED)
                return@setOnClickListener
            }
            if (!Vaidator.isValideEmail(EDX_EMAIL.text.toString())) {
                EDX_EMAIL.error = "Enter valid email."
                return@setOnClickListener
            }
            CALL_FORGOTPASSWORD()
        }


    }

    private fun CALL_FORGOTPASSWORD() {
        val params = HashMap<String, Any>()
        params.put("email", EDX_EMAIL.text.toString())
        JSONMAPPost(URLs.FORGOTPASSWORD, params, null, object : Responselistener {
            override fun Start() {
                if (!loader.isShowing)
                    loader.show()
            }

            override fun OnSucess(Responseobject: String?) {
                loader.dismiss()
                val msg = JSONObject(Responseobject).optString("response_message")

                SimpleAlertDialog(this@ForgotPasswordActivity, msg!!, object : SimpleOkListener {
                    override fun OnOk() {
                        finish()
                    }

                }).show()
            }

            override fun OnError(Error: String?) {
                loader.dismiss()
                SimpleAlertDialog(this@ForgotPasswordActivity, Error!!, object : SimpleOkListener {
                    override fun OnOk() {

                    }

                }).show()
            }

            override fun OnLogicError(Code: Int, response: String?, msg: String?) {
                loader.dismiss()
                SimpleAlertDialog(this@ForgotPasswordActivity, msg!!, object : SimpleOkListener {
                    override fun OnOk() {

                    }

                }).show()
            }

        }).execute()
    }

//    private fun CallForOtp() {
//        val dailog=CHANGEbottomSheetDialog.newInstance("","",0)
//        dailog.showNow(supportFragmentManager,"")
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK)
            when (requestCode) {
                REQUEST_PHONE -> {
                    val code = data!!.getStringExtra("code")
                    val number = data!!.getStringExtra("number")
                    EDX_MOB.setText("+$code $number")
                }
            }
    }

    private fun SENDOTP(mob: String) {
        val params = HashMap<String, Any>()
        params.put("mobile", mob)
        JSONMAPPost(URLs.SENDOTP, params, null, object : Responselistener {
            override fun Start() {
                if (!loader.isShowing)
                    loader.show()
            }

            override fun OnSucess(Responseobject: String?) {
                loader.dismiss()
                val otpdialog = OTPbottomSheetDialog.newInstance(Responseobject!!, "")
                otpdialog.show(supportFragmentManager, "")
            }

            override fun OnError(Error: String?) {
                loader.dismiss()
                SimpleAlertDialog(this@ForgotPasswordActivity, Error!!, object : SimpleOkListener {
                    override fun OnOk() {

                    }

                }).show()
            }

            override fun OnLogicError(Code: Int, response: String?, msg: String?) {
                loader.dismiss()
                SimpleAlertDialog(this@ForgotPasswordActivity, msg!!, object : SimpleOkListener {
                    override fun OnOk() {

                    }

                }).show()
            }

        }).execute()


    }
}

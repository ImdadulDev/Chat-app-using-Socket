package com.cbc.View

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.CBC.DialogListener.OnOTPListener
import com.CBC.DialogListener.OnPasswordChangeListener
import com.CBC.DialogListener.SimpleOkListener
import com.CBC.Dialogs.CHANGEbottomSheetDialog
import com.CBC.Dialogs.OTPbottomSheetDialog
import com.CBC.Dialogs.SimpleAlertDialog
import com.app.RitoRideSharingAppPassenger.NetworkUtils.JSONMAPPost
import com.app.RitoRideSharingAppPassenger.NetworkUtils.URLs
import com.cbc.NetworkUtils.AppLoader
import com.cbc.NetworkUtils.Klog
import com.cbc.NetworkUtils.Responselistener
import com.cbc.R
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
//        BACK.setOnClickListener {
//            finish()
//        }


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

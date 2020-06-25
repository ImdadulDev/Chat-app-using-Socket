package com.cbc.View

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import androidx.appcompat.app.AppCompatActivity
import com.CBC.DialogListener.SimpleOkListener
import com.CBC.Dialogs.SimpleAlertDialog
import com.app.RitoRideSharingAppPassenger.NetworkUtils.URLs
import com.app.RitoRideSharingAppPassenger.Utils.AppSharePref
import com.cbc.NetworkUtils.AppLoader
import com.cbc.NetworkUtils.FormPost
import com.cbc.NetworkUtils.Responselistener
import com.cbc.R
import com.cbc.models.LoginResponse
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    lateinit var loader: AppLoader
    var devicetoken = "test"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loader = AppLoader(this)
        /*FirebaseApp.initializeApp(this)
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                devicetoken = task.result?.token!!
            })*/

        TXT_SignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        TXT_FORGOTPASSWORD.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        EDX_USERNAME.filters = arrayOf(object : InputFilter {
            override fun filter(
                source: CharSequence?,
                start: Int,
                end: Int,
                dest: Spanned?,
                dstart: Int,
                dend: Int
            ): CharSequence? {
                // eliminates single space
                if (end == 1) {
                    if (Character.isWhitespace(source?.get(0)!!)) {
                        return ""
                    }
                }
                return null
            }
        })

        BTN_LOGIN.setOnClickListener {
            if (EDX_USERNAME.text.isNullOrEmpty()) {
                EDX_USERNAME.requestFocus()
                EDX_USERNAME.setHintTextColor(Color.RED)
                return@setOnClickListener
            }

            if (EDX_PASSWORD.text.isNullOrEmpty()) {
                EDX_PASSWORD.requestFocus()
                EDX_PASSWORD.setHintTextColor(Color.RED)
                return@setOnClickListener
            }

            login()
        }
    }

    private fun login() {

        val params = HashMap<String, Any>()

        params.put("userName", EDX_USERNAME.text.toString())
        params.put("password", EDX_PASSWORD.text.toString())
        params.put("deviceToken", devicetoken)
        params.put("appType", "ANDROID")

        FormPost(URLs.LOGIN, params, null, object : Responselistener {
            override fun Start() {
                loader.show()
            }

            override fun OnSucess(Responseobject: String?) {
                loader.dismiss()
                val resp = JSONObject(Responseobject).optJSONObject("response_data")
                val userdata =
                    Gson().fromJson<LoginResponse>(resp.toString(), LoginResponse::class.java)
                AppSharePref(this@LoginActivity).SetUSERDATA(userdata)

                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()

            }

            override fun OnError(Error: String?) {
                loader.dismiss()
                SimpleAlertDialog(this@LoginActivity, Error!!, object : SimpleOkListener {
                    override fun OnOk() {

                    }

                }).show()

            }

            override fun OnLogicError(Code: Int, response: String?, msg: String?) {
                loader.dismiss()
                SimpleAlertDialog(this@LoginActivity, msg!!, object : SimpleOkListener {
                    override fun OnOk() {

                    }

                }).show()
            }

        }).execute()
    }
}

package com.cbc.views

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.cbc.contactsyncutils.AutoSyncToServer
import com.cbc.interfaces.OnServerFetchListener
import com.cbc.localdb.CBCgetContactFromServer
import com.cbc.networkutils.AppLoader
import com.cbc.networkutils.FormPost
import com.cbc.networkutils.Responselistener
import com.cbc.R
import com.cbc.dialoglistener.OnOTPListener
import com.cbc.dialoglistener.SimpleOkListener
import com.cbc.dialogs.OTPbottomSheetDialog
import com.cbc.dialogs.SimpleAlertDialog
import com.cbc.models.LoginResponse
import com.cbc.networkutils.URLs
import com.cbc.utils.AppSharePref
import com.cbc.utils.hideKeyboard
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.EDX_PASSWORD
import kotlinx.android.synthetic.main.activity_login.EDX_USERNAME
import kotlinx.android.synthetic.main.activity_login.IMG_BACK
import kotlinx.android.synthetic.main.activity_register.*
import org.json.JSONObject

class LoginActivity : AppCompatActivity(), OnOTPListener {
    lateinit var loader: AppLoader
    var devicetoken = "test"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loader = AppLoader(this)

        devicetoken = applicationContext?.let { AppSharePref(it).getDeviceId() }!!

        /*FirebaseApp.initializeApp(this)
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                devicetoken = task.result?.token!!
            })*/

        ll_login_hide_keyboard.setOnClickListener {
            applicationContext.hideKeyboard(ll_login_hide_keyboard)
        }

        TXT_SignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        IMG_BACK.setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
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
        params.put("userName", EDX_USERNAME.text.toString().trim())
        params.put("password", EDX_PASSWORD.text.toString().trim())
        params.put("deviceToken", devicetoken)
        params.put("appType", "ANDROID")

        FormPost(URLs.LOGIN, params, null, object : Responselistener {
            override fun Start() {
                loader.show()
            }

            override fun OnSucess(Responseobject: String?) {
                val resp = JSONObject(Responseobject).optJSONObject("response_data")
                Log.d("login", "Res----: $resp")
                Log.d("login", "Res chatToken----: ${resp.optString("chatToken")}")

                AppSharePref(applicationContext).setUserChatToken(resp.optString("chatToken"))

                val userdata =
                    Gson().fromJson(resp.toString(), LoginResponse::class.java)

                if (userdata.otpVerify.equals("0")) {
                    loader.dismiss()
                    val otpdialog =
                        OTPbottomSheetDialog.newInstance(Responseobject!!, userdata.phone)
                    otpdialog.show(supportFragmentManager, userdata.phone)
                } else
                    checkDeviceUSerExist(userdata)

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

    private fun checkDeviceUSerExist(userdata: LoginResponse) {

        if (AppSharePref(this).GetDeviceRegId() != null && AppSharePref(this).GetDeviceRegId()!!
                .equals(userdata.id)
        ) {
            AppSharePref(this).SetUSERDATA(userdata)
            CALL_FOR_FETCH_SERVER_DATA()

        } else {
            /*** CALL NEW PAGE FOR CONFIRM ****/
            startActivity(
                Intent(this, ActivityAskQuestion::class.java).putExtra(
                    "log_data",
                    userdata
                )
            )
//           AppSharePref(this).SetDeviceRegUserId(userdata.id)
             finish()
        }

    }

    private fun CALL_FOR_FETCH_SERVER_DATA() {
        if (loader.isShowing)
            loader.show()

        CBCgetContactFromServer(this@LoginActivity).get(object : OnServerFetchListener {
            override fun onServerComplete() {
                loader.dismiss()
                if (AppSharePref(this@LoginActivity).GetDeviceRegId() != null && AppSharePref(this@LoginActivity).GetDeviceRegId()
                        .equals(AppSharePref(this@LoginActivity).GetUSERDATA()!!.id)
                ) {
                    AutoSyncToServer(this@LoginActivity).execute()
                    /*** BACKUP will execute when condition is satisfied*/
                }
                startActivity(
                    Intent(
                        this@LoginActivity,
                        MainActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                )
                finish()
            }

            override fun onFetchError(errormsg: String) {
                loader.dismiss()
                SimpleAlertDialog(this@LoginActivity, errormsg, object : SimpleOkListener {
                    override fun OnOk() {

                    }

                }).show()
            }

        })
    }

    override fun OnOTPVerified(response: String?) {
        val resp = JSONObject(response).optJSONObject("response_data")
        val userdata = Gson().fromJson<LoginResponse>(resp.toString(), LoginResponse::class.java)
        checkDeviceUSerExist(userdata)
    }

    override fun OnOTPFailed(error: String?) {
        SimpleAlertDialog(this, error!!, object : SimpleOkListener {
            override fun OnOk() {
                TXT_SignIn.performClick()
            }

        }).show()
    }
}

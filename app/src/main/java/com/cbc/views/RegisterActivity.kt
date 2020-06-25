package com.cbc.views

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cbc.contactsyncutils.AutoSyncToServer
import com.cbc.localdb.CBCDatabase
import com.cbc.localdb.UserIdEntity
import com.cbc.R
import com.cbc.dialoglistener.OnOTPListener
import com.cbc.dialoglistener.SimpleOkListener
import com.cbc.dialogs.*
import com.cbc.utils.Vaidator
import com.cbc.models.LoginResponse
import com.cbc.networkutils.*
import com.cbc.utils.AppSharePref
import com.github.florent37.viewtooltip.ViewTooltip
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_register.*
import org.json.JSONObject
import java.util.concurrent.Executors

const val REQUEST_PHONE = 12

class RegisterActivity : AppCompatActivity(), SecurityQuestionDialog.OnSelectQuestion,
    OnOTPListener,
    View.OnClickListener {
    var code = ""
    var phNo = ""
    val excecuterService = Executors.newSingleThreadExecutor()

    override fun OnOTPVerified(response: String?) {
        val resp = JSONObject(response).optJSONObject("response_data")
        val userdata = Gson().fromJson<LoginResponse>(resp.toString(), LoginResponse::class.java)
        val userdao = CBCDatabase.getDatabase(this).userDao()
        val user_reg_local = UserIdEntity(userdata.id, userdata.email)
        AppSharePref(this).SetDeviceRegUserId(userdata.id)
        excecuterService.execute {
            userdao.deleteallregisteruser()
            userdao.insert(user_reg_local)
        }
        AppSharePref(this).SetUSERDATA(userdata)

        AutoSyncToServer(this).execute()
        startActivity(
            Intent(
                this,
                MainActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        finish()


    }

    override fun OnOTPFailed(error: String?) {
        SimpleAlertDialog(this, error!!, object : SimpleOkListener {
            override fun OnOk() {
                TXT_SignIn.performClick()
            }

        }).show()
    }

    val info_password =
        "Password should be minimum 8 characters long. One Upper Case One Lower Case along with the special character."

    private var Qno1: Int = 0
    private var Qno2 = 0
    lateinit var loader: AppLoader
    override fun onClick(p0: View?) {
        when (p0) {
            TXT_QUESTION1 -> SecurityQuestionDialog.newInstance(QUESTIONONE, 0).show(
                supportFragmentManager,
                ""
            )
            TXT_QUESTION2 -> SecurityQuestionDialog.newInstance(QUESTIONTWO, Qno1).show(
                supportFragmentManager,
                ""
            )
            TXT_SignIn -> {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            BTN_SIGNUP ->
                SignUp()
            IMG_BACK -> {
                finish()
            }
            EDX_MOB -> {
                startActivityForResult(Intent(this, MobileActivity::class.java), REQUEST_PHONE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK)
            when (requestCode) {
                REQUEST_PHONE -> {
                    code = data!!.getStringExtra("code")
                    phNo = data!!.getStringExtra("number")
                    EDX_MOB.setText("+$code $phNo")
                }
            }
    }

    private fun SignUp() {

        if (EDX_FNAME.text.isNullOrEmpty()) {
            EDX_FNAME.requestFocus()
            EDX_FNAME.setHintTextColor(Color.RED)
            Scroll.scrollTo(0, EDX_FNAME.bottom)
            return
        }

        if (EDX_LNAME.text.isNullOrEmpty()) {
            EDX_LNAME.requestFocus()
            EDX_LNAME.setHintTextColor(Color.RED)
            Scroll.scrollTo(0, EDX_LNAME.bottom)
            return
        }

        if (EDX_USERNAME.text.isNullOrEmpty()) {
            EDX_USERNAME.requestFocus()
            EDX_USERNAME.setHintTextColor(Color.RED)
            Scroll.scrollTo(0, EDX_USERNAME.bottom)
            return
        }

        if (EDX_MOB.text.isNullOrEmpty()) {
            EDX_MOB.requestFocus()
            EDX_MOB.setHintTextColor(Color.RED)
            Scroll.scrollTo(0, EDX_MOB.bottom)
            return
        }


        if (EDX_PASSWORD.text.isNullOrEmpty()) {
            EDX_PASSWORD.requestFocus()
            EDX_PASSWORD.setHintTextColor(Color.RED)
            Scroll.scrollTo(0, EDX_PASSWORD.bottom)
            return
        }

        if (!Vaidator.isValidPassword(EDX_PASSWORD)) {
            EDX_PASSWORD.requestFocus()
            Scroll.scrollTo(0, EDX_PASSWORD.bottom)
            return
        }

        if (EDX_CPASS.text.isNullOrEmpty()) {
            EDX_CPASS.requestFocus()
            EDX_CPASS.setHintTextColor(Color.RED)
            Scroll.scrollTo(0, EDX_CPASS.bottom)
            return
        }
        if (!EDX_PASSWORD.text.toString().equals(EDX_CPASS.text.toString())) {
            EDX_CPASS.requestFocus()
            EDX_CPASS.setError("Password mismatch.")
            Scroll.scrollTo(0, EDX_CPASS.bottom)
            return
        }

        if (!Vaidator.isValidEmail(EDX_EMAIL, false)) {
            EDX_EMAIL.requestFocus()
            Scroll.scrollTo(0, EDX_EMAIL.bottom)
            return
        }

        if (TXT_QUESTION1.text.isNullOrEmpty()) {
            TXT_QUESTION1.setHintTextColor(Color.RED)
            Scroll.scrollTo(0, TXT_QUESTION1.bottom)
            return
        }

        if (EDX_ANS1.text.isNullOrEmpty()) {
            EDX_ANS1.requestFocus()
//            Scroll.scrollTo(0, EDX_ANS1.bottom)
            return
        }

        if (TXT_QUESTION2.text.isNullOrEmpty()) {
            TXT_QUESTION2.setHintTextColor(Color.RED)
            Scroll.scrollTo(0, TXT_QUESTION2.bottom)
            return
        }
        if (EDX_ANS2.text.isNullOrEmpty()) {
            EDX_ANS2.requestFocus()
            Scroll.scrollTo(0, EDX_ANS2.bottom)
            return
        }
        Register()
    }

    private fun Register() {
        val params = HashMap<String, Any>()

        params.put("firstName", EDX_FNAME.text.toString().trim())
        params.put("lastName", EDX_LNAME.text.toString().trim())
        params.put("userName", EDX_USERNAME.text.toString().trim())
        params.put("phone", phNo.trim())
        params.put("email", EDX_EMAIL.text.toString().trim())
        params.put("password", EDX_PASSWORD.text.toString().trim())
        params.put("questionOne", TXT_QUESTION1.text.toString().trim())
        params.put("answerOne", EDX_ANS1.text.toString().trim())
        params.put("questionTwo", TXT_QUESTION2.text.toString().trim())
        params.put("answerTwo", EDX_ANS2.text.toString().trim())
        params.put("appType", "ANDROID")
        params.put("countryCode", "+$code")

        Log.d("----", ": regs countryCode $code")
        Log.d("----", ": regs phone $phNo")

//        Klog.D("## Req-",JSONObject(params.toString()).toString())
        val req = Gson().toJson(params)
        Klog.d("## REQ-", req)
        FormPost(URLs.REGISTER, params, null, object : Responselistener {
            override fun Start() {
                loader.show()
            }

            override fun OnSucess(Responseobject: String?) {
                Klog.d("## REGISTER-", Responseobject!!)

                val MObj = JSONObject(Responseobject)
                MObj.optJSONObject("response_data").put("countryCode", "+$code")
                Klog.d("## REGISTER-", MObj.toString())
                val otpdialog =
                    OTPbottomSheetDialog.newInstance(MObj.toString(), phNo)
                otpdialog.show(supportFragmentManager, phNo)
                loader.dismiss()
            }

            override fun OnError(Error: String?) {
                loader.dismiss()
                Toast.makeText(this@RegisterActivity, Error, Toast.LENGTH_LONG).show()
            }

            override fun OnLogicError(Code: Int, response: String?, msg: String?) {
                Toast.makeText(this@RegisterActivity, msg, Toast.LENGTH_SHORT).show()
                loader.dismiss()
            }

        }).execute()
    }

    override fun Select(auestionfor: Int, questionNo: Int, question: String) {
        Klog.d("## QF-$auestionfor", " Q-$questionNo  Ans-$question")
        when (auestionfor) {
            QUESTIONONE -> {
                TXT_QUESTION1.text = question
                TXT_QUESTION1.setPadding(170, 0, 0, 0)
                TXT_QUESTION1.gravity = Gravity.LEFT or Gravity.CENTER_VERTICAL
                TXT_QUESTION2.text = ""
                EDX_ANS2.setText("")
                Qno1 = questionNo
            }
            QUESTIONTWO -> {
                TXT_QUESTION2.text = question
                TXT_QUESTION2.setPadding(170, 0, 0, 0)
                TXT_QUESTION2.gravity = Gravity.LEFT or Gravity.CENTER_VERTICAL
                Qno2 = questionNo
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        EDX_PASSWORD.setOnFocusChangeListener { view, b ->
//            Klog.d("## BOOLEAN-"," ${Vaidator.isValidPassword(EDX_PASSWORD)}")
            if (!b && !Vaidator.isValidPassword(EDX_PASSWORD)) {

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
        loader = AppLoader(this)

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

    }
}

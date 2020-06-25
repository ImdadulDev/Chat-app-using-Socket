package com.cbc.View

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.CBC.DialogListener.OnOTPListener
import com.CBC.DialogListener.SimpleOkListener
import com.CBC.Dialogs.OTPbottomSheetDialog
import com.CBC.Dialogs.SimpleAlertDialog
import com.app.RitoRideSharingAppPassenger.NetworkUtils.URLs
import com.app.RitoRideSharingAppPassenger.Utils.AppSharePref
import com.cbc.Dialogs.QUESTIONONE
import com.cbc.Dialogs.QUESTIONTWO
import com.cbc.Dialogs.SecurityQuestionDialog
import com.cbc.NetworkUtils.AppLoader
import com.cbc.NetworkUtils.FormPost
import com.cbc.NetworkUtils.Klog
import com.cbc.NetworkUtils.Responselistener
import com.cbc.R
import com.cbc.utils.Vaidator
import com.cbc.models.LoginResponse
import com.github.florent37.viewtooltip.ViewTooltip
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_register.*
import org.json.JSONObject

class RegisterActivity : AppCompatActivity(), SecurityQuestionDialog.OnSelectQuestion,
    OnOTPListener,
    View.OnClickListener {
    override fun OnOTPVerified(response: String?) {
        val resp = JSONObject(response).optJSONObject("response_data")
        val userdata = Gson().fromJson<LoginResponse>(resp.toString(), LoginResponse::class.java)
        AppSharePref(this).SetUSERDATA(userdata)
        startActivity(Intent(this, MainActivity::class.java))
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
        "Password length will be 6 to 8 characters. One Upper Case One Lower Case along with the special character."

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

        if (!Vaidator.isValidPassword(EDX_PASSWORD, false)) {
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
        if (EDX_PASSWORD.text.equals(EDX_CPASS.text)) {
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

        params.put("firstName", EDX_FNAME.text.toString())
        params.put("lastName", EDX_LNAME.text.toString())
        params.put("userName", EDX_USERNAME.text.toString())
        params.put("phone", EDX_MOB.text.toString())
        params.put("email", EDX_EMAIL.text.toString())
        params.put("password", EDX_PASSWORD.text.toString())
        params.put("questionOne", TXT_QUESTION1.text.toString())
        params.put("answerOne", EDX_ANS1.text.toString())
        params.put("questionTwo", TXT_QUESTION2.text.toString())
        params.put("answerTwo", EDX_ANS2.text.toString())
        params.put("appType", "ANDROID")
        params.put("countryCode", "+91")

//        Klog.D("## Req-",JSONObject(params.toString()).toString())
        val req = Gson().toJson(params)
        Klog.d("## REQ-", req)
        FormPost(URLs.REGISTER, params, null, object : Responselistener {
            override fun Start() {
                loader.show()
            }

            override fun OnSucess(Responseobject: String?) {
                Klog.d("## REGISTER-", Responseobject!!)
                val otpdialog =
                    OTPbottomSheetDialog.newInstance(Responseobject, EDX_MOB.text.toString())
                otpdialog.show(supportFragmentManager, EDX_MOB.text.toString())
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
                TXT_QUESTION2.text = ""
                EDX_ANS2.setText("")
                Qno1 = questionNo
            }
            QUESTIONTWO -> {
                TXT_QUESTION2.text = question
                Qno2 = questionNo
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        EDX_PASSWORD.setOnFocusChangeListener { view, b ->
            if (b) {
                ViewTooltip.on(this, EDX_PASSWORD)
                    .position(ViewTooltip.Position.TOP)
                    .corner(30)
                    .clickToHide(true)
                    .autoHide(true, 30000)
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

package com.cbc.views

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.cbc.contactsyncutils.AutoSyncToServer
import com.cbc.dialogs.QUESTIONONE
import com.cbc.dialogs.QUESTIONTWO
import com.cbc.dialogs.SecurityQuestionDialog
import com.cbc.interfaces.OnServerFetchListener
import com.cbc.localdb.CBCDatabase
import com.cbc.localdb.CBCgetContactFromServer
import com.cbc.localdb.UserIdEntity
import com.cbc.R
import com.cbc.dialoglistener.SimpleOkListener
import com.cbc.dialogs.SimpleAlertDialog
import com.cbc.models.LoginResponse
import com.cbc.networkutils.*
import com.cbc.utils.AppSharePref
import kotlinx.android.synthetic.main.activity_ask_question.*
import org.json.JSONObject
import java.util.concurrent.Executors

class ActivityAskQuestion : AppCompatActivity(), View.OnClickListener,
    SecurityQuestionDialog.OnSelectQuestion {
    lateinit var login_res: LoginResponse
    private var Qno1: Int = 0
    private var Qno2 = 0
    lateinit var loader: AppLoader
    val excecuterService = Executors.newSingleThreadExecutor()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ask_question)
        login_res = intent.getParcelableExtra("log_data")
        loader = AppLoader(this)
    }

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
                AppSharePref(this).SetUSERDATA(login_res)
                CALL_FOR_FETCH_SERVER_DATA()
            }
            TXT_FORGOT -> {
                ForGotQuestion(login_res.email)
            }

            BTN_SIGNUP ->
                SignUp()
        }

    }

    private fun SignUp() {
        val params = HashMap<String, Any>()
        val Header = HashMap<String, Any>()
        Header.put("authtoken", login_res.authToken)

        params.put("questionOne", TXT_QUESTION1.text.toString().trim())
        params.put("answerOne", EDX_ANS1.text.toString().trim())
        params.put("questionTwo", TXT_QUESTION2.text.toString().trim())
        params.put("answerTwo", EDX_ANS2.text.toString().trim())
        Klog.d("## PARAMS-", params.toString().trim())
        Klog.d("## HEADER-", Header.toString().trim())

        FormPost(URLs.CHECKQUESTION, params, Header, object : Responselistener {
            override fun Start() {
                loader.show()
            }

            override fun OnSucess(Responseobject: String?) {
//                loader.dismiss()
                val userdao = CBCDatabase.getDatabase(this@ActivityAskQuestion).userDao()
                AppSharePref(this@ActivityAskQuestion).SetDeviceRegUserId(login_res.id)
                AppSharePref(this@ActivityAskQuestion).SetUSERDATA(login_res)
                val user_reg_local = UserIdEntity(login_res.id, login_res.email)
                AppSharePref(this@ActivityAskQuestion).SetDeviceRegUserId(login_res.id)
                excecuterService.execute {
                    userdao.deleteallregisteruser()
                    userdao.insert(user_reg_local)
                }

                CALL_FOR_FETCH_SERVER_DATA()


            }

            override fun OnError(Error: String?) {
                loader.dismiss()
                SimpleAlertDialog(this@ActivityAskQuestion, Error!!, object : SimpleOkListener {
                    override fun OnOk() {

                    }

                }).show()

            }

            override fun OnLogicError(Code: Int, response: String?, msg: String?) {
                loader.dismiss()
                SimpleAlertDialog(this@ActivityAskQuestion, msg!!, object : SimpleOkListener {
                    override fun OnOk() {

                    }

                }).show()
            }

        }).execute()
    }

    private fun ForGotQuestion(email: String) {
        val params = HashMap<String, Any>()
        params.put("email", email)

        FormPost(URLs.FORGOTQUESTION, params, null, object : Responselistener {
            override fun Start() {
                loader.show()
            }

            override fun OnSucess(Responseobject: String?) {
                loader.dismiss()
                SimpleAlertDialog(
                    this@ActivityAskQuestion,
                    JSONObject(Responseobject).optString("response_message")!!,
                    object : SimpleOkListener {
                        override fun OnOk() {

                        }

                    }).show()

            }

            override fun OnError(Error: String?) {
                loader.dismiss()
                SimpleAlertDialog(this@ActivityAskQuestion, Error!!, object : SimpleOkListener {
                    override fun OnOk() {

                    }

                }).show()

            }

            override fun OnLogicError(Code: Int, response: String?, msg: String?) {
                loader.dismiss()
                SimpleAlertDialog(this@ActivityAskQuestion, msg!!, object : SimpleOkListener {
                    override fun OnOk() {

                    }

                }).show()
            }

        }).execute()
    }

    override fun Select(auestionfor: Int, questionNo: Int, question: String) {
        when (auestionfor) {
            QUESTIONONE -> {
                TXT_QUESTION1.text = question
                TXT_QUESTION1.setPadding(170, 0, 0, 0)
                TXT_QUESTION1.gravity = Gravity.START or Gravity.CENTER_VERTICAL
                TXT_QUESTION2.text = ""
                EDX_ANS2.setText("")
                Qno1 = questionNo
            }
            QUESTIONTWO -> {
                TXT_QUESTION2.text = question
                TXT_QUESTION2.setPadding(170, 0, 0, 0)
                TXT_QUESTION2.gravity = Gravity.START or Gravity.CENTER_VERTICAL
                Qno2 = questionNo
            }
        }
    }


    private fun CALL_FOR_FETCH_SERVER_DATA() {
        if (!loader.isShowing)
            loader.show()

        CBCgetContactFromServer(this).get(object : OnServerFetchListener {
            override fun onServerComplete() {
                loader.dismiss()
                if (AppSharePref(this@ActivityAskQuestion).GetDeviceRegId() != null && AppSharePref(
                        this@ActivityAskQuestion
                    ).GetDeviceRegId().equals(login_res.id)
                ) {
                    AutoSyncToServer(this@ActivityAskQuestion).execute()
                    /*** BACKUP will execute when condition is satisfied*/
                }


                startActivity(
                    Intent(
                        this@ActivityAskQuestion,
                        MainActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                )
                finish()
            }

            override fun onFetchError(errormsg: String) {
                loader.dismiss()
                SimpleAlertDialog(this@ActivityAskQuestion, errormsg, object : SimpleOkListener {
                    override fun OnOk() {

                    }

                }).show()
            }

        })
    }
}

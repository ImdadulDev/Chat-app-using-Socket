package com.CBC.Dialogs

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.CBC.DialogListener.OnPasswordChangeListener
import com.app.RitoRideSharingAppPassenger.NetworkUtils.JSONMAPPost
import com.app.RitoRideSharingAppPassenger.NetworkUtils.URLs
import com.cbc.NetworkUtils.AppLoader
import com.cbc.NetworkUtils.Responselistener
import com.cbc.R
import com.cbc.utils.Vaidator
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_resetpassword_sheet_dialog.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [OTPbottomSheetDialog.OnOTPListener] interface
 * to handle interaction events.
 * Use the [OTPbottomSheetDialog.newInstance] factory method to
 * create an instance of this fragment.
 */
class CHANGEbottomSheetDialog : BottomSheetDialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var forgotpassword: Int? = null
    private var listener: OnPasswordChangeListener? = null
    lateinit var loader: AppLoader
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            forgotpassword = it.getInt(ARG_PARAM3)
        }
        loader = AppLoader(activity!!)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_resetpassword_sheet_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        BTN_VERIFY.setOnClickListener {
            if (EDX_Password.text.isNullOrEmpty()) {
                EDX_Password.setHintTextColor(Color.RED)
                EDX_Password.requestFocus()
                return@setOnClickListener
            }
            if (!Vaidator.isValidPassword(EDX_Password.text.toString())) {
                TXTERROR.text =
                    "Requires at least 1 Upper case character, At least 1 number and at least 1 special character and should be at least 8 characters long."
                EDX_Password.requestFocus()
                return@setOnClickListener
            }

            val header = HashMap<String, Any>()
            header.put("authtoken", param1!!)
            header.put("user_id", param2!!)

            val params = HashMap<String, Any>()
            params.put("userId", param2!!)
            params.put("currentpassword", "")
            params.put("password", EDX_Password.text.toString())
            params.put("forgotPassword", "$forgotpassword")





            JSONMAPPost(URLs.CHANGEPASSWORD, params, header, object : Responselistener {
                override fun Start() {
                    loader.show()
                }

                override fun OnSucess(Responseobject: String?) {
                    listener?.SuccessfullyPasswordChnaged(Responseobject)
                    loader.dismiss()
                    dismissAllowingStateLoss()
                }

                override fun OnError(Error: String?) {

                    TXTERROR.text = Error
                    loader.dismiss()
                }

                override fun OnLogicError(Code: Int, response: String?, msg: String?) {
                    loader.dismiss()
                    TXTERROR.text = msg

                }

            }).execute()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnPasswordChangeListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnOTPListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String, isforgot: Int) =
            CHANGEbottomSheetDialog().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                    putInt(ARG_PARAM3, isforgot)
                }
            }
    }
}

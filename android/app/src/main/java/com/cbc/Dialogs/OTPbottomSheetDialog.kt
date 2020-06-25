package com.CBC.Dialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.CBC.DialogListener.OnOTPListener
import com.app.RitoRideSharingAppPassenger.NetworkUtils.JSONMAPPost
import com.app.RitoRideSharingAppPassenger.NetworkUtils.URLs
import com.cbc.NetworkUtils.AppLoader
import com.cbc.NetworkUtils.Responselistener
import com.cbc.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_otpbottom_sheet_dialog.*
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [OTPbottomSheetDialog.OnOTPListener] interface
 * to handle interaction events.
 * Use the [OTPbottomSheetDialog.newInstance] factory method to
 * create an instance of this fragment.
 */
class OTPbottomSheetDialog : BottomSheetDialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnOTPListener? = null
    lateinit var loader: AppLoader
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        loader = AppLoader(activity!!)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_otpbottom_sheet_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val JSON = JSONObject(param1).optJSONObject("response_data")
        EDX_OTP.setText(JSON.optString("otp"))

        BTN_VERIFY.setOnClickListener {
            val param = HashMap<String, Any>()
            param.put("otp", EDX_OTP.text.toString())
            param.put("phone", param2!!)
            param.put("countryCode", "+91")
            JSONMAPPost(URLs.OTPVERIFY, param, null, object : Responselistener {
                override fun Start() {
                    loader.show()
                }

                override fun OnSucess(Responseobject: String?) {
                    listener?.OnOTPVerified(Responseobject)
                    loader.dismiss()
                    dismissAllowingStateLoss()
                }

                override fun OnError(Error: String?) {
                    listener?.OnOTPFailed(Error)
                    TXTERROR.text = Error
                    loader.dismiss()
                }

                override fun OnLogicError(Code: Int, response: String?, msg: String?) {
                    loader.dismiss()
                    TXTERROR.text = msg
                    listener?.OnOTPFailed(msg)
                }

            }).execute()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnOTPListener) {
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
        fun newInstance(param1: String, param2: String) =
            OTPbottomSheetDialog().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

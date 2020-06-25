package com.cbc.views.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cbc.R
import com.cbc.utils.AuthExpire
import com.cbc.views.loader.GlideImageLoader
import com.cbc.models.LoginResponse
import com.cbc.networkutils.*
import com.cbc.utils.AppSharePref
import com.cbc.views.MainActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.profile_view_fragment.*
import lv.chi.photopicker.ChiliPhotoPicker
import lv.chi.photopicker.PhotoPickerFragment
import org.json.JSONObject
import java.io.File

class ProfileViewFragment : Fragment(), PhotoPickerFragment.Callback {

    companion object {
        fun newInstance() = ProfileViewFragment()
    }

    private lateinit var viewModel: ProfileViewViewModel
    private lateinit var loader: AppLoader
    private var file: File? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loader = AppLoader(activity!!)
        loader.show()
        viewModel = ViewModelProviders.of(this).get(ProfileViewViewModel::class.java)

        return inflater.inflate(R.layout.profile_view_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ChiliPhotoPicker.init(
            loader = GlideImageLoader(),
            authority = "lv.chi.sample.fileprovider"
        )

        IMG_BACK.setOnClickListener {
            goToMainActivity()
        }

        IMG_IMAGE.setOnClickListener {
            PhotoPickerFragment.newInstance(
                    multiple = false,
            allowCamera = true,
            maxSelection = 1,
            theme = R.style.ChiliPhotoPicker_Dark
            ).show(childFragmentManager, "picker")
        }

        BTN_Update.setOnClickListener {
            update_profile()
        }

        viewModel.profiledata.observe(this, Observer {
            loader.dismiss()
            if (it != null) {
                Glide.with(activity!!).load(it.responseData.profileImage).apply(
                    RequestOptions.circleCropTransform().circleCrop()
                        .placeholder(R.drawable.ic_user_placeholder)
                ).into(IMG_IMAGE)
                EDX_MOB.setText("${it.responseData.phone}")
                EDX_EMAIL.setText("${it.responseData.email}")
                EDX_FNAME.setText("${it.responseData.firstName}")
                EDX_LNAME.setText("${it.responseData.lastName}")
                EDX_USERNAME.setText("${it.responseData.userName}")
            }

        })
    }

    private fun goToMainActivity() {
        context?.startActivity(Intent(activity, MainActivity::class.java))
    }


    private fun update_profile() {
        if (EDX_FNAME.text.isNullOrEmpty()) {
            EDX_FNAME.requestFocus()
            EDX_FNAME.error = "First name please"
            return
        }

        if (EDX_LNAME.text.isNullOrEmpty()) {
            EDX_LNAME.requestFocus()
            EDX_LNAME.error = "Last name please"
            return
        }

        if (EDX_USERNAME.text.isNullOrEmpty()) {
            EDX_USERNAME.requestFocus()
            EDX_USERNAME.error = "Provide username please"
            return
        }

        val header = HashMap<String, Any>()
        header.put("authtoken", AppSharePref(activity!!).GetUSERDATA()!!.authToken)
        header.put("content-type", "multipart/form-data")
        val param = HashMap<String, Any>()
        param.put("firstName", EDX_FNAME.text.toString())
        param.put("lastName", EDX_LNAME.text.toString())
        param.put("userName", EDX_USERNAME.text.toString())
//        param.put("_id",AppSharePref(activity!!).GetUSERDATA()!!.id)
        if (file != null)
            param.put("image", file!!)
        Klog.d("## PARAMS-", Gson().toJson(param))

        FormPost(URLs.UPDATEPROFILE, param, header, object : Responselistener {
            override fun OnSucess(Responseobject: String?) {
                Toast.makeText(
                    activity!!,
                    JSONObject(Responseobject).optString("response_message"),
                    Toast.LENGTH_LONG
                ).show()
                val resp = JSONObject(Responseobject).optJSONObject("response_data")
                val userdata =
                    Gson().fromJson<LoginResponse>(resp.toString(), LoginResponse::class.java)
                AppSharePref(activity!!).SetUSERDATA(userdata)
                loader.dismiss()
            }

            override fun OnError(Error: String?) {
                Toast.makeText(activity!!, Error, Toast.LENGTH_LONG).show()
                loader.dismiss()
            }

            override fun Start() {
                loader.show()
            }

            override fun OnLogicError(Code: Int, response: String?, msg: String?) {
                loader.dismiss()
                Toast.makeText(activity!!, msg, Toast.LENGTH_LONG).show()
                if (Code == 4000) {
                    AuthExpire(activity!!).logout()
                }
            }

        }).execute()


    }

    override fun onImagesPicked(photos: ArrayList<Uri>) {
        Klog.d("### PATH-", "${photos[0]}")
        file = File(photos[0].path)
        Glide.with(activity!!).load("${photos[0]}")
            .apply(RequestOptions.circleCropTransform().circleCrop()).into(IMG_IMAGE)
    }

}

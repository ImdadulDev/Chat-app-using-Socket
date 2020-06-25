package com.cbc.views.ui.logout

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.cbc.R
import com.cbc.utils.AppSharePref
import com.cbc.views.SplashActivity
import kotlinx.android.synthetic.main.fragment_gallery.*

class LogoutFragment : Fragment() {

    private lateinit var galleryViewModel: LogoutViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        galleryViewModel =
            ViewModelProviders.of(this).get(LogoutViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_gallery, container, false)
        val textView: TextView = root.findViewById(R.id.text_gallery)
        galleryViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        text_gallery.setOnClickListener {
            AppSharePref(activity!!).LogOUT()
            galleryViewModel.logout()
            //Registering contact observer

            startActivity(Intent(activity!!, SplashActivity::class.java))
            activity!!.finish()
        }
    }
}
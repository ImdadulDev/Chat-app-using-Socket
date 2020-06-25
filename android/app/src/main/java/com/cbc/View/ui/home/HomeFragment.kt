package com.cbc.View.ui.home

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cbc.Localdb.CBCDatabase
import com.cbc.Localdb.ContactEntity
import com.cbc.NetworkUtils.Klog
import com.cbc.R
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
//        val textView: RecyclerView = root.findViewById(R.id.RECV)
//        homeViewModel.text.observe(this, Observer {
//            textView.text = it
//        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        RECV.layoutManager = LinearLayoutManager(activity!!)

        val cbcDatabase = CBCDatabase.getDatabase(activity!!)
        object : AsyncTask<Void, Void, List<ContactEntity>>() {
            override fun doInBackground(vararg p0: Void?): List<ContactEntity>? {
                return cbcDatabase.contctDao().getAll()
            }

            override fun onPostExecute(result: List<ContactEntity>?) {
                super.onPostExecute(result)
                RECV.adapter = AdapterContactlist(activity!!, result!!)

            }
        }.execute()
    }
}
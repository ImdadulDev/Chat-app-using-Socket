package com.cbc.views.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.cbc.contactsyncutils.savedataInToDevice
import com.cbc.interfaces.onSaveListener
import com.cbc.localdb.ContactEntity
import com.cbc.localdb.SaveDeviceData
import com.cbc.R
import com.cbc.utils.AuthExpire
import com.cbc.models.contactResponse.GetContactResponse
import com.cbc.networkutils.*
import com.cbc.views.ui.activities.CreateGroupActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONArray
import org.json.JSONObject

class HomeFragment : Fragment(), OnFetchDeviceContactListener {

    private lateinit var homeViewModel: HomeViewModel
    lateinit var loader: AppLoader
    var contactList = ArrayList<ContactEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loader = AppLoader(activity!!)

        RECV.layoutManager = LinearLayoutManager(activity!!)
        adapterlist = AdapterContactlist(activity!!, contactList, this)
        RECV.adapter = adapterlist
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            setListData(it)
        })


        BTN_IMPORTDEVICE.setOnClickListener {
            SaveDeviceData(activity!!, this).execute() //import device data
        }

        /*EDX_phoneno.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                adapterlist!!.filter.filter(p0.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })*/



    }

    companion object {
        private var adapterlist: AdapterContactlist? = null
        fun getChangedTextFromTabHomeFragment(text: String) {
            Log.d("----","home frag text: $text")
            adapterlist?.filter?.filter(text)
        }
    }



    private fun setListData(it: List<ContactEntity>?) {
        if (it!!.isEmpty()) {
            LL_NO_CONTACT_FOUND.visibility = View.VISIBLE
            if (!homeViewModel.isTrustedDevice()) {
                BTN_IMPORTDEVICE.visibility = View.GONE
            }

        } else {
            LL_NO_CONTACT_FOUND.visibility = View.GONE
            Klog.d("## SET", "-> ${it.size}")
            contactList.clear()
            contactList.addAll(it)

            //for(element in contactList){ //Log.d("----", "contact list: $element") }

            if (adapterlist == null) {
                adapterlist = AdapterContactlist(activity!!, contactList, this)
                RECV.adapter = adapterlist
            } else
                adapterlist!!.notifyDataSetChanged()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
//            R.id.action_BackUp -> {
//                CBClocalContacts(activity!!, this).execute()
//            }
            R.id.action_Import -> {
                loader.show()
                savedataInToDevice(activity!!, object : onSaveListener {
                    override fun SaveComplete() {
                        loader.dismiss()
                        Toast.makeText(activity!!, "Added Successfully", Toast.LENGTH_LONG).show()
                    }

                    override fun SaveError() {
                        loader.dismiss()
                        Toast.makeText(
                            activity!!,
                            "Something went wrong please try again later.",
                            Toast.LENGTH_LONG
                        ).show()

                    }

                }).execute() //import device data
            }

            R.id.action_create_group -> {
                val intentCreateGroup = Intent(activity, CreateGroupActivity::class.java)
                intentCreateGroup.putExtra("activityName", "Frag")
                intentCreateGroup.putExtra("receiveMsgUsername", "")
                intentCreateGroup.putExtra("receiveMsgRemoteId", "")
                intentCreateGroup.putExtra("receiveMsgCbcId", "")
                intentCreateGroup.putExtra("receiveMsgCbcImage", "")
                intentCreateGroup.putExtra("remoteUserMobNo", "")
                startActivity(intentCreateGroup)
                //finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onFetchComplete() {
        Klog.d("## ", "OnComplete")
        homeViewModel.text.observe(this, Observer {
            Klog.d("## ", "OnComplete Size-${it.size}")
            setListData(it)
        })
    }


    override fun onFetchLocalContacts(list: List<ContactEntity>, iSingle: Boolean) {
        if (list.isEmpty()) {
            Toast.makeText(activity!!, "No contact available for upload", Toast.LENGTH_LONG).show()
            return
        }


        val clist = JSONArray(Gson().toJson(list))
        Log.d("----", " upload cList params: $clist")
        val params = JSONObject()
        params.put("contact", clist)
        JSONPost(URLs.CONTACTUPLOAD, params, homeViewModel.header, object : Responselistener {
            override fun OnSucess(Responseobject: String?) {
                Klog.d("RSEP--->", Responseobject!!)
                loader.dismiss()
                if (iSingle)
                    homeViewModel.update(list[0])
                else {
                    val respdata = Gson().fromJson<GetContactResponse>(
                        Responseobject,
                        GetContactResponse::class.java
                    )
                    homeViewModel.updateAll(respdata.responseData)
                }
                onFetchComplete()
            }

            override fun OnError(Error: String?) {
                Toast.makeText(activity, Error, Toast.LENGTH_SHORT).show()
                loader.dismiss()
            }

            override fun Start() {
                loader.show()
            }

            override fun OnLogicError(Code: Int, response: String?, msg: String?) {
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
                loader.dismiss()
                if (Code == 4000) {
                    AuthExpire(activity!!).logout()
                }
            }

        }).execute()
    }
}
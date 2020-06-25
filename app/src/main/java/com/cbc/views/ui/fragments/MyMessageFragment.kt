package com.cbc.views.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cbc.R
import com.cbc.contactsyncutils.savedataInToDevice
import com.cbc.data.apiresponse.MyMessageResponse
import com.cbc.data.repositories.ChatRepository
import com.cbc.data.repositories.GroupChatRepository
import com.cbc.interfaces.RecyclerTouchListener
import com.cbc.interfaces.onSaveListener
import com.cbc.localdb.entities.GroupMemberEntity
import com.cbc.models.LoginResponse
import com.cbc.networkutils.AppLoader
import com.cbc.utils.*
import com.cbc.views.ChatActivity
import com.cbc.views.adapters.MessageListAdapter
import com.cbc.views.ui.activities.CreateGroupActivity
import com.cbc.views.ui.activities.GroupChatActivity
import kotlinx.android.synthetic.main.fragment_my_message.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import kotlin.random.Random

class MyMessageFragment : Fragment(), KodeinAware {

    override val kodein by kodein()
    private val chatRepository: ChatRepository by instance()
    private val groupChatRepository: GroupChatRepository by instance()
    lateinit var loader: AppLoader
    private var myMessageList: ArrayList<MyMessageResponse.Data.ResponseData> = ArrayList()
    private lateinit var messageRv: RecyclerView
    private lateinit var pref: AppSharePref
    lateinit var prefUserDetails: LoginResponse


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_message, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pref = AppSharePref(context!!)
        prefUserDetails = pref.GetUSERDATA()!!
        Log.d("----", "Pref getUser : ${pref.getUserChatToken()}")

        initData(view)
    }

    override fun onResume() {
        super.onResume()
        //Log.d("----",": my msg resume")
        myMessageList = ArrayList()
        getDataFromApi()
        populateRvContent()
    }

    private fun initData(view: View) {
        loader = AppLoader(activity!!)
        messageRv = view.findViewById(R.id.rv_my_message_list)

    }

    private fun getDataFromApi() = Coroutines.main {
        my_message_progressbar.show()
        try {
            val myMessageResponse = chatRepository.getMyMessageListResponse(pref.getUserChatToken()!!, prefUserDetails.id)
            Log.d("----", "chat list res: $myMessageResponse")
            for (element in myMessageResponse.data.responseData){
                myMessageList.add(element)
                groupChatRepository.deleteAllGroupMemberFromTable()
                for(groupDetailsElement in element.groupDetail ){
                    groupChatRepository.saveGroupMemberEntity(
                        GroupMemberEntity(
                            Random.nextInt(0, 1000000000),
                            groupDetailsElement.chatUserId,
                            element.groupId,
                            element.groupName,
                            groupDetailsElement.userFullName,
                            groupDetailsElement.userimage,
                            groupDetailsElement.phone
                        ))
                }
            }
            my_message_progressbar.hide()
        } catch (e: Exception) {
            e.printStackTrace()
            my_message_progressbar.hide()
        }
    }

    private fun populateRvContent() {
        // Creates a vertical Layout Manager
        messageRv.layoutManager = LinearLayoutManager(activity)

        // Access the RecyclerView Adapter and load the data into it
        myMessageAdapter = MessageListAdapter(myMessageList, activity!!.applicationContext, prefUserDetails.id)
        messageRv.adapter = myMessageAdapter

        // row click listener
        messageRv.addOnItemTouchListener(
            RecyclerTouchListener(
                activity,
                messageRv,
                object : RecyclerTouchListener.ClickListener {
                    override fun onClick(view: View, position: Int) {
                        if (isConnectedToNetwork(context!!)) {
                            val model = myMessageList[position]
                            if(myMessageList[position].groupId.isEmpty()){
                                val intent = Intent(context, ChatActivity::class.java)

                                var receiveMsgCbcId = ""
                                var receiveMsgUsername = ""
                                var receiveMsgRemoteId = ""
                                var receiveMsgCbcImage = ""
                                var remoteUserMobNo = ""

                                if(prefUserDetails.id == model.remoteUserId){
                                    receiveMsgCbcId = model.userId
                                    receiveMsgUsername = model.username
                                    receiveMsgRemoteId = model.userId
                                    receiveMsgCbcImage = model.userimage
                                    remoteUserMobNo = model.phone

                                } else {
                                    receiveMsgCbcId = model.remoteUserId
                                    receiveMsgUsername = model.remoteusername
                                    receiveMsgRemoteId = model.remoteUserId
                                    receiveMsgCbcImage = model.remoteuserimage
                                    remoteUserMobNo = model.phone
                                }

                                intent.putExtra("receiveMsgUsername", receiveMsgUsername)
                                intent.putExtra("receiveMsgRemoteId", receiveMsgRemoteId)
                                intent.putExtra("receiveMsgCbcId", receiveMsgCbcId)
                                intent.putExtra("receiveMsgCbcImage", receiveMsgCbcImage)
                                intent.putExtra("remoteUserMobNo", remoteUserMobNo)

                                Log.d("----","receiveMsgUsername: $receiveMsgUsername")
                                Log.d("----","receiveMsgRemoteId: $receiveMsgRemoteId")
                                Log.d("----","receiveMsgCbcId: $receiveMsgCbcId")
                                Log.d("----","receiveMsgCbcImage: $receiveMsgCbcImage")
                                Log.d("----","remoteUserMobNo: $remoteUserMobNo")

                                context!!.startActivity(intent)

                            } else {
                                val intent = Intent(context, GroupChatActivity::class.java)
                                intent.putExtra("group_name", myMessageList[position].groupName)
                                intent.putExtra("group_id", myMessageList[position].groupId)
                                intent.putExtra("group_image", myMessageList[position].groupImage)
                                //intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                                context!!.startActivity(intent)
                            }


                        } else Toast.makeText(
                            context,
                            context!!.resources.getString(R.string.no_network),
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                    override fun onLongClick(view: View, position: Int) {}
                })
        )
    }

    companion object {
        private var myMessageAdapter: MessageListAdapter? = null

        fun getChangedTextFromTabHomeFragment(text: String) {
            Log.d("----", "my message: $text")
            try {
                myMessageAdapter?.filter?.filter(text)
            } catch (e: Exception) {
                e.printStackTrace()
            }
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
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
package com.cbc.views.ui.activities

import android.content.Intent
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cbc.R
import com.cbc.data.repositories.ChatRepository
import com.cbc.data.apiresponse.FileUploadResponse
import com.cbc.data.FileUploadService
import com.cbc.data.Servicegenerator
import com.cbc.models.LoginResponse
import com.cbc.models.MessageModel
import com.cbc.utils.*
import com.cbc.views.adapters.GridMenuAdapter
import com.cbc.views.loader.GlideImageLoader
import com.cbc.widget.MenuEditText
import com.cbc.widget.SoftKeyBoardPopup
import com.squareup.picasso.Picasso
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import lv.chi.photopicker.ChiliPhotoPicker
import lv.chi.photopicker.PhotoPickerFragment
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.net.URISyntaxException
import kotlin.random.Random
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cbc.data.repositories.GroupChatRepository
import com.cbc.localdb.entities.GroupChatEntity
import com.cbc.utils.helpers.FileUtils
import com.cbc.utils.toast
import com.cbc.views.MainActivity
import kotlinx.android.synthetic.main.activity_group_chat.*
import kotlinx.android.synthetic.main.activity_group_chat.progressBarUpload
import okhttp3.RequestBody.Companion.asRequestBody

class GroupChatActivity : AppCompatActivity(), KodeinAware, MenuEditText.PopupListener,
    GridMenuAdapter.GridMenuListener, PhotoPickerFragment.Callback {

    override val kodein by kodein()
    private val chatRepository: ChatRepository by instance()
    private val groupChatRepository: GroupChatRepository by instance()

    lateinit var etChatBox: MenuEditText
    lateinit var btnSend: ImageView
    lateinit var recyclerMessages: RecyclerView
    lateinit var progressLoading: ProgressBar
    lateinit var prefUserDetails: LoginResponse
    private lateinit var pref: AppSharePref
    private var groupName = ""
    private var groupId = ""
    private var groupImage = ""
    private var receiveMsgRemoteId = ""
    private var receiveMsgCbcId = ""
    private var receiveMsgCbcImage = ""

    var messagesList = ArrayList<MessageModel>()
    var messagesAdapter: ChatMessagesAdapter? = null

    private lateinit var mSocket: Socket
    private val mUserName: String = ""

    //attachment layout
    //Our variables
    lateinit var parentView: CardView
    lateinit var menuKeyboard: SoftKeyBoardPopup
    lateinit var rootView: ConstraintLayout
    //lateinit var menuEditText: MenuEditText

    var positionList = ArrayList<Int>()
    var chatTextPosition = 0
    var remoteuserDetailsHashMap : HashMap<String, String> = HashMap<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chat)

        getDataFromPreviousActivity()

        //getting login user data from sharedPref
        try {
            pref = AppSharePref(applicationContext)
            prefUserDetails = pref.GetUSERDATA()!!
            Log.d("----", "Pref getUser : ${pref.getUserChatToken()}")

            connectToSocketServer()

        } catch (e: Exception) {
            e.printStackTrace()
        }

        initViews()
        // set up previous chat list
        setupViews()

    }

    private fun getDataFromPreviousActivity() {
        val intent = intent
        if (intent != null) {
            groupName = intent.getStringExtra("group_name")!!
            groupId = intent.getStringExtra("group_id")!!
            groupImage = intent.getStringExtra("group_image")!!


            Log.d("----","11: " + groupName)
            Log.d("----","22: " + groupId)
            Log.d("----","33: " + groupImage)

            if (groupImage.isNotEmpty()) {
                Glide.with(applicationContext).load(Constants.CHAT_ATTACHMENT_BASE_URL + "image/" + groupImage)
                    .apply(RequestOptions.circleCropTransform().circleCrop()).into(group_chat_group_img)
            } else {
                group_chat_group_img.setBackgroundResource(R.drawable.ic_group_img)
            }
        }
    }

    private fun connectToSocketServer() {
        //connecting to socket io
        val mOptions = IO.Options()
        mOptions.query =
            "userid=" + prefUserDetails.id + "&chatToken=" + pref.getUserChatToken()
        mSocket = IO.socket(Constants.CHAT_SOCKET_SERVER_URL, mOptions)

        mSocket.on(Socket.EVENT_CONNECT, onConnect)
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect)
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError)
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError)
        mSocket.on("userverified", onUserVerified)
        mSocket.on("userapearonline", onUserApearOnline)
        mSocket.on("newuserconnect", onNewUserConnect)
        mSocket.on("chatReciveRemoteuser", onChatReciveRemoteUser)
        mSocket.on("pushNewMessage", onPushNewMessage)
        mSocket.on("groupChatBroadCast", onGroupChatBroadCast)
        mSocket.on("SendChatCommit", onSendChatCommit)
        mSocket.connect()

        connectToGroup()
    }

    private fun connectToGroup() {
        //sending group data when user connect to socket
        val sendGroupConnectParamsObj = JSONObject()
        sendGroupConnectParamsObj.put("userId", prefUserDetails.id)
        sendGroupConnectParamsObj.put("groupId", groupId)

        Log.d("----", "groupConnect Params: $sendGroupConnectParamsObj")

        mSocket.emit("groupConnect", sendGroupConnectParamsObj)
    }

    private fun initViews() {
        // Get Views
        etChatBox = findViewById(R.id.group_chat_et_group_chat_box)
        btnSend = findViewById(R.id.group_chat_img_send)
        recyclerMessages = findViewById(R.id.recyclerMessages)
        progressLoading = findViewById(R.id.progressLoading)

        group_chat_text_name.setOnClickListener {
            openEditGroupActivity()
        }

        // Send Button
        btnSend.setOnClickListener {
            if (etChatBox.text.toString().isNotEmpty()) {
                updateSendMessage(etChatBox.text.toString(), "text", "", "", "")
            }
        }

        group_chat_img_back_arrow.setOnClickListener {
            finish()
            //super.onBackPressed()
        }

        group_chat_text_name.text = groupName

        group_chat_text_name.setOnClickListener {

        }

        etChatBox.onChange {
            if (it.isNotEmpty()) {
                group_chat_img_rec_audio.visibility = View.GONE
                btnSend.visibility = View.VISIBLE
            } else {
                btnSend.visibility = View.GONE
                group_chat_img_rec_audio.visibility = View.VISIBLE
            }
        }

        group_chat_img_calling.setOnClickListener {
        /*    intent = Intent(this, AudioCallActivity::class.java)
            intent.putExtra("activityName", "GroupChatActivity")
            intent.putExtra("groupId", groupId)
            intent.putExtra("groupName", groupName)
            intent.putExtra("groupImage", groupImage)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
            */
        }

        group_chat_img_video_calling.setOnClickListener {
    /*      intent = Intent(this, VideoCallActivity::class.java)
            intent.putExtra("activityName", "GroupChatActivity")
            intent.putExtra("groupId", groupId)
            intent.putExtra("groupName", groupName)
            intent.putExtra("groupImage", groupImage)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
            */
        }

        group_chat_img_add_person.setOnClickListener {
            openEditGroupActivity()
        }

        group_chat_img_rec_audio.setOnClickListener {
            val intentRecord = Intent(this, AudioRecorderActivity::class.java)
            startActivityForResult(intentRecord, 1002)
        }

        rootView = findViewById(R.id.group_chat_activity)
        parentView = findViewById(R.id.cardMessageBar)
        val group_chatBoxLl = findViewById<ConstraintLayout>(R.id.chat_box_constraint)

        etChatBox.popupListener = this

        menuKeyboard = SoftKeyBoardPopup(
            this,
            rootView,
            etChatBox,
            etChatBox,
            menugroup_chatContainer,
            this
        )

        group_chat_img_attachment.setOnClickListener {
            toggle()
        }

        //For search views

        group_chat_img_menu_search.setOnClickListener {
            ll_group_chat_search.visibility = View.VISIBLE
        }

        val searchIcon = group_chat_search.findViewById<ImageView>(R.id.search_mag_icon)
        searchIcon.setColorFilter(ContextCompat.getColor(applicationContext, R.color.colorPrimary))


        val cancelIcon = group_chat_search.findViewById<ImageView>(R.id.search_close_btn)
        cancelIcon.setColorFilter(ContextCompat.getColor(applicationContext, R.color.colorPrimary))

        cancelIcon.setOnClickListener {
            try {
                ll_group_chat_search.visibility = View.GONE


                messagesList.forEach {
                    it.searchedKeyWord = false
                }

                messagesAdapter!!.notifyDataSetChanged()
                recyclerMessages.scrollToPosition(
                    messagesList.size - 1
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        val textView = group_chat_search.findViewById<TextView>(R.id.search_src_text)
        textView.setTextColor(ContextCompat.getColor(applicationContext, R.color.black_700))
        // If you want to change the color of the cursor, change the 'colorAccent' in colors.xml


        group_chat_search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                //messagesAdapter?.filter?.filter(newText)
                try {
                    val string = newText.toString()
                    chatTextPosition = 0
                    if (newText.toString().isEmpty()) {
                        messagesList.forEach {
                            it.searchedKeyWord = false
                        }
                        messagesAdapter!!.notifyDataSetChanged()
                        recyclerMessages.scrollToPosition(
                            messagesList.size - 1
                        )
                    } else {
                        positionList = ArrayList()
                        messagesList.forEach {
                            if (it.message.contains(string, true)) {
                                positionList.add(messagesList.indexOf(it))
                                Log.d("----", ": ${messagesList.indexOf(it)}")
                                it.searchedKeyWord = true
                            } else {
                                it.searchedKeyWord = false
                            }
                        }

                        //chatList = p1!!.values as ArrayList<MessageModel>
                        chatTextPosition = positionList.size - 1
                        Log.d("----", "group_chatTextPosition: $chatTextPosition")
                        recyclerMessages.scrollToPosition(positionList[chatTextPosition])
                        messagesAdapter!!.notifyDataSetChanged()

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                return false
            }

        })

        group_chat_search_up.setOnClickListener {
            try {
                Log.d("----", "positionList up: $chatTextPosition")
                if (chatTextPosition >= 0 && chatTextPosition < positionList.size) {
                    chatTextPosition -= 1
                    recyclerMessages.scrollToPosition(
                        positionList[chatTextPosition]
                    )
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        group_chat_search_down.setOnClickListener {
            try {
                Log.d("----", "positionList down: $chatTextPosition")
                if (chatTextPosition >= 0 && chatTextPosition < positionList.size) {
                    chatTextPosition += 1
                    recyclerMessages.scrollToPosition(
                        positionList[chatTextPosition]
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun openEditGroupActivity() {
        if (groupName.isNotEmpty()) {
            val intentEditGroup = Intent(this, EditGroupActivity::class.java)
            intentEditGroup.putExtra("group_name", groupName)
            intentEditGroup.putExtra("group_id", groupId)
            intentEditGroup.putExtra("group_image", groupImage)
            intentEditGroup.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            intentEditGroup.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intentEditGroup)

        }
    }

    override fun onBackPressed() {
        finish()
    }


    override fun onDestroy() {
        super.onDestroy()
        mSocket.disconnect()

        mSocket.off(Socket.EVENT_CONNECT, onConnect)
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect)
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError)
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError)
        mSocket.off("userverified", onUserVerified)
        mSocket.off("userapearonline", onUserApearOnline)
        mSocket.off("newuserconnect", onNewUserConnect)
        mSocket.off("chatReciveRemoteuser", onChatReciveRemoteUser)
        mSocket.off("pushNewMessage", onPushNewMessage)
        mSocket.off("groupChatBroadCast", onGroupChatBroadCast)
        mSocket.off("SendChatCommit", onSendChatCommit)

        menuKeyboard.clear()
    }

    private val onConnect = Emitter.Listener {
        runOnUiThread {
            //toast("Group socket connect")
        }
    }

    private val onDisconnect = Emitter.Listener {
        runOnUiThread {
            //toast("Disconnected")
            //connectToSocketServer()
        }
    }

    private val onConnectError = Emitter.Listener {
        runOnUiThread(Runnable {
            Log.e("Socket", "Error connecting")
            //Toast.makeText(applicationContext, R.string.error_connect, Toast.LENGTH_LONG).show()
        })
    }

    private val onUserVerified = Emitter.Listener {
        Log.d("----", "onUserVerified: " + it[0].toString())
    }


    private val onUserApearOnline = Emitter.Listener {
        runOnUiThread {
            Log.d("----", "onUserApearOnline : " + it[0].toString())

        }
    }

    private val onNewUserConnect = Emitter.Listener {
        /*    runOnUiThread {
                try {
                    Log.d("----", "onNewUserConnect: " + it[0].toString())
                    //{"remoteuserid":"5e8ab0d760f46f3b5ccbbcfd"}
                    val json = JSONObject(it[0].toString())
                    val remoteuserid = json.optString("remoteuserid")
                    Log.d("----", "onNewUserConnect remoteuserid: $remoteuserid")

                    if (receiveMsgRemoteId == remoteuserid) { //TODO static
                        group_chat_img_user_online_status.visibility = View.VISIBLE
                    } else group_chat_img_user_online_status.visibility = View.GONE
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }*/
    }

    private val onPushNewMessage = Emitter.Listener {
        runOnUiThread {
            Log.d("----", "onPushNewMessage: " + it[0].toString())
        }
    }

    private val onGroupChatBroadCast = Emitter.Listener {
        runOnUiThread {
            Log.d("----", "onGroupChatBroadCast: " + it[0].toString())

            val json = JSONObject(it[0].toString())
            //val chatType = json.optString("chatType")
            val groupid = json.optString("groupId")
            //val remoteuserid = json.optString("remoteuserid")
            //val time = json.optString("")
            val userid = json.optString("userid")
           // val remoteuserimage = json.optString("remoteuserimage")
            //val jsonWebToken = json.optString("jsonWebToken")
            val chat = json.optString("chat")
           // val timeZone = json.optString("timeZone")
           // val attachment = json.optString("attachment")
            val attachmentName = json.optString("attachmentName")
            //val size = json.optString("size")
            val type = json.optString("type")
            val ISOTime = json.optString("ISOTime")
            val originalName = json.optString("originalName")

            if (groupid == groupId && userid != prefUserDetails.id) {

                try {// update Receive Message
                    val messageModel = MessageModel(
                        chat,
                        false,
                        getTimeFromDateUsingSubStringAndSplit(ISOTime),
                        type,
                        attachmentName,
                        originalName,
                        false,
                        remoteuserDetailsHashMap[userid]!!

                    )
                    messagesList.add(messageModel)
                    messagesAdapter?.notifyItemInserted(messagesList.size - 1)
                    recyclerMessages.scrollToPosition(messagesList.size - 1)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }

    private val onSendChatCommit  = Emitter.Listener {
        try {
            runOnUiThread {
                Log.d("----", "onSendChatCommit: " + it[0].toString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


   /* private suspend fun getRemoteUsernameFromDB(userid: String): String{
        return groupChatRepository.getRemoteUserNameFromDb(userid)
    }*/

    private val onChatReciveRemoteUser = Emitter.Listener {
        try {
            runOnUiThread {
                Log.d("----", "onChatReciveRemoteUser: " + it[0].toString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun socketEmitSendChat(
        chatMsg: String,
        chatType: String,
        attachmentName: String,
        size: String?
    ) {
        runOnUiThread {
            val sendChatParamsObj = JSONObject()
            sendChatParamsObj.put("chatType", "group")
            sendChatParamsObj.put("groupId", groupId)
            sendChatParamsObj.put("remoteuserid", "")
            sendChatParamsObj.put("time", getFormattedDateAndTimeTime())
            sendChatParamsObj.put("userid", prefUserDetails.id)
            sendChatParamsObj.put("remoteuserimage", "")
            sendChatParamsObj.put("jsonWebToken", pref.getUserChatToken())
            sendChatParamsObj.put("chat", chatMsg)
            sendChatParamsObj.put("timeZone", getTimeZone())
            sendChatParamsObj.put("attachment", "")
            sendChatParamsObj.put("attachmentName", attachmentName)
            sendChatParamsObj.put("size", size)
            sendChatParamsObj.put("type", chatType)


            Log.d("----", "sendChat Params: $sendChatParamsObj")

            mSocket.emit("sendchat", sendChatParamsObj)
        }

    }

    /* override fun onCreateOptionsMenu(menu: Menu): Boolean {
         // Inflate the menu; this adds items to the action bar if it is present.
         menuInflater.inflate(R.menu.menu_chat, menu)
         return true
     }

     override fun onOptionsItemSelected(item: MenuItem?): Boolean {
         when (item?.itemId) {
             R.id.menu_call ->{
                 Toast.makeText(applicationContext, "Call", Toast.LENGTH_SHORT).show()
             }

             R.id.menu_video ->{
                 Toast.makeText(applicationContext, "Video", Toast.LENGTH_SHORT).show()
             }
         }
         return super.onOptionsItemSelected(item)
     }
     */


    private fun setupViews() = Coroutines.main {
        // Show Progress Bar
        progressLoading.visibility = View.VISIBLE
        recyclerMessages.visibility = View.GONE

        try {
                val chatList = groupChatRepository.getRemoteUsersChatsFromDb(prefUserDetails.id, groupId)

                val indexToGetMsgNotSaved = chatList.size.toString()
                Log.d("group_chat", "-----offline list size:: " + chatList.size)

                loadData(chatList)
            if (isConnectedToNetwork(applicationContext)) {
             /*   Log.d(
                    "----", ":group_chat res: " + groupChatRepository.getGroupChatHistoryResponseFromApi(
                        groupId,
                        prefUserDetails.id,
                        pref.getUserChatToken().toString(),
                        indexToGetMsgNotSaved
                    )
                )*/

                val chatRes = groupChatRepository.getGroupChatHistoryResponseFromApi(
                    groupId,
                    prefUserDetails.id,
                    pref.getUserChatToken().toString(),
                    indexToGetMsgNotSaved
                )

                if (chatRes.success) {
                    //save data in db
                    groupChatRepository.saveGroupChatHistoryEntity(chatRes.chats)
                    Log.d("group_chat", "----online list size: " + chatRes.chats.size)

                    loadData(chatRes.chats)
                }

            } else {
                Toast.makeText(
                    applicationContext,
                    resources.getString(R.string.no_network),
                    Toast.LENGTH_SHORT
                ).show()
            }

            // Recycler View
            messagesAdapter = ChatMessagesAdapter()
            recyclerMessages.layoutManager =
                LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            recyclerMessages.adapter = messagesAdapter
            recyclerMessages.scrollToPosition(messagesList.size - 1)

        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Hide Progress bar
        progressLoading.visibility = View.GONE
        recyclerMessages.visibility = View.VISIBLE
    }

    private fun loadData(listOfChat: List<GroupChatEntity>) {
        for (element in listOfChat) {
            // send message
            if (element.userId == prefUserDetails.id) {
                messagesList.add(
                    MessageModel(
                        element.chatText,
                        true,
                        getTimeFromDateUsingSubStringAndSplitByColon(element.chattime),
                        element.chatType,
                        element.chatFileName,
                        element.originalName,
                        false,
                        element.username
                    )
                )
            } else {
                //receive msg
                messagesList.add(
                    MessageModel(
                        element.chatText,
                        false,
                        getTimeFromDateUsingSubStringAndSplitByColon(element.chattime),
                        element.chatType,
                        element.chatFileName,
                        element.originalName,
                        false,
                        element.username
                    )
                )
                Log.d("-----",": ${element.userId} , ${element.username}")
                remoteuserDetailsHashMap[element.userId] = element.username
            }
        }
    }

    override fun onResume() {
        super.onResume()
/*
        prefUserDetails.id.let {
            message?.let {
                messagesList.add(
                    MessageModel(
                        message = it.text.toString(),
                        isMine = false
                    )
                )
                messagesAdapter?.notifyItemInserted(messagesList.size)
                recyclerMessages.scrollToPosition(messagesList.size - 1)
            }
        }*/
    }

    override fun onPause() {
        try {
            val view = LayoutInflater.from(this).inflate(R.layout.group_chat_item_layout, rootView, false)
            ChatMessagesAdapter().MessageViewHolder(view).stopMediaPlayer()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        super.onPause()
    }


    private fun updateSendMessage(
        message: String,
        chatType: String,
        attachment: String,
        attachmentName: String,
        size: String?
    ) {
        if (isConnectedToNetwork(applicationContext)) {

            //Sending chat message
            socketEmitSendChat(message, chatType, attachment, size)

            //update recyclerview list data
            val messageModel = MessageModel(
                message,
                true,
                get24HrFormatTime(),
                chatType,
                attachment,
                attachmentName,
                false,
                ""
            )
            messagesList.add(messageModel)
            messagesAdapter?.notifyItemInserted(messagesList.size - 1)
            recyclerMessages.scrollToPosition(messagesList.size - 1)

            //clearing txtMessageBox text only if user send text
            etChatBox.setText("")
        } else {
            toast(getString(R.string.no_network))
        }

    }

    // Recycler Adapter
    inner class ChatMessagesAdapter :
        RecyclerView.Adapter<ChatMessagesAdapter.MessageViewHolder>() {

        // help to toggle between play and pause.
        private var playPause: Boolean = false

        //remain false till media is not completed, inside OnCompletionListener make it true.
        private var intialStage: Boolean = true
        private var chatList = messagesList


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
            val view = LayoutInflater.from(this@GroupChatActivity)
                .inflate(R.layout.group_chat_item_layout, parent, false)
            return MessageViewHolder(view)
        }

        override fun getItemCount(): Int = chatList.size

        override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
            val messageModel = chatList[position]
            holder.bindItem(messageModel)
        }

        // View Holder
        inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private var txtSendMessage: AppCompatTextView
            var txtReceiveMessage: AppCompatTextView
            var llMyMessage: LinearLayout
            var llOtherMessage: LinearLayout
            var txtMyName: TextView
            var txtOtherName: TextView
            var txtMyMessageTime: TextView
            var txtOtherMessageTime: TextView
            var imgMyUserImage: ImageView
            var imgOtherUserImage: ImageView
            var receiveAttachmentImg: ImageView
            var sendAttachmentImg: ImageView

            var sendAttachmentLayout: RelativeLayout
            var sendAttachmentTypeImg: ImageView
            var sendAttachmentTitle: TextView
            var sendAttachmentTypeTxt: TextView

            var receiveAttachmentLayout: RelativeLayout
            var receiveAttachmentTypeImg: ImageView
            var receiveAttachmentTitle: TextView
            var receiveAttachmentTypeTxt: TextView

            var sendImageAttachmentLL: LinearLayout
            var receiveImageAttachmentLL: LinearLayout
            var sendImageAttachmentLayoutUserTitle: TextView
            var sendImageAttachmentLayoutUserTime: TextView
            var receiveImageAttachmentLayoutUserTitle: TextView
            var receiveImageAttachmentLayoutUserTime: TextView

            //Audio player
            var llChatSendAudioPlayer: LinearLayout
            var llChatReceiveAudioPlayer: LinearLayout
            var ivAdapterSendPlay: ImageView
            var ivAdapterSendPasuse: ImageView
            var ivAdapterReceivePlay: ImageView
            var ivAdapterReceivePause: ImageView
            var seekBarReceiveAdapter: SeekBar
            var seekBarSendAdapter: SeekBar
            var progressbarChatSendDownload: ProgressBar
            var progressbarChatReceiveDownload: ProgressBar

            //Init audio player
            private var mPlayer: MediaPlayer? = MediaPlayer()
            private var fileName: String? = null
            private var lastProgress = 0
            private var mHandler = Handler()

            init {
                txtSendMessage = itemView.findViewById(R.id.group_chat_item_txt_send_message)
                txtReceiveMessage = itemView.findViewById(R.id.group_chat_item_txt_receive_message)
                llMyMessage = itemView.findViewById(R.id.group_chat_ll_send)
                llOtherMessage = itemView.findViewById(R.id.group_chat_ll_receive)
                txtMyName = itemView.findViewById(R.id.group_chat_item_txt_send_username)
                txtOtherName = itemView.findViewById(R.id.group_chat_item_txt_receive_username)
                txtMyMessageTime = itemView.findViewById(R.id.group_chat_item_txt_send_time)
                txtOtherMessageTime = itemView.findViewById(R.id.group_chat_item_txt_receive_time)
                imgMyUserImage = itemView.findViewById(R.id.group_chat_item_img_user_send)
                imgOtherUserImage = itemView.findViewById(R.id.group_chat_item_img_user_receive)
                receiveAttachmentImg = itemView.findViewById(R.id.group_chatItemReceiveAttachmentImg)
                sendAttachmentImg = itemView.findViewById(R.id.group_chat_item_send_attachment_img)

                sendAttachmentLayout = itemView.findViewById(R.id.group_chat_send_attachment_layout)
                sendAttachmentTypeImg = itemView.findViewById(R.id.ivGroupChatSendContentType)
                sendAttachmentTitle = itemView.findViewById(R.id.tvGroupChatSendContentTitle)
                sendAttachmentTypeTxt = itemView.findViewById(R.id.tvGroupChatSendContentType)

                receiveAttachmentLayout = itemView.findViewById(R.id.group_chat_receive_attachment_layout)
                receiveAttachmentTypeImg = itemView.findViewById(R.id.ivGroupChatReceiveContentType)
                receiveAttachmentTitle = itemView.findViewById(R.id.tvGroupChatReceiveContentTitle)
                receiveAttachmentTypeTxt = itemView.findViewById(R.id.tvGroupChatReceiveContentType)

                sendImageAttachmentLL = itemView.findViewById(R.id.group_chat_send_ll_AttachmentImg)
                receiveImageAttachmentLL  = itemView.findViewById(R.id.group_chat_receive_ll_AttachmentImg)
                sendImageAttachmentLayoutUserTitle  = itemView.findViewById(R.id.groupChatSendAttachmentImgTitle)
                sendImageAttachmentLayoutUserTime = itemView.findViewById(R.id.groupChatSendAttachmentImgTime)
                receiveImageAttachmentLayoutUserTitle = itemView.findViewById(R.id.groupChatReceiveAttachmentImgTitle)
                receiveImageAttachmentLayoutUserTime = itemView.findViewById(R.id.groupChatReceiveAttachmentImgTime)

                llChatSendAudioPlayer = itemView.findViewById(R.id.llGroupChatSendAudioPlayer)
                llChatReceiveAudioPlayer = itemView.findViewById(R.id.llGroupChatReceiveAudioPlayer)
                ivAdapterSendPlay = itemView.findViewById(R.id.ivAdapterGroupSendPlay)
                ivAdapterSendPasuse = itemView.findViewById(R.id.ivAdapterGroupSendPause)
                ivAdapterReceivePlay = itemView.findViewById(R.id.ivAdapterGroupReceivePlay)
                ivAdapterReceivePause = itemView.findViewById(R.id.ivAdapterGroupReceivePause)
                seekBarReceiveAdapter = itemView.findViewById(R.id.seekBarGroupReceiveAdapter)
                seekBarSendAdapter = itemView.findViewById(R.id.seekBarGroupSendAdapter)

                progressbarChatSendDownload =
                    itemView.findViewById(R.id.progressGroupChatSendContentUpload)
                progressbarChatReceiveDownload =
                    itemView.findViewById(R.id.progressGroupChatReceiveContentUpload)


            }

            fun bindItem(messageModel: MessageModel) {
                Log.d("----", " Type: ${messageModel.attachmentType}")

                if (messageModel.attachmentType == "text") {

                    sendAttachmentImg.visibility = View.GONE
                    receiveAttachmentImg.visibility = View.GONE
                    sendAttachmentLayout.visibility = View.GONE
                    receiveAttachmentLayout.visibility = View.GONE
                    sendImageAttachmentLL.visibility = View.GONE
                    receiveImageAttachmentLL.visibility = View.GONE

                    if (messageModel.isMine) {
                        llMyMessage.visibility = View.VISIBLE
                        imgMyUserImage.visibility = View.VISIBLE
                        txtSendMessage.visibility = View.VISIBLE

                        llOtherMessage.visibility = View.GONE
                        imgOtherUserImage.visibility = View.GONE
                        txtReceiveMessage.visibility = View.GONE


                        txtMyName.text = prefUserDetails.firstName
                        txtMyMessageTime.text = messageModel.time

                        if (messageModel.searchedKeyWord) {
                            txtSendMessage.setTextColor(Color.parseColor("#FFCF6F"))
                        } else txtSendMessage.setTextColor(Color.parseColor("#000000"))

                        txtSendMessage.text = messageModel.message

                    } else {
                        llMyMessage.visibility = View.GONE
                        imgMyUserImage.visibility = View.GONE
                        txtSendMessage.visibility = View.GONE

                        llOtherMessage.visibility = View.VISIBLE
                        imgOtherUserImage.visibility = View.VISIBLE
                        txtReceiveMessage.visibility = View.VISIBLE

                        txtOtherName.text = messageModel.remoteUserName
                        txtOtherMessageTime.text = messageModel.time

                        if (messageModel.searchedKeyWord) {
                            txtReceiveMessage.setTextColor(Color.parseColor("#FFCF6F"))
                        } else txtReceiveMessage.setTextColor(Color.parseColor("#FFFFFF"))

                        txtReceiveMessage.text = messageModel.message
                    }


                } else if (messageModel.attachmentType == "image") {
                    llMyMessage.visibility = View.GONE
                    llOtherMessage.visibility = View.GONE
                    txtSendMessage.visibility = View.GONE
                    txtReceiveMessage.visibility = View.GONE

                    if (messageModel.isMine) {
                        imgMyUserImage.visibility = View.VISIBLE
                        sendAttachmentImg.visibility = View.VISIBLE
                        sendImageAttachmentLL.visibility = View.VISIBLE

                        receiveAttachmentImg.visibility = View.GONE
                        imgOtherUserImage.visibility = View.GONE
                        receiveImageAttachmentLL.visibility = View.GONE

                        try {
                            //txtMyName.text = prefUserDetails.firstName
                            //txtMyMessageTime.text = messageModel.time
                            sendImageAttachmentLayoutUserTitle.text = prefUserDetails.firstName
                            sendImageAttachmentLayoutUserTime.text = messageModel.time

                            Log.d(
                                "-----",
                                " send : ${Constants.CHAT_ATTACHMENT_BASE_URL + "image/" + messageModel.attachmentFileName}"
                            )

                            Picasso
                                .get()
                                .load(Constants.CHAT_ATTACHMENT_BASE_URL + "image/" + messageModel.attachmentFileName)
                                .fit()
                                .placeholder(R.drawable.loading_text_image)
                                .into(sendAttachmentImg)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        sendAttachmentImg.setOnClickListener {
                            openFileInCustomImageActivity(Constants.CHAT_ATTACHMENT_BASE_URL + "image/" + messageModel.attachmentFileName)
                        }

                    } else {
                        imgMyUserImage.visibility = View.GONE
                        sendAttachmentImg.visibility = View.GONE
                        sendImageAttachmentLL.visibility = View.GONE

                        receiveImageAttachmentLL.visibility = View.VISIBLE
                        receiveAttachmentImg.visibility = View.VISIBLE
                        imgOtherUserImage.visibility = View.VISIBLE

                        try {
                            //txtOtherName.text = messageModel.remoteUserName
                            //txtOtherMessageTime.text = messageModel.time
                            receiveImageAttachmentLayoutUserTitle.text = messageModel.remoteUserName
                            receiveImageAttachmentLayoutUserTime.text = messageModel.time

                            Log.d(
                                "-----",
                                " rec : ${Constants.CHAT_ATTACHMENT_BASE_URL + "image/" + messageModel.attachmentFileName}"
                            )

                            Picasso
                                .get()
                                .load(Constants.CHAT_ATTACHMENT_BASE_URL + "image/" + messageModel.attachmentFileName)
                                .fit()
                                .placeholder(R.drawable.loading_text_image)
                                .into(receiveAttachmentImg)

                            receiveAttachmentImg.setOnClickListener {
                                openFileInCustomImageActivity(Constants.CHAT_ATTACHMENT_BASE_URL + "image/" + messageModel.attachmentFileName)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                } else if (messageModel.attachmentType == "audio") {
                    sendAttachmentTypeImg.setBackgroundResource(R.drawable.ic_volume)
                    receiveAttachmentTypeImg.setBackgroundResource(R.drawable.ic_volume)

                    sendAttachmentImg.visibility = View.GONE
                    receiveAttachmentImg.visibility = View.GONE
                    txtSendMessage.visibility = View.GONE
                    txtReceiveMessage.visibility = View.GONE
                    sendImageAttachmentLL.visibility = View.GONE
                    receiveImageAttachmentLL.visibility = View.GONE


                    if (messageModel.isMine) {
                        imgMyUserImage.visibility = View.VISIBLE
                        sendAttachmentLayout.visibility = View.VISIBLE
                        llMyMessage.visibility = View.VISIBLE
                        llChatSendAudioPlayer.visibility = View.VISIBLE

                        llOtherMessage.visibility = View.GONE
                        imgOtherUserImage.visibility = View.GONE
                        receiveAttachmentLayout.visibility = View.GONE
                        llChatReceiveAudioPlayer.visibility = View.GONE

                        txtMyName.text = prefUserDetails.firstName
                        txtMyMessageTime.text = messageModel.time
                        sendAttachmentTitle.text = messageModel.attachmentOriginalName
                        sendAttachmentTypeTxt.text = messageModel.attachmentType

                        //control audio player and view
                        mPlayer = MediaPlayer()
                        lastProgress = 0
                        mHandler = Handler()
                        playPause = false
                        intialStage = true

                        fileName =
                            Constants.CHAT_ATTACHMENT_BASE_URL + messageModel.attachmentType + "/" + messageModel.attachmentFileName
                        mPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)



                        ivAdapterSendPlay.setOnClickListener {
                            try {
                                playPause = if (!playPause) {
                                    if (intialStage)
                                        Player("send").execute(fileName)
                                    else {
                                        if (!mPlayer!!.isPlaying) {
                                            mPlayer!!.start()
                                            ivAdapterSendPlay.visibility = View.GONE
                                            ivAdapterSendPasuse.visibility = View.VISIBLE
                                        }
                                    }
                                    true
                                } else {
                                    if (mPlayer!!.isPlaying) {
                                        mPlayer!!.pause()
                                        ivAdapterSendPlay.visibility = View.VISIBLE
                                        ivAdapterSendPasuse.visibility = View.GONE
                                    }
                                    false
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        ivAdapterSendPasuse.setOnClickListener {
                            audioPause("send")
                        }

                    } else {
                        imgMyUserImage.visibility = View.GONE
                        sendAttachmentLayout.visibility = View.GONE
                        llMyMessage.visibility = View.GONE
                        llChatSendAudioPlayer.visibility = View.GONE

                        llOtherMessage.visibility = View.VISIBLE
                        imgOtherUserImage.visibility = View.VISIBLE
                        receiveAttachmentLayout.visibility = View.VISIBLE
                        llChatReceiveAudioPlayer.visibility = View.VISIBLE

                        txtOtherName.text = messageModel.remoteUserName
                        txtOtherMessageTime.text = messageModel.time
                        receiveAttachmentTitle.text = messageModel.attachmentOriginalName
                        receiveAttachmentTypeTxt.text = messageModel.attachmentType


                        //control audio player and view
                        mPlayer = MediaPlayer()
                        lastProgress = 0
                        mHandler = Handler()
                        playPause = false
                        intialStage = true

                        fileName =
                            Constants.CHAT_ATTACHMENT_BASE_URL + messageModel.attachmentType + "/" + messageModel.attachmentFileName
                        mPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)

                        ivAdapterReceivePlay.setOnClickListener {
                            playPause = if (!playPause) {
                                if (intialStage)
                                    Player("receive").execute(fileName)
                                else {
                                    if (!mPlayer!!.isPlaying) {
                                        mPlayer!!.start()
                                        ivAdapterReceivePlay.visibility = View.GONE
                                        ivAdapterReceivePause.visibility = View.VISIBLE
                                    }
                                }
                                true
                            } else {
                                if (mPlayer!!.isPlaying) {
                                    mPlayer!!.pause()
                                    ivAdapterReceivePlay.visibility = View.VISIBLE
                                    ivAdapterReceivePause.visibility = View.GONE
                                }
                                false
                            }
                        }

                        ivAdapterReceivePause.setOnClickListener {
                            audioPause("receive")
                        }
                    }

                } else if (messageModel.attachmentType == "video" || messageModel.attachmentType == "pdf" || messageModel.attachmentType == "doc") {
                    sendAttachmentImg.visibility = View.GONE
                    receiveAttachmentImg.visibility = View.GONE
                    txtSendMessage.visibility = View.GONE
                    txtReceiveMessage.visibility = View.GONE
                    llChatSendAudioPlayer.visibility = View.GONE
                    llChatReceiveAudioPlayer.visibility = View.GONE
                    sendImageAttachmentLL.visibility = View.GONE
                    receiveImageAttachmentLL.visibility = View.GONE

                    if (messageModel.isMine) {
                        imgMyUserImage.visibility = View.VISIBLE
                        sendAttachmentLayout.visibility = View.VISIBLE
                        llMyMessage.visibility = View.VISIBLE

                        llOtherMessage.visibility = View.GONE
                        imgOtherUserImage.visibility = View.GONE
                        receiveAttachmentLayout.visibility = View.GONE

                        txtMyName.text = prefUserDetails.firstName
                        txtMyMessageTime.text = messageModel.time
                        sendAttachmentTitle.text = messageModel.attachmentOriginalName
                        sendAttachmentTypeTxt.text = messageModel.attachmentType

                    } else {
                        imgMyUserImage.visibility = View.GONE
                        sendAttachmentLayout.visibility = View.GONE
                        llMyMessage.visibility = View.GONE

                        llOtherMessage.visibility = View.VISIBLE
                        imgOtherUserImage.visibility = View.VISIBLE
                        receiveAttachmentLayout.visibility = View.VISIBLE

                        txtOtherName.text = messageModel.remoteUserName
                        txtOtherMessageTime.text = messageModel.time
                        receiveAttachmentTitle.text = messageModel.attachmentOriginalName
                        receiveAttachmentTypeTxt.text = messageModel.attachmentType
                    }

                    sendAttachmentLayout.setOnClickListener {
                        if (messageModel.attachmentType == "video" || messageModel.attachmentType == "pdf" || messageModel.attachmentType == "doc") {
                            openFileInWebViewActivity(
                                messageModel.attachmentType + "/" + messageModel.attachmentFileName,
                                messageModel.attachmentType
                            )
                        }

                    }
                    receiveAttachmentLayout.setOnClickListener {
                        if (messageModel.attachmentType == "video" || messageModel.attachmentType == "pdf" || messageModel.attachmentType == "doc") {
                            openFileInWebViewActivity(
                                messageModel.attachmentType + "/" + messageModel.attachmentFileName,
                                messageModel.attachmentType
                            )
                        }
                    }

                    when (messageModel.attachmentType) {
                        "video" -> {
                            sendAttachmentTypeImg.setBackgroundResource(R.drawable.ic_video)
                            receiveAttachmentTypeImg.setBackgroundResource(R.drawable.ic_video)
                        }
                        "pdf" -> {
                            sendAttachmentTypeImg.setBackgroundResource(R.drawable.ic_pdf)
                            receiveAttachmentTypeImg.setBackgroundResource(R.drawable.ic_pdf)
                        }
                        "doc" -> {
                            sendAttachmentTypeImg.setBackgroundResource(R.drawable.ic_document)
                            receiveAttachmentTypeImg.setBackgroundResource(R.drawable.ic_document)
                        }
                    }
                }
            }

            /**
             * preparing mediaplayer will take sometime to buffer the content so prepare it inside the background thread and starting it on UI thread.
             * @author piyush
             */

            inner class Player(private var playAt: String) :
                AsyncTask<String?, Void?, Boolean?>() {
                private var urlString = ""

                override fun doInBackground(vararg params: String?): Boolean {

                    var prepared: Boolean
                    try {
                        urlString = params[0]!!
                        mPlayer?.setDataSource(params[0])
                        mPlayer?.prepare()

                        audioStartPlaying(playAt)

                        /*if (!isPlaying && fileName != null) {
                            isPlaying = true

                        } else {
                            isPlaying = false
                            audioStopPlaying("send")
                        }*/

                        prepared = true
                    } catch (e: IllegalArgumentException) {
                        Log.d("IllegarArgument", e.message!!)
                        prepared = false
                        e.printStackTrace()
                    } catch (e: SecurityException) {
                        prepared = false
                        e.printStackTrace()
                    } catch (e: IllegalStateException) {
                        prepared = false
                        e.printStackTrace()
                    } catch (e: IOException) {
                        prepared = false
                        e.printStackTrace()
                    }
                    return prepared
                }

                override fun onPostExecute(result: Boolean?) {
                    super.onPostExecute(result)

                    try {
                        runOnUiThread {
                            progressbarChatReceiveDownload.visibility = View.GONE
                            progressbarChatSendDownload.visibility = View.GONE
                        }

                        Log.d("----Prepared", "$result")

                        mPlayer?.start()
                        playPause = true
                        intialStage = false

                        audioStartPlaying(playAt)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onPreExecute() {
                    super.onPreExecute()

                    runOnUiThread {
                        // update the UI
                        if (playAt == "send") {
                            progressbarChatSendDownload.visibility = View.VISIBLE
                            progressbarChatReceiveDownload.visibility = View.GONE

                        } else if (playAt == "receive") {
                            progressbarChatReceiveDownload.visibility = View.VISIBLE
                            progressbarChatSendDownload.visibility = View.GONE
                        }
                    }
                }
            }

            private fun audioPause(playAt: String) {
                try {
                    Log.d("----", "playPause: $playPause")
                    if (playPause) {
                        playPause = false
                        mPlayer?.pause()

                        //showing the play button
                        runOnUiThread {
                            if (playAt == "send") {
                                ivAdapterSendPlay.visibility = View.VISIBLE
                                ivAdapterSendPasuse.visibility = View.GONE
                            } else if (playAt == "receive") {
                                ivAdapterReceivePlay.visibility = View.VISIBLE
                                ivAdapterReceivePause.visibility = View.GONE
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            private fun audioStartPlaying(playAt: String) {
                //making the imageView pause button
                runOnUiThread {
                    if (playAt == "send") {
                        ivAdapterSendPasuse.visibility = View.VISIBLE
                        seekBarSendAdapter.visibility = View.VISIBLE

                        seekBarReceiveAdapter.visibility = View.GONE
                        ivAdapterSendPlay.visibility = View.GONE

                    } else if (playAt == "receive") {
                        ivAdapterReceivePause.visibility = View.VISIBLE
                        seekBarReceiveAdapter.visibility = View.VISIBLE

                        ivAdapterReceivePlay.visibility = View.GONE
                        seekBarSendAdapter.visibility = View.GONE
                    }

                    seekBarSendAdapter.progress = lastProgress
                    seekBarReceiveAdapter.progress = lastProgress

                    mPlayer!!.seekTo(lastProgress)
                    seekBarSendAdapter.max = mPlayer!!.duration
                    seekBarReceiveAdapter.max = mPlayer!!.duration

                    seekBarUpdate()

                    mPlayer!!.setOnCompletionListener {
                        intialStage = true
                        playPause = false

                        if (playAt == "send") {
                            ivAdapterSendPlay.visibility = View.VISIBLE
                            ivAdapterSendPasuse.visibility = View.GONE
                        } else if (playAt == "receive") {
                            ivAdapterReceivePlay.visibility = View.VISIBLE
                            ivAdapterReceivePause.visibility = View.GONE
                        }

                        mPlayer!!.seekTo(0)
                        audioStopPlaying()

                    }
                }

                seekBarSendAdapter.setOnSeekBarChangeListener(object :
                    SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        try {
                            if (mPlayer != null && fromUser) {
                                mPlayer!!.seekTo(progress)
                                lastProgress = progress
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar) {}

                    override fun onStopTrackingTouch(seekBar: SeekBar) {}
                })

                seekBarReceiveAdapter.setOnSeekBarChangeListener(object :
                    SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        try {
                            if (mPlayer != null && fromUser) {
                                mPlayer!!.seekTo(progress)
                                lastProgress = progress
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar) {}

                    override fun onStopTrackingTouch(seekBar: SeekBar) {}
                })
            }

            private fun audioStopPlaying() {
                try {
                    playPause = false
                    mPlayer!!.stop()
                    mPlayer!!.reset()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                //mPlayer = null
            }

            private var runnable: Runnable = Runnable { seekBarUpdate() }

            private fun seekBarUpdate() {
                try {
                    if (mPlayer != null) {
                        val mCurrentPosition = mPlayer!!.currentPosition
                        seekBarSendAdapter.progress = mCurrentPosition
                        seekBarReceiveAdapter.progress = mCurrentPosition
                        lastProgress = mCurrentPosition
                        mHandler.postDelayed(runnable, 100)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

           fun stopMediaPlayer(){
               try {
                   Log.d("----","stopMediaPlayer called: ")
                   playPause = false
                   mPlayer!!.stop()
                   mPlayer!!.reset()
                   mPlayer = null
               } catch (e: Exception) {
                   e.printStackTrace()
               }
            }
        }


    }

    private fun openFileInCustomImageActivity(imageUrl: String) {
        val intent = Intent(this, CustomImageViewActivity::class.java)
        intent.putExtra("image_url", imageUrl)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun openFileInWebViewActivity(link: String, attachmentType: String) {
        Log.d("----", "Link: $link")

        val intent = Intent(this, WebViewActivity::class.java)
        intent.putExtra("link", link)
        intent.putExtra("attachmentType", attachmentType)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }


    private fun toggle() {
        if (menuKeyboard.isShowing) {
            menuKeyboard.dismiss()
        } else {
            menuKeyboard.show()
        }
    }


    override fun getPopup(): PopupWindow {
        return menuKeyboard
    }

    override fun dismissPopup() {
        menuKeyboard.dismiss()
    }

    override fun onGalleryClick(menuItem: String) {
        //toast(menuItem)

        when (menuItem) {
            "Camera" -> {
                openPicker()
            }
            "Gallery" -> {
                openPicker()
            }
            "Audio" -> {
                openActivityToPicFile("audio")
            }
            "Video" -> {
                openActivityToPicFile("video")
            }
            "Document" -> {
                openActivityToPicFile("doc")
            }
            "Pdf" -> {
                openActivityToPicFile("pdf")
            }
        }
    }

    private fun openActivityToPicFile(fileType: String) {
        val intent = Intent(this, AllFilesActivity::class.java)
        intent.putExtra("file_type", fileType)
        startActivityForResult(intent, 1001)
    }

    private fun openPicker() {
        ChiliPhotoPicker.init(
            loader = GlideImageLoader(),
            authority = "lv.chi.sample.fileprovider"
        )

        PhotoPickerFragment.newInstance(
            multiple = false,
            allowCamera = true,
            maxSelection = 1,
            theme = R.style.ChiliPhotoPicker_Dark
        ).show(supportFragmentManager, "picker")

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1001 && resultCode == RESULT_OK) {
            var title = data?.getStringExtra("title")
            val path = data?.getStringExtra("path")
            val type = data?.getStringExtra("type")
            val uri = data?.getStringExtra("uri")
            val size = data?.getStringExtra("size")

            Log.d("----", "Uri : $uri")

            title = Random.nextInt(0, 1000000000).toString() + "_cbc_" + title

            uploadFile(Uri.parse(uri), type!!, title, "", size!!)

        } else if (requestCode == 1002 && resultCode == RESULT_OK) {

            val recordAudioFile = data?.getStringExtra("record_audio")
            val recordAudioFileName = data?.getStringExtra("record_audio_name")
            Log.d("----", "Record Audio File Type 1: $recordAudioFileName")
            Log.d("----", "Record Audio File Name 2: $recordAudioFile")
            //updateSendMessage("", "audio", recordAudioFile!!, recordAudioFileName!!, recordAudioFileName.length.toString())
            uploadFile(
                Uri.parse(recordAudioFile),
                "audio",
                recordAudioFileName!!,
                "",
                recordAudioFileName.length.toString()
            )
        }
    }

    override fun onImagesPicked(photos: ArrayList<Uri>) {
        try {
            Log.d("Image uri ----", "${photos[0]}")
            Log.d("Image path ----", "${photos[0].path}")
            Log.d("----","getRealPathFromURI: ${FileUtils(applicationContext).getPath(photos[0])}")
            // D/Image path 2----: /storage/emulated/0/WhatsApp/Media/WhatsApp Images/IMG-20200321-WA0011.jpg

            val fileName = Random.nextInt(0, 1000000000).toString() + "_cbc_image.jpeg"
            val imgUri = Uri.parse(FileUtils(applicationContext).getPath(photos[0]))

            uploadFile(imgUri, "image", fileName, "", photos[0].path?.length.toString())

        } catch (e: Exception) {
            toast("Could not upload. Please try again.")
        }
    }


    private fun uploadFile(
        fileUri: Uri?,
        fileType: String,
        title: String,
        message: String,
        size: String
    ) = Coroutines.main {

        progressBarUpload.show()

        val service: FileUploadService =
            Servicegenerator.createService(FileUploadService::class.java)
        val attachment: MultipartBody.Part = prepareFilePart("attachment", fileUri!!)!!
        val token: RequestBody = createPartFromString(pref.getUserChatToken()!!)!!
        val type: RequestBody = createPartFromString(fileType)!!
        val userId: RequestBody = createPartFromString(prefUserDetails.id)!!
        val attachmentName: RequestBody = createPartFromString(title)!!

        //val uploadResponse = chatRepository.uploadFileToServer(token, type, userId, attachment, attachmentName)
        //Log.d("----", "upload Response: $uploadResponse")

        val call: Call<FileUploadResponse> =
            service.uploadFile(token, type, userId, attachmentName, attachment)

        call.enqueue(object : Callback<FileUploadResponse?> {
            override fun onResponse(
                call: Call<FileUploadResponse?>?,
                response: Response<FileUploadResponse?>
            ) {
                Log.d("----Response are ", response.toString())
                // {"success":true,"message":"uploaded successfully","url":"https://nodeserver.brainiuminfotech.com:3011/uploads/cbc/image/15292203_cbc_image.jpeg","originalName":"image.jpeg"}
                progressBarUpload.hide()

                if (response.isSuccessful) {
                    try {
                        val responseBody = response.body()
                        //val url = responseBody!!.url
                        val originalName = responseBody!!.originalName
                        //toast(responseBody.message)

                        updateSendMessage("", fileType, title, originalName, size)


                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<FileUploadResponse?>?, t: Throwable?) {
                progressBarUpload.hide()
            }
        })

    }

    private fun prepareFilePart(
        partName: String,
        fileUri: Uri
    ): MultipartBody.Part? {
        // https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
        // use the FileUtils to get the actual file by uri
        val picturePath = fileUri.path
        if (picturePath != null) {
            val file = File(picturePath)

            // create RequestBody instance from file
            val requestFile = file
                .asRequestBody(picturePath.toMediaTypeOrNull())

            // MultipartBody.Part is used to send also the actual file name
            return MultipartBody.Part.createFormData(partName, file.name, requestFile)
        }
        return null
    }

    private fun createPartFromString(value: String): RequestBody? {
        return value.toRequestBody("text/plain".toMediaTypeOrNull())
    }
}


/* private lateinit var list: ArrayList<Int>

 override fun onCreate(savedInstanceState: Bundle?) {
     super.onCreate(savedInstanceState)
     setContentView(R.layout.activity_group_chat)

     initData()

     //list = intent.getIntegerArrayListExtra(LIST)
     //Log.d("----","list.sum(): " + list.sum().toString())
 }

 private fun initData() {
     val intent = intent
     //val bundle = intent.getBundleExtra("BUNDLE")
     //val groupName = bundle?.getString("groupName")
     //val groupUserList = intent.getParcelableExtra("selected_group_list") as ArrayList<ContactEntity>

     //Log.d("----", "first user: ${groupUserList[0].display_name}")
     //Log.d("----", "grp name: $groupName")
 }

 companion object {
     const val LIST = "list"

     fun launch(context: Context, list: ArrayList<Int>) {
         val intent = Intent(context, GroupChatActivity::class.java)
         intent.putIntegerArrayListExtra(LIST, list)
         context.startActivity(intent)
     }
 }
}*/

package com.cbc.views.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cbc.R
import com.cbc.utils.Constants
import kotlinx.android.synthetic.main.activity_audio_call.*
import kotlinx.android.synthetic.main.item_action.*

class AudioCallActivity : AppCompatActivity() {
    private var userId = ""
    private var userCbcId = ""
    private var userImage = ""
    private var userName = ""
    private var userMobNo = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_call)

        initViews()
    }

    private fun initViews() {
        val intent = intent
        if(intent != null){
            val activityName = intent.getStringExtra("activityName")
            if(activityName == "GroupChatActivity"){
                userCbcId = intent.getStringExtra("groupId")!!
                userName = intent.getStringExtra("groupName")!!
                userImage = intent.getStringExtra("groupImage")!!

            } else if (activityName == "ChatActivity"){
                userName = intent.getStringExtra("receiveMsgUsername")!!
                userId = intent.getStringExtra("receiveMsgRemoteId")!!
                userCbcId = intent.getStringExtra("receiveMsgCbcId")!!
                userImage = intent.getStringExtra("receiveMsgCbcImage")!!
                userMobNo = intent.getStringExtra("remoteUserMobNo")!!
            }
        }

        tvUserName.text = userName
        tvCallingTime.text = "10:10 AM" // TODO Static

        if (userImage.isNotEmpty()) {
            Glide.with(applicationContext).load(Constants.CHAT_ATTACHMENT_BASE_URL + "image/" + userImage)
                .apply(RequestOptions.circleCropTransform().circleCrop()).into(ivUnderUserImg)
        }

        if (userImage.isNotEmpty()) {
            Glide.with(applicationContext).load(Constants.CHAT_ATTACHMENT_BASE_URL + "image/" + userImage)
                .apply(RequestOptions.circleCropTransform().circleCrop()).into(ivAboveUserImg)
        }

        ivRecCall.setOnClickListener {  }
        ivCallPutSpeaker.setOnClickListener {  }
        ivReceiveAndEndCallImg.setOnClickListener {  }
        ivVideoCall.setOnClickListener {  }

        ivBack.setOnClickListener {
            finish()
        }
    }
}

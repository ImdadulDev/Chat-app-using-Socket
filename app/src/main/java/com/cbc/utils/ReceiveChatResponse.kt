package com.cbc.utils
import com.google.gson.annotations.SerializedName

data class ReceiveChatResponse(
    @SerializedName("inituser")
    val inituser: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("remoteuserid")
    val remoteuserid: String,
    @SerializedName("roomid")
    val roomid: String
)
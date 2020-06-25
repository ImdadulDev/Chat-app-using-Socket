package com.cbc.data.apiresponse
import com.cbc.localdb.entities.GroupChatEntity
import com.google.gson.annotations.SerializedName


data class GroupChatHistoryResponse(
    @SerializedName("Chats")
    val chats: List<GroupChatEntity> = listOf(),
    @SerializedName("message")
    val message: String = "", // SERVER.SUCCESS
    @SerializedName("statusCode")
    val statusCode: Int = 0, // 200
    @SerializedName("success")
    val success: Boolean = false // true
)
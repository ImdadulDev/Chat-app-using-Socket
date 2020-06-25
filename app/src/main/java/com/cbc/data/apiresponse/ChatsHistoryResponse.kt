package com.cbc.data.apiresponse
import com.cbc.localdb.entities.ChatHistoryEntity
import com.google.gson.annotations.SerializedName

data class ChatsHistoryResponse(
    @SerializedName("Chats")
    val chats: List<ChatHistoryEntity> = listOf(),
    @SerializedName("success")
    val success: Boolean = false
)
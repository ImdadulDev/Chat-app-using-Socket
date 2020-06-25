package com.cbc.localdb.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class ChatHistoryEntity(
    @PrimaryKey
    @SerializedName("_id")
    val id: String = "",
    @SerializedName("remoteUserId")
    val remoteUserId: String = "",
    @SerializedName("attachment")
    val attachment: String = "",
    @SerializedName("attachmentName")
    val attachmentName: String = "",
    @SerializedName("chatDate")
    val chatDate: String = "",
    @SerializedName("chatFileName")
    val chatFileName: String = "",
    @SerializedName("chatMode")
    val chatMode: String = "",
    @SerializedName("chatText")
    val chatText: String = "",
    @SerializedName("chatType")
    val chatType: String = "",
    @SerializedName("chattime")
    val chattime: String = "",
    @SerializedName("createdAt")
    val createdAt: String = "",
    @SerializedName("originalName")
    val originalName: String = "",
    @SerializedName("readunread")
    val readunread: String = "",
    @SerializedName("remoteuserimage")
    val remoteuserimage: String = "",
    @SerializedName("roomId")
    val roomId: String = "",
    @SerializedName("size")
    val size: String = "",
    @SerializedName("updatedAt")
    val updatedAt: String = "",
    @SerializedName("userId")
    val userId: String = "",
    @SerializedName("__v")
    val v: Int = 0
)
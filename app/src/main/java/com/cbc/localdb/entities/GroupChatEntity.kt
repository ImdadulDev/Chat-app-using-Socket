package com.cbc.localdb.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class GroupChatEntity(
    @PrimaryKey(autoGenerate = false)
    @SerializedName("_id")
    val id: String = "", // 5ea164470021083eddb70184
    @SerializedName("attachment")
    val attachment: String = "",
    @SerializedName("attachmentName")
    val attachmentName: String = "",
    @SerializedName("chatDate")
    val chatDate: String = "", // 2020-04-23T10:02:00.000Z
    @SerializedName("chatFileName")
    val chatFileName: String = "",
    @SerializedName("chatText")
    val chatText: String = "", // hello
    @SerializedName("chatType")
    val chatType: String = "", // text
    @SerializedName("chattime")
    val chattime: String = "", // 2020-04-23 15:32:00
    @SerializedName("createdAt")
    val createdAt: String = "", // 2020-04-23T09:47:51.975Z
    @SerializedName("_group")
    val group: String = "", // 5e9efe2dcb9f106d7a41e755
    @SerializedName("originalName")
    val originalName: String = "",
    @SerializedName("phone")
    val phone: String = "", // 9564636037
    @SerializedName("size")
    val size: String = "",
    @SerializedName("updatedAt")
    val updatedAt: String = "", // 2020-04-23T09:47:51.975Z
    @SerializedName("_user")
    val user: String = "", // 5e8599db4993d7618dccb73c
    @SerializedName("userId")
    val userId: String = "", // 5e858f6d60f46f3b5ccbb578
    @SerializedName("userimage")
    val userimage: String = "", // uploads/profilepic/1586520993988.jpg
    @SerializedName("username")
    val username: String = "", // kiron
    @SerializedName("__v")
    val v: Int = 0 // 0
)
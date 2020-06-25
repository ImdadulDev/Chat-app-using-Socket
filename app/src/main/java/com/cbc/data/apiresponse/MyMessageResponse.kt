package com.cbc.data.apiresponse

import com.google.gson.annotations.SerializedName

data class MyMessageResponse(
    @SerializedName("data")
    val `data`: Data = Data(),
    @SerializedName("message")
    val message: String = "", // data
    @SerializedName("statusCode")
    val statusCode: Int = 0, // 2000
    @SerializedName("success")
    val success: Boolean = false // true
) {
    data class Data(
        @SerializedName("response_code")
        val responseCode: Int = 0, // 2000
        @SerializedName("response_data")
        val responseData: List<ResponseData> = listOf(),
        @SerializedName("response_message")
        val responseMessage: String = "" // List
    ) {
        data class ResponseData(
            @SerializedName("chatFileName")
            val chatFileName: String = "",
            @SerializedName("chatFileSize")
            val chatFileSize: String = "",
            @SerializedName("chat_id")
            val chatId: String = "",
            @SerializedName("chatMode")
            val chatMode: String = "",
            @SerializedName("chattime")
            val chattime: String = "", // 2020-04-22 14:09:00
            @SerializedName("createdAt")
            val createdAt: String = "", // 2020-04-17T11:05:09.291Z
            @SerializedName("groupDetail")
            val groupDetail: List<GroupDetail> = listOf(),
            @SerializedName("group_id")
            val groupId: String = "",
            @SerializedName("group_image")
            val groupImage: String = "",
            @SerializedName("group_name")
            val groupName: String = "",
            @SerializedName("_id")
            val id: String = "", // 5e998d6500c571431bba0ddb
            @SerializedName("lastMessageusername")
            val lastMessageusername: String = "",
            @SerializedName("lastmessage")
            val lastmessage: String = "",
            @SerializedName("lastmessageTime")
            val lastmessageTime: String = "",
            @SerializedName("lastmessageType")
            val lastmessageType: String = "",
            @SerializedName("lastmessageUserId")
            val lastmessageUserId: String = "",
            @SerializedName("messagedelete")
            val messagedelete: String = "",
            @SerializedName("msg")
            val msg: String = "", // hello
            @SerializedName("originalName")
            val originalName: String = "",
            @SerializedName("phone")
            val phone: String = "", // 9564636037
            @SerializedName("readStatus")
            val readStatus: String = "", // UNREAD
            @SerializedName("remoteUserId")
            val remoteUserId: String = "", // 5e8ab0d760f46f3b5ccbbcfd
            @SerializedName("remoteuserimage")
            val remoteuserimage: String = "",
            @SerializedName("remoteusername")
            val remoteusername: String = "", // joyetadey1
            @SerializedName("time")
            val time: String = "", // 2020-04-22T08:39:00.000Z
            @SerializedName("type")
            val type: String = "", // text
            @SerializedName("unreadcount")
            val unreadcount: Int = 0, // 0
            @SerializedName("updatedAt")
            val updatedAt: String = "", // 2020-04-22T08:24:39.528Z
            @SerializedName("userId")
            val userId: String = "", // 5e858f6d60f46f3b5ccbb578
            @SerializedName("userimage")
            val userimage: String = "", // uploads/profilepic/1586520993988.jpg
            @SerializedName("username")
            val username: String = "", // kiron
            @SerializedName("__v")
            val v: Int = 0, // 0
            @SerializedName("whoom")
            val whoom: String = "" // 5e858f6d60f46f3b5ccbb578
        ) {
            data class GroupDetail(
                @SerializedName("chatUserId")
                val chatUserId: String = "", // 5e8ab0d760f46f3b5ccbbcfd
                @SerializedName("createdAt")
                val createdAt: String = "", // 2020-04-21T14:07:41.700Z
                @SerializedName("_group")
                val group: String = "", // 5e9efe2dcb9f106d7a41e755
                @SerializedName("groupImageName")
                val groupImageName: String = "",
                @SerializedName("_id")
                val id: String = "", // 5e9efe2dcb9f106d7a41e756
                @SerializedName("moderatorType")
                val moderatorType: String = "", // User
                @SerializedName("phone")
                val phone: String = "", // 9432881076
                @SerializedName("readunread")
                val readunread: String = "", // on
                @SerializedName("updatedAt")
                val updatedAt: String = "", // 2020-04-23T05:46:00.648Z
                @SerializedName("_user")
                val user: String = "", // 5e8ab0d760f46f3b5ccbbcfd
                @SerializedName("userimage")
                val userimage: String = "",
                @SerializedName("username")
                val username: String = "", // joyetadey1
                @SerializedName("userFullName")
                val userFullName: String = "",
                @SerializedName("__v")
                val v: Int = 0 // 0
            )
        }
    }
}
package com.cbc.data.apiresponse

import com.google.gson.annotations.SerializedName

data class CreateGroupResponse(
    @SerializedName("groupImageName")
    val groupImageName: String = "",
    @SerializedName("message")
    val message: String = "", // SERVER.SUCCESS
    @SerializedName("result")
    val result: List<Result> = listOf(),
    @SerializedName("statusCode")
    val statusCode: Int = 0, // 200
    @SerializedName("success")
    val success: Boolean = false // true
) {
    data class Result(
        @SerializedName("chatUserId")
        val chatUserId: String = "", // 5e8ab0d760f46f3b5ccbbcfd
        @SerializedName("createdAt")
        val createdAt: String = "", // 2020-05-01T05:33:15.212Z
        @SerializedName("_group")
        val group: Group = Group(),
        @SerializedName("groupImageName")
        val groupImageName: String = "",
        @SerializedName("_id")
        val id: String = "", // 5eabb49bace66e3c6883aff9
        @SerializedName("moderatorType")
        val moderatorType: String = "", // User
        @SerializedName("phone")
        val phone: String = "", // 9432881076
        @SerializedName("readunread")
        val readunread: String = "",
        @SerializedName("updatedAt")
        val updatedAt: String = "", // 2020-05-01T05:33:15.212Z
        @SerializedName("_user")
        val user: String = "", // 5e8ab0d760f46f3b5ccbbcfd
        @SerializedName("userFullName")
        val userFullName: String = "", // joyeta dey
        @SerializedName("userimage")
        val userimage: String = "",
        @SerializedName("username")
        val username: String = "", // joyetadey1
        @SerializedName("__v")
        val v: Int = 0 // 0
    ) {
        data class Group(
            @SerializedName("createdAt")
            val createdAt: String = "", // 2020-05-01T05:33:15.195Z
            @SerializedName("groupAdminId")
            val groupAdminId: String = "",
            @SerializedName("groupImageName")
            val groupImageName: String = "",
            @SerializedName("groupName")
            val groupName: String = "", // helloGroup31
            @SerializedName("_id")
            val id: String = "", // 5eabb49bace66e3c6883aff8
            @SerializedName("msg")
            val msg: String = "",
            @SerializedName("msg_time")
            val msgTime: String = "",
            @SerializedName("msg_type")
            val msgType: String = "",
            @SerializedName("status")
            val status: String = "", // Active
            @SerializedName("updatedAt")
            val updatedAt: String = "", // 2020-05-01T05:33:15.195Z
            @SerializedName("_user")
            val user: String = "", // 5e858f6d60f46f3b5ccbb578
            @SerializedName("userId")
            val userId: String = "",
            @SerializedName("__v")
            val v: Int = 0 // 0
        )
    }
}
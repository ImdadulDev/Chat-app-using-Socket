package com.cbc.data.apiresponse

import com.google.gson.annotations.SerializedName

data class RemoveUserResponse(
    @SerializedName("groupImageName")
    val groupImageName: String = "", // 991.jpeg
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
        val chatUserId: String = "", // 5eac054fe96f363e5fdf6e6e
        @SerializedName("createdAt")
        val createdAt: String = "", // 2020-05-04T13:34:56.157Z
        @SerializedName("_group")
        val group: Group = Group(),
        @SerializedName("groupImageName")
        val groupImageName: String = "",
        @SerializedName("_id")
        val id: String = "", // 5eb01a006175865f973875de
        @SerializedName("moderatorType")
        val moderatorType: String = "", // User
        @SerializedName("phone")
        val phone: String = "", // 9432881076
        @SerializedName("readunread")
        val readunread: String = "", // on
        @SerializedName("updatedAt")
        val updatedAt: String = "", // 2020-05-05T05:06:22.846Z
        @SerializedName("_user")
        val user: String = "", // 5eac054fe96f363e5fdf6e6e
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
            val createdAt: String = "", // 2020-05-04T13:34:56.150Z
            @SerializedName("groupAdminId")
            val groupAdminId: String = "",
            @SerializedName("groupImageName")
            val groupImageName: String = "", // 991.jpeg
            @SerializedName("groupName")
            val groupName: String = "", // Friends for ever
            @SerializedName("_id")
            val id: String = "", // 5eb01a006175865f973875dd
            @SerializedName("msg")
            val msg: String = "", // hi
            @SerializedName("msg_time")
            val msgTime: String = "", // 2020-05-05 10:52:00
            @SerializedName("msg_type")
            val msgType: String = "", // text
            @SerializedName("status")
            val status: String = "", // Active
            @SerializedName("updatedAt")
            val updatedAt: String = "", // 2020-05-05T05:11:00.840Z
            @SerializedName("_user")
            val user: String = "", // 5eac054de96f363e5fdf6e6d
            @SerializedName("userId")
            val userId: String = "", // 5eac054fe96f363e5fdf6e6e
            @SerializedName("__v")
            val v: Int = 0 // 0
        )
    }
}
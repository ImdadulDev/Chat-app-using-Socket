package com.cbc.data.apiresponse
import com.google.gson.annotations.SerializedName


data class EditGroupImageResponse(
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
        val chatUserId: String = "", // 5e8aab8e60f46f3b5ccbbc62
        @SerializedName("createdAt")
        val createdAt: String = "", // 2020-04-28T11:50:08.392Z
        @SerializedName("_group")
        val group: Group = Group(),
        @SerializedName("groupImageName")
        val groupImageName: String = "",
        @SerializedName("_id")
        val id: String = "", // 5ea818709b807245e895f518
        @SerializedName("moderatorType")
        val moderatorType: String = "", // User
        @SerializedName("phone")
        val phone: String = "", // 8116668467
        @SerializedName("readunread")
        val readunread: String = "",
        @SerializedName("updatedAt")
        val updatedAt: String = "", // 2020-04-28T11:50:08.392Z
        @SerializedName("_user")
        val user: String = "", // 5e8aab8e60f46f3b5ccbbc62
        @SerializedName("userimage")
        val userimage: String = "",
        @SerializedName("username")
        val username: String = "", // ipsita.deb
        @SerializedName("__v")
        val v: Int = 0 // 0
    ) {
        data class Group(
            @SerializedName("createdAt")
            val createdAt: String = "", // 2020-04-28T11:50:08.381Z
            @SerializedName("groupAdminId")
            val groupAdminId: String = "",
            @SerializedName("groupImageName")
            val groupImageName: String = "", // 991.jpeg
            @SerializedName("groupName")
            val groupName: String = "", // Test one
            @SerializedName("_id")
            val id: String = "", // 5ea818709b807245e895f517
            @SerializedName("msg")
            val msg: String = "",
            @SerializedName("msg_time")
            val msgTime: String = "",
            @SerializedName("msg_type")
            val msgType: String = "",
            @SerializedName("status")
            val status: String = "", // Active
            @SerializedName("updatedAt")
            val updatedAt: String = "", // 2020-04-28T12:21:53.138Z
            @SerializedName("_user")
            val user: String = "", // 5e858f6d60f46f3b5ccbb578
            @SerializedName("userId")
            val userId: String = "",
            @SerializedName("__v")
            val v: Int = 0 // 0
        )
    }
}
package com.cbc.data.apiresponse

import com.google.gson.annotations.SerializedName

data class LeaveGroupResponse(
    @SerializedName("message")
    val message: String = "",
    @SerializedName("statusCode")
    val statusCode: String = "",
    @SerializedName("success")
    val success: String = ""
)
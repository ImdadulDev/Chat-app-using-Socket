package com.cbc.views.ui.profile.model


import com.google.gson.annotations.SerializedName

data class Profile(
    @SerializedName("response_code")
    var responseCode: Int,
    @SerializedName("response_data")
    var responseData: ResponseData,
    @SerializedName("response_message")
    var responseMessage: String
)
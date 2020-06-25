package com.cbc.views.ui.profile.model


import com.google.gson.annotations.SerializedName

data class ResponseData(
    @SerializedName("countryCode")
    var countryCode: String,
    @SerializedName("email")
    var email: String,
    @SerializedName("firstName")
    var firstName: String,
    @SerializedName("_id")
    var id: String,
    @SerializedName("lastName")
    var lastName: String,
    @SerializedName("phone")
    var phone: String,
    @SerializedName("profileImage")
    var profileImage: String,
    @SerializedName("userName")
    var userName: String
)
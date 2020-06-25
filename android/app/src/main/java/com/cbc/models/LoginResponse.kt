package com.cbc.models


import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("answerOne")
    val answerOne: String,
    @SerializedName("answerTwo")
    val answerTwo: String,
    @SerializedName("authToken")
    val authToken: String,
    @SerializedName("countryCode")
    val countryCode: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("_id")
    val id: String,
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("otpVerify")
    val otpVerify: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("questionOne")
    val questionOne: String,
    @SerializedName("questionTwo")
    val questionTwo: String,
    @SerializedName("userName")
    val userName: String
)
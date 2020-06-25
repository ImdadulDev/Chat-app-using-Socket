package com.cbc.models.country


import com.google.gson.annotations.SerializedName

data class Country(
    @SerializedName("callingCode")
    val callingCode: String,
    @SerializedName("code")
    val code: String,
    @SerializedName("name")
    val name: String
)
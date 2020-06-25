package com.cbc.data.apiresponse
import com.google.gson.annotations.SerializedName

data class FileUploadResponse(
    @SerializedName("success")
    val success: Boolean = false,
    @SerializedName("message")
    val message: String = "",
    @SerializedName("url")
    val url: String = "",
    @SerializedName("originalName")
    val originalName: String = ""
)
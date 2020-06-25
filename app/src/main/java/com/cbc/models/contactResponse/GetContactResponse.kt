package com.cbc.models.contactResponse


import com.cbc.localdb.ContactEntity
import com.google.gson.annotations.SerializedName

data class GetContactResponse(
    @SerializedName("response_code")
    val responseCode: Int,
    @SerializedName("response_data")
    val responseData: List<ContactEntity>,
    @SerializedName("response_message")
    val responseMessage: String
)
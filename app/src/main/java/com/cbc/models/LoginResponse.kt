package com.cbc.models


import android.os.Parcel
import android.os.Parcelable
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
    @SerializedName("profileImage")
    val profileImage: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("questionOne")
    val questionOne: String,
    @SerializedName("questionTwo")
    val questionTwo: String,
    @SerializedName("userName")
    val userName: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(answerOne)
        parcel.writeString(answerTwo)
        parcel.writeString(authToken)
        parcel.writeString(countryCode)
        parcel.writeString(email)
        parcel.writeString(firstName)
        parcel.writeString(id)
        parcel.writeString(lastName)
        parcel.writeString(otpVerify)
        parcel.writeString(profileImage)
        parcel.writeString(phone)
        parcel.writeString(questionOne)
        parcel.writeString(questionTwo)
        parcel.writeString(userName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LoginResponse> {
        override fun createFromParcel(parcel: Parcel): LoginResponse {
            return LoginResponse(parcel)
        }

        override fun newArray(size: Int): Array<LoginResponse?> {
            return arrayOfNulls(size)
        }
    }

}
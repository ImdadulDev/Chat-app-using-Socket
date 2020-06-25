package com.cbc.localdb

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class ContactEntity (
    @SerializedName("_id")
    val id: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("mobile_no")
    @PrimaryKey
    val mobile_no: String,
    @SerializedName("display_mobile_no")
    val display_mobile_no: String,
    @SerializedName("display_name")
    val display_name: String,
    @SerializedName("quickblox_id")
    val quickblox_id: String,
    @SerializedName("twilio_id")
    val twilio_id: String,
    @SerializedName("is_cbc_backedup")
    var is_cbc_backedup: Boolean,
    @SerializedName("cbc_id")
    val cbc_id: String
): Parcelable


/*
@Entity
data class ContactEnity(
    @PrimaryKey(autoGenerate = false)
    val mobile_no: String,
    @ColumnInfo(name = "display_mobile_no")
    val display_mobile_no: String?,
//    @ColumnInfo(name = "mobileno") val mobileno: String?,
    @ColumnInfo(name = "display_name")
    val display_name: String?,
    @ColumnInfo(name = "quickblox_id")
    val quickblox_id: String?,
    @ColumnInfo(name = "twilio_id")
    val twilio_id: String?,
    @ColumnInfo(name = "is_cbc_backedup")
    var is_cbc_backedup: Boolean,
    @ColumnInfo(name = "cbc_id")
    val cbc_id: String

)*/

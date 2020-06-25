package com.cbc.Localdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ContactEntity(
    @PrimaryKey
    val id: String,
    val image: String,
    val mobileno: String,
//    @ColumnInfo(name = "mobileno") val mobileno: String?,
    @ColumnInfo(name = "display_name")
    val displayname: String?,
    @ColumnInfo(name = "quickbloxid")
    val quickbloxid: String?,
    @ColumnInfo(name = "twilio_id")
    val twilio_id: String?,
    @ColumnInfo(name = "is_cbc_backedup")
    val is_cbc_backedup: Boolean,
    @ColumnInfo(name = "cbc_id")
    val cbc_id: String

)
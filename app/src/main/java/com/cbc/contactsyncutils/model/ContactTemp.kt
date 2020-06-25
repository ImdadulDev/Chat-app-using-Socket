package com.cbc.contactsyncutils.model

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity
data class ContactTemp(
    @ColumnInfo(name = "display_name")
    val displayname: String,
    @ColumnInfo(name = "display_mobile_no")
    val number: String
)
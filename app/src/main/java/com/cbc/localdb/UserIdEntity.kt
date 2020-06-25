package com.cbc.localdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserIdEntity(
    @PrimaryKey(autoGenerate = false)
    val _id: String,
    @ColumnInfo(name = "email")
    val email: String
)
package com.cbc.localdb.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GroupMemberEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val userId: String,
    val groupId: String,
    val groupName: String,
    val username: String,
    val userImage: String,
    val userMobNo: String
)
package com.cbc.data

data class PreviousChatsApiBody (
    val remoteUserId: String,
    val userId: String,
    val token: String,
    val chatMode: String
)
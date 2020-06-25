package com.cbc.models

data class MessageModel (
    val message: String = "",
    val isMine: Boolean = true,
    val time: String = "",
    val attachmentType: String = "",
    val attachmentFileName: String = "",
    val attachmentOriginalName:String = "",
    var searchedKeyWord: Boolean = false,
    val remoteUserName: String = ""

)
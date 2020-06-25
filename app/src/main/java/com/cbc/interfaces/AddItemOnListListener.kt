package com.cbc.interfaces

import com.cbc.localdb.ContactEntity

interface AddItemOnListListener {
    fun addItemOnListListener (userList: ArrayList<ContactEntity>)
}
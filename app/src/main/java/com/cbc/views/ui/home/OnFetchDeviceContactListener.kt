package com.cbc.views.ui.home

import com.cbc.localdb.ContactEntity

interface OnFetchDeviceContactListener {
    fun onFetchComplete()
    fun onFetchLocalContacts(list: List<ContactEntity>, iSingle: Boolean)
}
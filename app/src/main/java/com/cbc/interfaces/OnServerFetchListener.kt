package com.cbc.interfaces

interface OnServerFetchListener {
    fun onServerComplete()
    fun onFetchError(errormsg: String)
}
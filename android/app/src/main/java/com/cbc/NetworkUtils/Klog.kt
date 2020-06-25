package com.cbc.NetworkUtils

import android.util.Log

object Klog {
    fun d(tag: String, msg: String) {
        Log.d(tag, msg)
    }

    fun e(tag: String, msg: String) {
        Log.e(tag, msg)
    }
}
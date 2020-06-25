package com.cbc.utils

import android.util.Log

object Klog {
    fun D(tag: String, msg: String) {
        Log.d(tag, msg)
    }

    fun E(tag: String, msg: String) {
        Log.e(tag, msg)
    }
}
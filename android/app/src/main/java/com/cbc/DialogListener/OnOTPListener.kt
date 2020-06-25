package com.CBC.DialogListener

interface OnOTPListener {
    fun OnOTPVerified(response: String?)
    fun OnOTPFailed(error: String?)
}
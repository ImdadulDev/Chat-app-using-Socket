package  com.cbc.dialoglistener

interface OnOTPListener {
    fun OnOTPVerified(response: String?)
    fun OnOTPFailed(error: String?)
}
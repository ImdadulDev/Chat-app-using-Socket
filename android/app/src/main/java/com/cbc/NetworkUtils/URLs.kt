package com.app.RitoRideSharingAppPassenger.NetworkUtils

object URLs {
    val baseuser = "https://nodeserver.brainiuminfotech.com:3007/"
    val LOGIN = baseuser.plus("api/login")
    val REGISTER = baseuser.plus("api/register")
    val SENDOTP = baseuser.plus("api/sent-otp")
    val OTPVERIFY = baseuser.plus("api/otpVerification")
    val SOCAILLOGIN = baseuser.plus("api/check-social")
    val SOCAILREGISTRATION = baseuser.plus("api/register-social")
    val CHANGEPASSWORD = baseuser.plus("api/change-password")
    val PROFILEPICUPDATE = baseuser.plus("api/profile-image-upload")
    val GETPROFILE = baseuser.plus("api/get-user-profile")
    val UPDATEPROFILE = baseuser.plus("api/set-user-profile")


}
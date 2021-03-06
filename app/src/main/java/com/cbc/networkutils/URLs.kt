package  com.cbc.networkutils

object URLs {
    val baseuser = "https://nodeserver.brainiuminfotech.com:3007/"
    val LOGIN = baseuser.plus("api/login")
    val REGISTER = baseuser.plus("api/register")
    val SENDOTP = baseuser.plus("api/sent-otp")
    val RESENDOTP = baseuser.plus("api/otpResend")
    val FORGOTPASSWORD = baseuser.plus("api/forgotPassword")
    val FORGOTQUESTION = baseuser.plus("api/forgotSecurityQuestion")
    val CHECKQUESTION = baseuser.plus("api/checkSecurityQuestion")
    val OTPVERIFY = baseuser.plus("api/otpVerification")
    val CONTACTUPLOAD = baseuser.plus("api/contactSync") //JSONArry upload
    val GETCONTCATLIST = baseuser.plus("api/contactList")
    val CHANGEPASSWORD = baseuser.plus("api/changePassword")
    val PROFILEPICUPDATE = baseuser.plus("api/profile-image-upload")
    val GETPROFILE = baseuser.plus("api/viewProfile")
    val UPDATEPROFILE = baseuser.plus("api/editProfile")
}
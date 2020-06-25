package com.cbc.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.cbc.R
import com.cbc.localdb.ContactEntity
import com.google.android.material.snackbar.Snackbar
import kotlinx.io.ByteArrayOutputStream
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


fun get24HrFormatTime(): String {
    val calendar = Calendar.getInstance()
    val simpleDateFormat = SimpleDateFormat("hh:mm", Locale.getDefault())
    return simpleDateFormat.format(calendar.time)
}

fun get12HrFormatTime(): String {
    val calendar = Calendar.getInstance()
    val simpleDateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    return simpleDateFormat.format(calendar.time)
}

fun showSnackBar(
    context: Context,
    view: View,
    msg: String,
    string: String
) {
    val snack = Snackbar.make(
        view,
        msg,
        Snackbar.LENGTH_LONG
    )
    snack.setBackgroundTint(ContextCompat.getColor(context, R.color.grey_10))
    snack.setTextColor(ContextCompat.getColor(context, R.color.white))
    snack.setActionTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
    snack.setAction("Close") {
        snack.dismiss()
    }

    snack.show()
}

fun showSnackBar(
    context: Context,
    view: View,
    msg: String,
    btnTxt: String,
    action: (View) -> Unit
) {
    val snack = Snackbar.make(
        view,
        msg,
        Snackbar.LENGTH_LONG

    )
    snack.setBackgroundTint(ContextCompat.getColor(context, R.color.grey_10))
    snack.setTextColor(ContextCompat.getColor(context, R.color.white))
    snack.setActionTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
    snack.setAction(btnTxt) {
        snack.dismiss()
    }

    snack.show()
}


fun View.showSnackbar(msgId: Int, length: Int) {
    showSnackbar(context.getString(msgId), length)
}

fun View.showSnackbar(msg: String, length: Int) {
    showSnackbar(msg, length, null) {}
}

fun View.showSnackbar(
    msgId: Int,
    length: Int,
    actionMessageId: Int,
    action: (View) -> Unit
) {
    showSnackbar(context.getString(msgId), length, context.getString(actionMessageId), action)
}

fun View.showSnackbar(
    msg: String,
    length: Int,
    actionMessage: CharSequence?,
    action: (View) -> Unit
) {
    val snackbar = Snackbar.make(this, msg, length)
    if (actionMessage != null) {
        snackbar.setAction(actionMessage) {
            action(this)
        }.show()
    }
}

fun showAlertDialog(context: Context, title: String, msg: String): Boolean {

    var returnValue: Boolean = false

    // Initialize a new instance of
    val builder = AlertDialog.Builder(context)

    // Set the alert dialog title
    builder.setTitle(title)

    // Display a message on alert dialog
    builder.setMessage(msg)

    // Set a positive button and its click listener on alert dialog
    builder.setPositiveButton("Ok") { dialog, _ ->
        // Do something when user press the positive button
        dialog.dismiss()

        returnValue = true
    }


    // Display a negative button on alert dialog
    builder.setNegativeButton("No") { dialog, _ ->
        dialog.dismiss()

        returnValue = false
    }

    // Finally, make the alert dialog using builder
    val dialog: AlertDialog = builder.create()

    // Display the alert dialog on app interface
    dialog.show()

    return returnValue
}


@Suppress("DEPRECATION")
fun isConnectedToNetwork(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    } else {
        val nwInfo = connectivityManager.activeNetworkInfo ?: return false
        return nwInfo.isConnected
    }
}

fun EditText.onChange(cb: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            cb(s.toString())
        }
    })
}

fun getCurrentDate(): String {
    val currentDateAndTime: String
    currentDateAndTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd MMM, YYYY")
        current.format(formatter)
        //println("Current Date and Time is: $currentDateAndTime")
    } else {
        val date = Date()
        val formatter = SimpleDateFormat("dd MMM, YYYY", Locale.getDefault())
        formatter.format(date)
        //Log.d("answer",formatter.format(date))
    }

    return currentDateAndTime
}

fun getFormattedDateAndTimeTime(): String {
    val currentDateAndTime: String
    currentDateAndTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a")
        println("Current Date and Time is: ${current.format(formatter)}")
        current.format(formatter)

    } else {
        val date = Date()
        val formatter = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())
        //Log.d("answer", formatter.format(date))
        formatter.format(date)
    }

    return currentDateAndTime
}

fun getDateAndTimeDifference(beginDate: String): Int {
    val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(beginDate)

    //val userDob = SimpleDateFormat("yyyy-MM-dd").parse("2020-02-16")
    val today = Date()
    val diff = today.time - date.time
    val numOfDays = (diff / (1000 * 60 * 60 * 24)).toInt()


    //println("--- 4: $numOfDays")

    return numOfDays
}

fun getTimeFromDateUsingSubStringAndSplit(dateString: String): String {
    // 2020-03-27T05:45:00.000Z
    val time = dateString.split("T")[1]
    return time.substring(0,5)
}

fun getTimeFromDateUsingSubStringAndSplitByColon(dateString: String): String {
    //2020-04-22 14:09:00
    val time = dateString.split(" ")[1]
    return time.substring(0,5)
}


fun get12hrTimeFromDateUsingSubStringAndSplit(dateString: String): String {
    // 2020-04-06 02:28 pm
    Log.d("----","time lenght: " + dateString.length)
    return dateString.substring(dateString.length - 8)
}

fun getTimeFromDate(dateString: String): String {
    // 2020-03-27T05:45:00.000Z
    var timeString = ""
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val inputFormatter =
            DateTimeFormatter.ofPattern(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                Locale.ENGLISH
            )
        val outputFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH)
        val date = LocalDate.parse(dateString, inputFormatter)
        timeString = outputFormatter.format(date)
        println("----$timeString")
    } else {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val outputFormat = SimpleDateFormat("HH:mm")
        val date = inputFormat.parse(dateString)
        timeString = outputFormat.format(date)
        println("----$timeString") // prints 10-04-2018

    }

    return timeString
}

fun getDisplayWidth(activity: Activity): Int {
    val displayMetrics = DisplayMetrics()
    activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics.widthPixels
}

fun getDisplayHeight(activity: Activity): Int {
    val displayMetrics = DisplayMetrics()
    activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics.heightPixels
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Int.toPx() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun getStringSizeLengthFile(size: Long): String {
    val df = DecimalFormat("0.00")
    val sizeKb = 1024.0f
    val sizeMb = sizeKb * sizeKb
    val sizeGb = sizeMb * sizeKb
    val sizeTerra = sizeGb * sizeKb
    return when {
        size < sizeMb -> df.format(size / sizeKb)
            .toString() + " Kb"
        size < sizeGb -> df.format(size / sizeMb)
            .toString() + " Mb"
        size < sizeTerra -> df.format(size / sizeGb)
            .toString() + " Gb"
        else -> ""
    }
}

fun itemListToJsonConvert(list: ArrayList<ContactEntity>): String {
    // creating this
    /* {"Values":[{"chatUserId":"5e89c8ee60f46f3b5ccbb659"}]}
    groupUsers: {"Values": [{
        "chatUserId": 1349
        },
        {
        "chatUserId": 1350
        }]}
     */

    val jResult = JSONObject() // main object
    val jArray = JSONArray() // /ItemDetail jsonArray

    for (i in 0 until list.size) {
        val jGroup = JSONObject() // /sub Object
        try {
            jGroup.put("chatUserId", list[i].cbc_id)
            jArray.put(jGroup)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    // /itemDetail Name is JsonArray Name
    jResult.put("Values", jArray)
    return jResult.toString()
}


fun getConvertUTCtoLocalTime(dateTime:String): String{
    // 2020-04-06 02:28 pm
    val dateStr = "Jul 16, 2013 12:08:59 AM"
    val df = SimpleDateFormat("yyyy HH:mm:ss a", Locale.ENGLISH)
    df.timeZone = TimeZone.getTimeZone("UTC")
    val date = df.parse(dateStr)
    df.timeZone = TimeZone.getDefault()
    val formattedDate = df.format(date)

    return formattedDate
}

fun getTimeZone(): String {
    val tz = TimeZone.getDefault()
    System.out.println("----TimeZone   " + tz.getDisplayName(false, TimeZone.SHORT).toString() + " Time zone id :: " + tz.id)
    return tz.id
}

//method to convert the selected image to base64 encoded string
fun convertBitmapToString(bitmap: Bitmap): String? {
    var encodedImage = ""
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
    try {
        encodedImage = URLEncoder.encode(Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT), "UTF-8")
    } catch (e: UnsupportedEncodingException) {
        e.printStackTrace()
    }
    return encodedImage
}

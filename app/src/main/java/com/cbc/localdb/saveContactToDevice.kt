package com.cbc.localdb

import android.content.Context
import android.os.AsyncTask
import android.provider.ContactsContract
import com.cbc.networkutils.Klog
import com.cbc.utils.AppSharePref
import com.cbc.views.ui.home.OnFetchDeviceContactListener
import com.google.i18n.phonenumbers.PhoneNumberUtil
import java.util.*

const val appdbname = "cbc"

class SaveDeviceData(val mContext: Context, val listener: OnFetchDeviceContactListener) :
    AsyncTask<Void, Void, Void>() {
    val phones = mContext.contentResolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        null,
        null,
        null,
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
    )

    val cbcDatabase = CBCDatabase.getDatabase(mContext)
    val userdetails = AppSharePref(mContext).GetUSERDATA()
    var countryCode = userdetails!!.countryCode
    override fun doInBackground(vararg p0: Void): Void? {
        var deviceList = ArrayList<ContactEntity>()
        while (phones!!.moveToNext()) {

            val name =
                phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val phoneNumber =
                phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

            /*   val photothumburi =
                   phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI))
               val photouri =
                   phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI))
               */

            var finalphoneno = phoneNumber.replace("-", "")
            if (finalphoneno.startsWith("+")) {
                val phoneUtil: PhoneNumberUtil = PhoneNumberUtil.getInstance()
//                val numberproto=phoneUtil.parse(finalphoneno,"")
//                countryCode="+${numberproto.countryCode.toString()}"

            } else {
                countryCode = userdetails!!.countryCode
                finalphoneno = "$countryCode${finalphoneno.trim()}"
            }
            finalphoneno = finalphoneno.replace(" ", "")
            //Klog.d("## ${finalphoneno.replace(" ", "")} NO-", "$phoneNumber name- $name")
            val contact = ContactEntity("","", finalphoneno.trim(), phoneNumber, name, "", "", false, "")
            deviceList.add(contact)

        }

        cbcDatabase.contctDao().insertAllDeviceContact(deviceList)
        return null
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        listener.onFetchComplete()
    }
}
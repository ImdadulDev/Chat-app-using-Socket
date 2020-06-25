package com.cbc.Localdb

import android.content.Context
import android.os.AsyncTask
import android.provider.ContactsContract
import androidx.room.Room

const val appdbname = "cbc"

class saveDevicedata(val mContext: Context) : AsyncTask<Void, Void, Void>() {
    val phones = mContext.contentResolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        null,
        null,
        null,
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
    )

    val cbcDatabase = CBCDatabase.getDatabase(mContext)
    override fun doInBackground(vararg p0: Void): Void? {
        while (phones!!.moveToNext()) {
            val name =
                phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val phoneNumber =
                phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            val photothumburi =
                phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI))
            val photouri =
                phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI))
            val contact = ContactEntity(phoneNumber, name, "", "", false, "")
            cbcDatabase.contctDao().insert(contact)
        }

        return null
    }


}
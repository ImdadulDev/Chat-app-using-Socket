package com.cbc.contactsyncutils

import android.content.ContentProviderOperation
import android.content.Context
import android.content.OperationApplicationException
import android.os.AsyncTask
import android.os.RemoteException
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.CommonDataKinds.StructuredName
import android.provider.ContactsContract.Data
import android.provider.ContactsContract.RawContacts
import com.cbc.contactsyncutils.model.ContactTemp
import com.cbc.interfaces.onSaveListener
import com.cbc.localdb.CBCDatabase


class savedataInToDevice(val mContext: Context, val onSaveListener: onSaveListener) :
    AsyncTask<Void, Void, Boolean>() {
    val cbcDatabase = CBCDatabase.getDatabase(mContext)
    val phones = mContext.contentResolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        null,
        null,
        null,
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
    )

    override fun doInBackground(vararg p0: Void?): Boolean {

        var deviceList = java.util.ArrayList<ContactTemp>()
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

            val contact = ContactTemp(name, phoneNumber)
            deviceList.add(contact)

        }

        var serverdata = cbcDatabase.contctDao().getAllList() as ArrayList
        deviceList.forEach {
            serverdata.remove(it)
        }

        serverdata.forEach {
            writeContact(it.displayname, it.number)
        }

        return true
    }

    override fun onPostExecute(result: Boolean?) {
        super.onPostExecute(result)
        onSaveListener.SaveComplete()
    }

    private fun writeContact(displayName: String, number: String) {
        val contentProviderOperations = ArrayList<ContentProviderOperation>()
        //insert raw contact using RawContacts.CONTENT_URI
        contentProviderOperations.add(
            ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
                .withValue(RawContacts.ACCOUNT_TYPE, null).withValue(
                    RawContacts.ACCOUNT_NAME,
                    null
                ).build()
        )
        //insert contact display name using Data.CONTENT_URI
        contentProviderOperations.add(
            ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0).withValue(
                    ContactsContract.Data.MIMETYPE,
                    StructuredName.CONTENT_ITEM_TYPE
                )
                .withValue(StructuredName.DISPLAY_NAME, displayName).build()
        )
        //insert mobile number using Data.CONTENT_URI
        contentProviderOperations.add(
            ContentProviderOperation.newInsert(Data.CONTENT_URI)
                .withValueBackReference(
                    ContactsContract.Data.RAW_CONTACT_ID,
                    0
                ).withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                .withValue(Phone.NUMBER, number).withValue(Phone.TYPE, Phone.TYPE_MOBILE).build()
        )
        try {
            mContext.getApplicationContext()
                .getContentResolver()
                .applyBatch(ContactsContract.AUTHORITY, contentProviderOperations)
        } catch (e: RemoteException) {
            e.printStackTrace()
        } catch (e: OperationApplicationException) {
            e.printStackTrace()
        }
    }
}
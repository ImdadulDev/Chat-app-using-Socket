package com.cbc.contactsyncutils

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.provider.ContactsContract
import com.cbc.localdb.CBCDatabase
import com.cbc.localdb.ContactEntity
import com.cbc.networkutils.Klog
import com.cbc.models.contactResponse.GetContactResponse
import com.cbc.networkutils.URLs
import com.cbc.utils.AppSharePref
import com.google.gson.Gson
import com.google.i18n.phonenumbers.PhoneNumberUtil
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

class ContactChangeObserver(val mContext: Context, handler: Handler?) : ContentObserver(handler) {
    val cbcDatabase = CBCDatabase.getDatabase(mContext)
    val userdetails = AppSharePref(mContext).GetUSERDATA()
    val header = AppSharePref(mContext).GetAuthHeader()
    var countryCode = userdetails!!.countryCode

    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
        Klog.d("## RECIVER-", " 33ADED-$selfChange")
    }

    override fun onChange(selfChange: Boolean, uri: Uri?) {
        super.onChange(selfChange, uri)
        val phones = mContext.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )


        val deviceList = ArrayList<ContactEntity>()
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
            //Klog.d("##-> ${finalphoneno.replace(" ", "")} NO-", "$phoneNumber name- $name")
            val contact = ContactEntity("","",finalphoneno.trim(), phoneNumber, name, "", "", false, "")
            deviceList.add(contact)

        }
        cbcDatabase.contctDao().insertAllDeviceContact(deviceList)
        deviceList.clear()

        /**************** UPDATE DEVICE ****************/

        val localList = cbcDatabase.contctDao().getDeviceData(false)
        localList.forEach {
            it.is_cbc_backedup = true
        }

        val clist = JSONArray(Gson().toJson(localList))
        val params = JSONObject()
        params.put("contact", clist)
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()


        val MEDIA_TYPE = "application/json".toMediaTypeOrNull()
//        final MediaType MEDIA_TYPE_PNG = MediaType.parse("*/*");

        val bodybuilder = MultipartBody.Builder()
        bodybuilder.setType(MultipartBody.FORM)


        val body = RequestBody.create(MEDIA_TYPE, params.toString())

        val builder = Headers.Builder()
        if (header != null)
            for (entry in header.entries) {
                println(entry.key + "/" + entry.value)
                builder.add(entry.key, entry.value.toString())
            }
        val h = builder.build()

        val request = Request.Builder()
            .url(URLs.CONTACTUPLOAD)
            .post(body)
            .addHeader("content-type", "application/json")
            .headers(h)
            .build()


        try {
            val response = client.newCall(request).execute()
            val stringResp = response.body!!.string()
            if (JSONObject(stringResp).optInt("response_code") == 2000) {
                val respdata = Gson().fromJson<GetContactResponse>(
                    stringResp,
                    GetContactResponse::class.java
                )
                cbcDatabase.contctDao().UpdateAll(respdata.responseData)
            }

        } catch (e: IOException) {
            e.printStackTrace()

        }

        Klog.d("## RECIVER-", " ADED-$uri")
    }

    override fun deliverSelfNotifications(): Boolean {
        return super.deliverSelfNotifications()
    }
}
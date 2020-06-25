package com.cbc.localdb

import androidx.lifecycle.LiveData
import androidx.room.*
import com.cbc.contactsyncutils.model.ContactTemp

@Dao
interface ContactDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAllDeviceContact(contacts: List<ContactEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllServerContact(contacts: List<ContactEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertDeviceContact(contact: ContactEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertServerContact(contact: ContactEntity)

    @Delete
    fun delete(contact: ContactEntity)

    @Query("SELECT * FROM ContactEntity ORDER BY display_name ASC")
    fun getAll(): LiveData<List<ContactEntity>>

    @Query("SELECT display_name,display_mobile_no FROM ContactEntity ORDER BY display_name ASC")
    fun getAllList(): List<ContactTemp>

    @Query("SELECT * FROM ContactEntity WHERE is_cbc_backedup= :backedup")
    fun getDeviceData(backedup: Boolean = false): List<ContactEntity>

    @Query("UPDATE ContactEntity SET is_cbc_backedup=:backedup WHERE is_cbc_backedup=:Notbackedup")
    fun UpdateAlltoUploaded(backedup: Boolean = true, Notbackedup: Boolean = false)

    @Query("DELETE FROM ContactEntity")
    fun deleteall()

    @Query("DELETE FROM ContactEntity WHERE is_cbc_backedup=:backedup")
    fun deleteAllBackedUp(backedup: Boolean = true)

    @Update
    fun UpdateOne(contact: ContactEntity)

    @Update
    fun UpdateAll(contacts: List<ContactEntity>)

    @Query("SELECT * FROM ContactEntity WHERE cbc_id IS NOT NULL")
    fun getAllContactWhereCbcIdPresent(): List<ContactEntity>

}
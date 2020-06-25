package com.cbc.Localdb

import androidx.room.*

@Dao
interface ContactDao {
    @Insert
    fun insertAll(vararg contact: ContactEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(contact: ContactEntity)

    @Delete
    fun delete(contact: ContactEntity)

    @Query("SELECT * FROM ContactEntity")
    fun getAll(): List<ContactEntity>


}
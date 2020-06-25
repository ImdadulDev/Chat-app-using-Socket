package com.cbc.localdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserIdDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userIdDao: UserIdEntity)

    @Query("SELECT * FROM useridentity")
    fun getDeviceRegisterUserID(): UserIdEntity?

    @Query("DELETE FROM useridentity")
    fun deleteallregisteruser()

}
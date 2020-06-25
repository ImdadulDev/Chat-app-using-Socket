package com.cbc.Localdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(ContactEntity::class), version = 1)
abstract class CBCDatabase : RoomDatabase() {
    abstract fun contctDao(): ContactDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: CBCDatabase? = null

        fun getDatabase(context: Context): CBCDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CBCDatabase::class.java,
                    "cbc.db"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
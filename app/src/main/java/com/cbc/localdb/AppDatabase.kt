package com.cbc.localdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cbc.localdb.daos.ChatHistoryDao
import com.cbc.localdb.daos.GroupChatHistoryDao
import com.cbc.localdb.entities.ChatHistoryEntity
import com.cbc.localdb.entities.GroupChatEntity
import com.cbc.localdb.entities.GroupMemberEntity

@Database(
    entities = [ChatHistoryEntity::class, GroupMemberEntity::class, GroupChatEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getChatHistoryDao(): ChatHistoryDao
    abstract fun getGroupChatHistoryDao(): GroupChatHistoryDao

    companion object {

        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "ChatDatabase.db"
            ).build()
    }
}
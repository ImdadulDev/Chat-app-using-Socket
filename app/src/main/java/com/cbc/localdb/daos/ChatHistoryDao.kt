package com.cbc.localdb.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cbc.localdb.entities.ChatHistoryEntity
import com.cbc.localdb.entities.GroupMemberEntity

@Dao
interface ChatHistoryDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertChatHistory(chatHistoryEntity: ChatHistoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMsgInChatHistory(chatHistoryEntity: List<ChatHistoryEntity>)

    @Query("SELECT * FROM ChatHistoryEntity")
    fun getAllChatHistory(): List<ChatHistoryEntity>

    //WHERE first_name LIKE :first AND " + "last_name LIKE :last
    //@Query("SELECT * FROM ChatHistoryEntity WHERE (id = :userId AND remoteUserId = :receiveMsgRemoteId)")
    @Query("SELECT * FROM ChatHistoryEntity WHERE (userId = :userId AND remoteUserId = :receiveMsgRemoteId) ORDER BY chatDate")
    fun getChatHistory(userId: String, receiveMsgRemoteId:String) : List<ChatHistoryEntity>

    @Query("DELETE FROM ChatHistoryEntity")
    fun deleteAllChatHistoryFromTable()

}
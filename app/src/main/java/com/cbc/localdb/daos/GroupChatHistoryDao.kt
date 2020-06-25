package com.cbc.localdb.daos

import androidx.room.*
import com.cbc.localdb.entities.GroupChatEntity
import com.cbc.localdb.entities.GroupMemberEntity

@Dao
interface GroupChatHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMsgOfGroupChatHistory(goupChatHistoryEntity: List<GroupChatEntity>)

    @Query("SELECT * FROM GroupChatEntity")
    fun getAllGroupChatHistory(): List<GroupChatEntity>

    //@Query("SELECT * FROM ChatHistoryEntity WHERE (id = :userId AND remoteUserId = :receiveMsgRemoteId)")
    @Query("SELECT * FROM GroupChatEntity WHERE (userId = :userId AND `group` = :groupId)")
    fun getGroupChatHistory(userId: String, groupId:String) : List<GroupChatEntity>

  /*  //@Query("SELECT * FROM ChatHistoryEntity WHERE (id = :userId AND remoteUserId = :receiveMsgRemoteId)")
    @Query("SELECT * FROM GroupChatEntity WHERE userId = :userId")
    fun getRemoteUserNameFromDb(userId: String) : String


*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupMember(groupMemberEntity: GroupMemberEntity)

    @Query("SELECT * FROM GroupMemberEntity")
    fun getAllGroupMembers(): List<GroupMemberEntity>

    @Query("SELECT * FROM GroupMemberEntity WHERE groupId = :groupId")
    fun getGroupMembersByGroupId(groupId:String) : List<GroupMemberEntity>


    @Query("DELETE FROM GroupMemberEntity")
    fun deleteAllGroupMemberFromTable()

    @Delete
    fun deleteUserDataFromDb(groupMemberEntity: GroupMemberEntity)
}
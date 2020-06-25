package com.cbc.data.repositories

import com.cbc.data.Apis
import com.cbc.data.SafeApiRequest
import com.cbc.data.apiresponse.*
import com.cbc.localdb.AppDatabase
import com.cbc.localdb.entities.GroupChatEntity
import com.cbc.localdb.entities.GroupMemberEntity
import com.cbc.utils.Coroutines
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GroupChatRepository(
    private val api: Apis,
    private val db: AppDatabase
) : SafeApiRequest() {

    suspend fun getCreateGroupChatResponseFromApi(
        groupUsers: String,
        userId: String,
        token: String,
        groupName: String,
        image: String,
        groupImageName: String
    ): CreateGroupResponse {
        return withContext(Dispatchers.IO) {
            apiRequest {
                api.createGroupApi(
                    groupUsers,
                    token,
                    groupName,
                    userId,
                    image,
                    groupImageName
                )
            }
        }
    }

    suspend fun getGroupChatHistoryResponseFromApi(
        groupId: String,
        userId: String,
        userChatToken: String,
        chatStartIndex: String
    ): GroupChatHistoryResponse {
        return withContext(Dispatchers.IO) {
            apiRequest {
                api.getPreviousGroupChatsFromApi(
                    userChatToken,
                    userId,
                    groupId,
                    chatStartIndex
                )
            }
        }
    }

    fun saveGroupChatHistoryEntity(groupChatHistoryEntityList: List<GroupChatEntity>) {
        Coroutines.io {
            db.getGroupChatHistoryDao().insertMsgOfGroupChatHistory(groupChatHistoryEntityList)
        }
    }


    suspend fun getRemoteUsersChatsFromDb(userId: String, groupId: String): List<GroupChatEntity>{
        return withContext(Dispatchers.IO){
            db.getGroupChatHistoryDao().getGroupChatHistory(userId, groupId)
        }
    }

 /*   suspend fun getRemoteUserNameFromDb(userId: String): String {
        return withContext(Dispatchers.IO){
            db.getGroupChatHistoryDao().getRemoteUserNameFromDb(userId)
        }
    }
*/

    fun saveGroupMemberEntity(groupMemberEntity: GroupMemberEntity) {
        Coroutines.io {
            db.getGroupChatHistoryDao().insertGroupMember(groupMemberEntity)
        }
    }

    suspend fun getGroupMembersFromDb(): List<GroupMemberEntity> {
        return withContext(Dispatchers.IO) {
            db.getGroupChatHistoryDao().getAllGroupMembers()
        }
    }

    suspend fun getGroupMembersByGroupIdFromDb(groupId:String): List<GroupMemberEntity> {
        return withContext(Dispatchers.IO) {
            db.getGroupChatHistoryDao().getGroupMembersByGroupId(groupId)
        }
    }

    fun deleteAllGroupMemberFromTable() {
        Coroutines.io {
            db.getGroupChatHistoryDao().deleteAllGroupMemberFromTable()
        }
    }

    fun deleteGroupMemberFromDb(groupMemberEntity: GroupMemberEntity) {
        Coroutines.io {
            db.getGroupChatHistoryDao().deleteUserDataFromDb(groupMemberEntity)
        }
    }

    suspend fun getEditGroupChatResponseFromApi(
        groupUsers: String,
        userId: String,
        token: String,
        groupName: String,
        groupId: String,
        image: String,
        groupImageName: String
    ): EditGroupResponse {
        return withContext(Dispatchers.IO) {
            apiRequest {
                api.editGroupApi(
                    groupUsers,
                    token,
                    groupName,
                    groupId,
                    userId,
                    image,
                    groupImageName
                )
            }
        }
    }

    suspend fun getEditGroupImageResponseFromApi(
        userId: String,
        token: String,
        groupId: String,
        image: String,
        groupImageName: String
    ): EditGroupImageResponse {
        return withContext(Dispatchers.IO) {
            apiRequest {
                api.editGroupImageApi(
                    token,
                    groupId,
                    userId,
                    image,
                    groupImageName
                )
            }
        }
    }

    suspend fun getAddGroupUserResponseFromApi(
        groupUsers: String,
        userId: String,
        token: String,
        groupName: String,
        groupId: String,
        image: String,
        groupImageName: String
    ): AddGroupUserResponse {
        return withContext(Dispatchers.IO) {
            apiRequest {
                api.addGroupUserApi(
                    groupUsers,
                    token,
                    groupName,
                    groupId,
                    userId,
                    image,
                    groupImageName
                )
            }
        }
    }

    suspend fun getLeaveGroupUserResponseFromApi(
        userId: String,
        token: String,
        groupId: String
    ): LeaveGroupResponse {
        return withContext(Dispatchers.IO) {
            apiRequest {
                api.leaveGroupApi(
                    token,
                    userId,
                    groupId
                )
            }
        }
    }

    suspend fun getRemoveGroupUserResponseFromApi(
        userId: String,
        token: String,
        groupId: String,
        groupImageName: String,
        delUser: String
    ): RemoveUserResponse {
        return withContext(Dispatchers.IO) {
            apiRequest {
                api.removeGroupUserApi(
                    token,
                    userId,
                    groupId,
                    groupImageName,
                    delUser
                )
            }
        }
    }

}
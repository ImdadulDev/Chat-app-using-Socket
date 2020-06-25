package com.cbc.data.repositories

import android.util.Log
import com.cbc.data.Apis
import com.cbc.data.apiresponse.ChatsHistoryResponse
import com.cbc.data.apiresponse.FileUploadResponse
import com.cbc.data.SafeApiRequest
import com.cbc.data.apiresponse.MyMessageResponse
import com.cbc.localdb.AppDatabase
import com.cbc.localdb.entities.ChatHistoryEntity
import com.cbc.localdb.entities.GroupMemberEntity
import com.cbc.utils.Coroutines
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ChatRepository (
    private val api: Apis,
    private val db: AppDatabase
) : SafeApiRequest() {

 /*   private val chatHistoryEntity = MutableLiveData<ChatHistoryEntity>()

    init {
        chatHistoryEntity.observeForever {
            saveChatHistoryEntity(it)
        }
    }*/

    suspend fun getPreviousChatsApi(
        remoteUserId: String,
        userId: String,
        userChatToken: String,
        chatStartIndex: String
    ): ChatsHistoryResponse {
        return withContext(Dispatchers.IO) {
            apiRequest { api.getPreviousChatsFromApi(
                userChatToken,
                remoteUserId,
                userId,
                "NONSECRET",
                chatStartIndex)
            }
        }
    }


    suspend fun getChatHistoryResponse(
        remoteUserId: String,
        userId: String,
        userChatToken: String,
        chatStartIndex: String): ChatsHistoryResponse {
        return withContext(Dispatchers.IO) {
            apiRequest { api.getPreviousChatsFromApi(
                userChatToken,
                remoteUserId,
                userId,
                "NONSECRET",
                chatStartIndex)
            }
        }
    }

    suspend fun getMyMessageListResponse(
        userChatToken: String, userId: String): MyMessageResponse {
        return withContext(Dispatchers.IO) {
            apiRequest { api.getMyMessageFromApi(
                userChatToken, userId)
            }
        }
    }

    /* private suspend fun fetchChatHistory(
        remoteUserId: String,
        userId: String,
        userChatToken: String,
        chatStartIndex: String) {
        try {
            val previousChatsResponse = apiRequest { api.getPreviousChatsFromApi(
                userChatToken,
                remoteUserId,
                userId,
                "NONSECRET",
                "0") }
            chatHistoryEntity.postValue(previousChatsResponse.chats)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    } */

    fun saveChatHistoryEntity(chatHistoryEntityList: ChatHistoryEntity) {
        Coroutines.io {
            db.getChatHistoryDao().insertChatHistory(chatHistoryEntityList)
        }
    }


    fun deleteChatHistoryFromDb() {
        Coroutines.io {
            db.getChatHistoryDao().deleteAllChatHistoryFromTable()
        }
    }

    fun saveReceiveInChatHistoryEntity(chatHistoryEntity: List<ChatHistoryEntity>) {
        Coroutines.io {
            db.getChatHistoryDao().insertMsgInChatHistory(chatHistoryEntity)
        }
    }

    suspend fun getRemoteUsersChatsFromDb(userId: String,receiveMsgRemoteId: String): List<ChatHistoryEntity>{
       return withContext(Dispatchers.IO){
           db.getChatHistoryDao().getChatHistory(userId, receiveMsgRemoteId)
       }
    }


    fun getChatsFromDb(): ArrayList<ChatHistoryEntity>{
        val chatList : ArrayList<ChatHistoryEntity> = ArrayList()
        Coroutines.io {
            Log.d("----","all chats size: " + db.getChatHistoryDao().getAllChatHistory().size)
            chatList.addAll(db.getChatHistoryDao().getAllChatHistory())
        }

        return chatList
    }


    suspend fun uploadFileToServer(
        token: RequestBody,
        type: RequestBody,
        userId: RequestBody,
        attachment: MultipartBody.Part,
        attachmentName: RequestBody
    ): FileUploadResponse {
        Log.d("----1", ": $token")
        Log.d("----2", ": $type")
        Log.d("----3", ": $userId")
        Log.d("----4", ": $attachment")

        return withContext(Dispatchers.IO) {
            apiRequest { api.uploadFileToServer(
                attachment,
                token,
                type,
                userId,
                attachmentName)
            }
        }
    }



}
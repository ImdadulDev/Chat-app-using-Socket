package com.cbc.data

import com.cbc.data.apiresponse.*
import com.cbc.utils.Constants
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface Apis {

    @Headers(
        "Content-Type: application/x-www-form-urlencoded"
    )
    @POST("chatHistory")
    @FormUrlEncoded
    suspend fun getPreviousChatsFromApi(
        @Field("token") token: String,
        @Field("remoteUserId") remoteUserId: String,
        @Field("userId") userId: String,
        @Field("chatMode") chatMode: String,
        @Field("chatStartIndex") chatStartIndex: String
    ): Response<ChatsHistoryResponse>


    @Headers(
        "Content-Type: application/x-www-form-urlencoded"
    )
    @POST("chatList")
    @FormUrlEncoded
    suspend fun getMyMessageFromApi(
        @Field("token") token: String,
        @Field("userId") userId: String
    ): Response<MyMessageResponse>


    @POST("fileUplaod")
    @Multipart
    suspend fun uploadFileToServer(
        @Part attachment: MultipartBody.Part,
        @Part("token") token: RequestBody,
        @Part("type") type: RequestBody,
        @Part("userId") userId: RequestBody,
        @Part("attachmentName") attachmentName: RequestBody
    ): Response<FileUploadResponse>

    fun uploadImage(
        @Part file: MultipartBody.Part?, @Part("filename") name: RequestBody?
    ): Call<String?>?


    @Headers(
        //"Content-Type: application/from-data"
        "Content-Type: application/x-www-form-urlencoded"
    )
    @POST("createGroup")
    @FormUrlEncoded
    suspend fun createGroupApi(
        @Field("groupUsers") groupUsers: String,
        @Field("token") token: String,
        @Field("groupName") groupName: String,
        @Field("userId") userId: String,
        @Field("image") image: String,
        @Field("groupImageName") groupImageName: String
    ): Response<CreateGroupResponse>

    @POST("editGroup")
    @FormUrlEncoded
    suspend fun editGroupApi(
        @Field("groupUsers") groupUsers: String,
        @Field("token") token: String,
        @Field("groupName") groupName: String,
        @Field("groupId") groupId: String,
        @Field("userId") userId: String,
        @Field("image") image: String,
        @Field("groupImageName") groupImageName: String
    ): Response<EditGroupResponse>


    @POST("editGroupImage")
    @FormUrlEncoded
    suspend fun editGroupImageApi(
        @Field("token") token: String,
        @Field("groupId") groupId: String,
        @Field("userId") userId: String,
        @Field("image") image: String,
        @Field("groupImageName") groupImageName: String
    ): Response<EditGroupImageResponse>


    @POST("addGroupUser")
    @FormUrlEncoded
    suspend fun addGroupUserApi(
        @Field("groupUsers") groupUsers: String,
        @Field("token") token: String,
        @Field("groupName") groupName: String,
        @Field("groupId") groupId: String,
        @Field("userId") userId: String,
        @Field("image") image: String,
        @Field("groupImageName") groupImageName: String
    ): Response<AddGroupUserResponse>



    @POST("leaveGroup")
    @FormUrlEncoded
    suspend fun leaveGroupApi(
        @Field("token") token: String,
        @Field("userId") userId: String,
        @Field("groupId") groupId: String
    ): Response<LeaveGroupResponse>



    @Headers(
        "Content-Type: application/x-www-form-urlencoded"
    )
    @POST("groupChatHistory")
    @FormUrlEncoded
    suspend fun getPreviousGroupChatsFromApi(
        @Field("token") token: String,
        @Field("userId") userId: String,
        @Field("groupId") chatMode: String,
        @Field("chatStartIndex") chatStartIndex: String
    ): Response<GroupChatHistoryResponse>


    @POST("leaveGroupUser")
    @FormUrlEncoded
    suspend fun removeGroupUserApi(
        @Field("token") token: String,
        @Field("userId") userId: String,
        @Field("groupId") groupId: String,
        @Field("groupImageName") groupImageName: String,
        @Field("deluser") deluser: String
    ): Response<RemoveUserResponse>


    companion object {
        operator fun invoke(
            networkConnectionInterceptor: NetworkConnectionInterceptor
        ): Apis {

            val okkHttpclient = OkHttpClient.Builder()
                .addInterceptor(networkConnectionInterceptor)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .client(okkHttpclient)
                .baseUrl(Constants.CHAT_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Apis::class.java)
        }
    }
}
/*
object ApiUtils {
    val apiService: Apis
        get() = RetrofitClient.getClient(ApiConstants.BASE_URL)!!.create(Apis::class.java)

}*/

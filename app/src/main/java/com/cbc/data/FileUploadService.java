package com.cbc.data;

import com.cbc.data.apiresponse.FileUploadResponse;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface FileUploadService {

    @POST("fileUpload")
    @Multipart
    Call<FileUploadResponse> uploadFile(
            @Part("token")RequestBody token ,
            @Part("type")RequestBody type ,
            @Part("userId")RequestBody userId ,
            @Part("attachmentName")RequestBody attachmentName,
            @Part MultipartBody.Part attachment);

    @Multipart
    @POST("fileUpload")
    Call<ResponseBody> uploadFileWithPartMap(
            @PartMap() Map<String, RequestBody> partMap,
            @Part MultipartBody.Part file);


}

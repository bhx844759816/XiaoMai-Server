package com.guangzhida.xiaomai.server.data.common

import com.guangzhida.xiaomai.server.model.UploadMessageModel
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface CommonService {

    /**
     * 上传图片
     */
    @Multipart
    @POST("common/upload_pic")
    suspend fun uploadImg(@Part photo: MultipartBody.Part): UploadMessageModel
}
package com.guangzhida.xiaomai.server.data.common

import com.guangzhida.xiaomai.server.base.BaseResult
import com.guangzhida.xiaomai.server.http.RetrofitManager
import com.guangzhida.xiaomai.server.model.UploadMessageModel
import com.guangzhida.xiaomai.server.model.UserModel
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import java.io.File

class CommonRepository {
    private val mService = RetrofitManager.getInstance().create(CommonService::class.java)


    /**
     * 上传图片
     */
    suspend fun uploadImg(file: File): UploadMessageModel {
        val body = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val part = MultipartBody.Part.createFormData("file", file.name, body)
        return mService.uploadImg(part)
    }




    companion object {
        @Volatile
        private var INSTANCE: CommonRepository? = null

        fun getInstance() =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: CommonRepository().also { INSTANCE = it }
            }
    }
}
package com.guangzhida.xiaomai.server.data.user

import com.guangzhida.xiaomai.server.base.BaseResult
import com.guangzhida.xiaomai.server.model.UserModel
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface UserService {


    /**
     * 控制客服上线下线
     */
    @FormUrlEncoded
    @POST("network/customer_service/upAndDownLine")
    suspend fun operateServerOnLineStatus(@Field("id") id: String): BaseResult<UserModel>


    /**
     * 获取客服的信息
     */
    @FormUrlEncoded
    @POST("network/customer_service/getCustomerServiceById")
    suspend fun getUserInfo(@Field("id") id: String): BaseResult<UserModel>

    /**
     * 修改客服
     */
    @FormUrlEncoded
    @POST("network/customer_service/modifyCustomerService")
    suspend fun updateUserInfo(
        @FieldMap map: Map<String, String>
    ): BaseResult<UserModel>
}
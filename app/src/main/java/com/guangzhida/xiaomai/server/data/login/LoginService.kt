package com.guangzhida.xiaomai.server.data.login

import com.guangzhida.xiaomai.server.base.BaseResult
import com.guangzhida.xiaomai.server.model.UserModel
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * 用户登录注册等接口
 */
interface LoginService {

    /**
     * 18539572651   123456
     * 用户登录
     */
    @FormUrlEncoded
    @POST("network/customer_service/customerLogin")
    suspend fun doLogin(
        @Field("mobilePhone") mobilePhone: String,
        @Field("password") passWord: String
    ): BaseResult<UserModel>


    /**
     * 18539572651   123456
     * 用户登录
     */
    @FormUrlEncoded
    @POST("network/customer_service/regiest_user")
    suspend fun doRegister(
        @Field("mobilePhone") mobilePhone: String, @Field("password") passWord: String
        , @Field("code") code: String, @Field("schoolId") schoolId: String
    ): BaseResult<String>

    /**
     * 发送验证码
     * @param type 1校麦APP用户注册  2校麦APP找回密码  3校麦网管助手的注册
     */
    @FormUrlEncoded
    @POST("common/get_regiest_code")
    suspend fun sendSmsCode(@Field("mobilePhone") phone: String, @Field("type") type: String): BaseResult<String>
}
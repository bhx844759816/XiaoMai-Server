package com.guangzhida.xiaomai.server.data.login

import com.guangzhida.xiaomai.server.base.BaseResult
import com.guangzhida.xiaomai.server.http.RetrofitManager
import com.guangzhida.xiaomai.server.model.UserModel

class LoginRepository {
    private val mService = RetrofitManager.getInstance().create(LoginService::class.java)

    /**
     * 执行登录
     */
    suspend fun doLogin(phone: String, password: String): BaseResult<UserModel> =
        mService.doLogin(phone, password)

    /**
     * 注册
     */
    suspend fun doRegister(
        phone: String,
        password: String,
        code: String,
        schoolId: String
    ): BaseResult<String> = mService.doRegister(phone, password, code, schoolId)

    /**
     * 发送验证码
     */
    suspend fun sendSmsCode(phone: String, type: String): BaseResult<String> =
        mService.sendSmsCode(phone, type)

    companion object {
        @Volatile
        private var INSTANCE: LoginRepository? = null

        fun getInstance() =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: LoginRepository().also { INSTANCE = it }
            }
    }


}
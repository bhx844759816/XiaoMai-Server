package com.guangzhida.xiaomai.server.data.user

import com.guangzhida.xiaomai.server.base.BaseResult
import com.guangzhida.xiaomai.server.http.RetrofitManager
import com.guangzhida.xiaomai.server.model.UserModel
import retrofit2.http.Field

class UserRepository {
    private val mService = RetrofitManager.getInstance().create(UserService::class.java)


    /**
     * 操作客服的上下线
     */
    suspend fun operateServerOnLineStatus(id: String): BaseResult<UserModel> {
        return mService.operateServerOnLineStatus(id)
    }

    /**
     * 获取用户对象
     */
    suspend fun getUserInfo(id: String): BaseResult<UserModel> {
        return mService.getUserInfo(id)
    }

    /**
     * 更新用户信息
     */
    suspend fun updateUserInfo(params: Map<String, String>): BaseResult<UserModel> {
        return mService.updateUserInfo(params)
    }

    companion object {
        @Volatile
        private var INSTANCE: UserRepository? = null

        fun getInstance() =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: UserRepository().also { INSTANCE = it }
            }
    }
}
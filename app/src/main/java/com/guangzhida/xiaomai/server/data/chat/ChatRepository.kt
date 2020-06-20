package com.guangzhida.xiaomai.server.data.chat

import com.guangzhida.xiaomai.server.base.BaseResult
import com.guangzhida.xiaomai.server.data.home.HomeService
import com.guangzhida.xiaomai.server.http.RetrofitManager
import com.guangzhida.xiaomai.server.model.ChatUserModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field

class ChatRepository {
    private val mService = RetrofitManager.getInstance().create(ChatService::class.java)

    /**
     * 获取用户信息
     */
    suspend fun getChatUserModel(userName: String): BaseResult<List<ChatUserModel>> {
        return mService.getChatUserModel(userName)
    }

    fun  getChatUserModelCall(userName: String):Call<BaseResult<List<ChatUserModel>>>{
        return mService.getChatUserModelCall(userName)
    }


    companion object {
        @Volatile
        var INSTANCE: ChatRepository? = null

        fun getInstance(): ChatRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: ChatRepository().also { INSTANCE = it }
            }
    }
}
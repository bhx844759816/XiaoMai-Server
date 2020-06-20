package com.guangzhida.xiaomai.server.task

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.guangzhida.xiaomai.server.data.InjectorUtils
import com.guangzhida.xiaomai.server.room.AppDataBase
import com.guangzhida.xiaomai.server.room.entity.ConversationEntity

class UpdateConversationTask(val context: Context, private val workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {
    // 会话的实例
    private val mConversationDao = AppDataBase.invoke(context).conversationDao()
    private val mChatRepository = InjectorUtils.getChatRepository()

    override fun doWork(): Result {
        try {
            val userName = workerParameters.inputData.getString("userName")
            userName?.let {
                val conversation = mConversationDao?.queryByUserName(it)
                if (conversation == null) {
                    val _conversation = getConversationEntityByUserTable(it)
                    mConversationDao?.insert(_conversation)
                }
            }

        } catch (t: Throwable) {
            t.printStackTrace()
        }
        return Result.success()
    }

    /**
     * 获取用户信息从用户表
     */
    private fun getConversationEntityByUserTable(userName: String): ConversationEntity? {
        val call = mChatRepository.getChatUserModelCall(userName)
        val response = call.execute()
        if (response.isSuccessful && response.body() != null) {
            val result = response.body()!!
            if (result.status == 200 && result.data != null && result.data.isNotEmpty()) {
                val conversationEntity = ConversationEntity()
                //获取的用户对象
                val userModel = result.data[0]
                conversationEntity.avatarUrl = userModel.headUrl ?: ""
                conversationEntity.nickName = userModel.nickName
                conversationEntity.userName = userModel.mobilePhone
                return conversationEntity
            }

        }
        return null
    }
}
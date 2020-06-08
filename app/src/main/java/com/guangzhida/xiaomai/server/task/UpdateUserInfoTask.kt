package com.guangzhida.xiaomai.server.task

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.guangzhida.xiaomai.server.data.InjectorUtils
import com.guangzhida.xiaomai.server.room.AppDataBase
import com.guangzhida.xiaomai.server.utils.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * 更新本地数据库的用户信息(昵称和头像)
 */
class UpdateUserInfoTask(context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {
    private val mConversationDao = AppDataBase.invoke(context).conversationDao()
    private val mRepository = InjectorUtils.getChatRepository()
    override fun doWork(): Result {
        LogUtils.i("开始执行更新用户信息的任务")
        val conversationList = mConversationDao?.queryAll()
        conversationList?.forEach { entity ->
            val userName = entity.userName
            try {
                val call = mRepository.getChatUserModelCall(userName)
                val response = call.execute()
                val result = response.body()
                if (result != null && result.status == 200) {
                    result.data?.get(0)?.let {
                        entity.avatarUrl = it.headUrl ?: ""
                        entity.nickName = it.nickName
                    }
                    mConversationDao?.update(entity)
                }
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
        LogUtils.i("结束执行更新用户信息的任务")
        return Result.success()
    }


}
package com.guangzhida.xiaomai.server.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import com.guangzhida.xiaomai.server.BaseApplication
import com.guangzhida.xiaomai.server.base.BaseViewModel
import com.guangzhida.xiaomai.server.model.SearchMessageModel
import com.guangzhida.xiaomai.server.room.AppDataBase
import com.guangzhida.xiaomai.server.utils.LogUtils
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMConversation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChatSearchViewModel : BaseViewModel() {
    val searchChatMessageMap = MutableLiveData<Map<String, SearchMessageModel>>()
    private val mConversationDao by lazy {
        AppDataBase.invoke(BaseApplication.instance().applicationContext).conversationDao()
    }

    /**
     * 检索 好友 和聊天记录
     */
    fun doSearch(key: String) {
        launchUI {
            //检索聊天记录
            val messageMap = withContext(Dispatchers.IO) {
                val messageMap = mutableMapOf<String, SearchMessageModel>()
                //对象(头像 昵称 聊天记录条数)
                val list = EMClient.getInstance().chatManager()
                    .searchMsgFromDB(
                        key,
                        0,
                        Int.MAX_VALUE,
                        "",
                        EMConversation.EMSearchDirection.DOWN
                    )
                LogUtils.i("doSearch list=$list")
                list.forEach {
                    val from = it.from
                    val to = it.to
                    val userName = if (from != BaseApplication.instance().mUserModel!!.username) {
                        from
                    } else {
                        to
                    }
                    if (messageMap.containsKey(userName)) {
                        val searchMessageModel = messageMap[userName]
                        if (searchMessageModel != null) {
                            val count = searchMessageModel.messageCount
                            searchMessageModel.messageCount = count + 1
                        }
                    } else {
                        val conversationEntity = mConversationDao?.queryByUserName(userName)
                        if (conversationEntity != null) {
                            val searchMessageModel = SearchMessageModel(conversationEntity, 1)
                            messageMap[userName] = searchMessageModel
                        }
                    }
                }
                messageMap
            }
            searchChatMessageMap.postValue(messageMap)
        }
    }
}
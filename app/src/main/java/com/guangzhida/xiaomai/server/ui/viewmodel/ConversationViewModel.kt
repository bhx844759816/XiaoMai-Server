package com.guangzhida.xiaomai.server.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import com.guangzhida.xiaomai.ktxlibrary.ext.sharedpreference.getSpValue
import com.guangzhida.xiaomai.server.BaseApplication
import com.guangzhida.xiaomai.server.DEFAULT_CUR_PAGE
import com.guangzhida.xiaomai.server.MESSAGE_SETTING_DAY
import com.guangzhida.xiaomai.server.MESSAGE_SETTING_SWITCH
import com.guangzhida.xiaomai.server.base.BaseViewModel
import com.guangzhida.xiaomai.server.data.InjectorUtils
import com.guangzhida.xiaomai.server.model.ChatUserModel
import com.guangzhida.xiaomai.server.model.ConversationModelWrap
import com.guangzhida.xiaomai.server.room.AppDataBase
import com.guangzhida.xiaomai.server.room.entity.ConversationEntity
import com.guangzhida.xiaomai.server.utils.LogUtils
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMConversation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 互动页面的ViewModel
 */
class ConversationViewModel : BaseViewModel() {
    private var curPage = 0
    private val pageSize = 10
    val mConversationListLiveData = MutableLiveData<MutableList<ConversationModelWrap>>()
    val mSwipeRefreshLiveData = MutableLiveData<Boolean>()
    val topConversationResult = MutableLiveData<Boolean>() //置顶和取消置顶的结果
    val deleteConversationResult = MutableLiveData<ConversationModelWrap>() //置顶和取消置顶的结果
    private val mRepository = InjectorUtils.getChatRepository()
    private val mConversationDao by lazy {
        AppDataBase.invoke(BaseApplication.instance().applicationContext).conversationDao()
    }
    //获取是否打开消息过滤设置
    private val mMessageSettingIsOpen: Boolean
        get() {
            val isOpen =
                BaseApplication.instance().applicationContext.getSpValue(MESSAGE_SETTING_SWITCH, 0)
            return isOpen == 1
        }
    //是否打开
    private val mMessageSettingDays: Int
        get() {
            return BaseApplication.instance().applicationContext.getSpValue(MESSAGE_SETTING_DAY, 7)
        }


    /**
     * 加载本地的会话列表
     */
    fun loadAllConversation() {
        launchUI {
            try {
                val modelWrapList = mutableListOf<ConversationModelWrap>()
                withContext(Dispatchers.IO) {
                    val _modelList = mutableListOf<ConversationModelWrap>()
                    val conversationList = mutableListOf<EMConversation>()
                    val maps =
                        EMClient.getInstance().chatManager().allConversations
                    maps.forEach {
                        conversationList.add(it.value)
                    }
                    // 过滤聊天 或者未读的聊天会话
                    val filterList = conversationList.filter {
                        val timCur = System.currentTimeMillis() - it.lastMessage.msgTime
                        if (mMessageSettingIsOpen) {
                            return@filter timCur <= mMessageSettingDays * 24 * 60 * 60 * 1000
                        }
                        return@filter true
                    }.toMutableList()
                    filterList.forEach {
                        val userName = it.conversationId()
                        val conversationEntity =
                            mConversationDao?.queryByUserName(userName)
                        if (conversationEntity != null) {
                            conversationEntity.lastMessageTime =
                                if (it.lastMessage != null) it.lastMessage.msgTime else 0
                            val conversationModelWrap = ConversationModelWrap(
                                emConversation = it,
                                conversationEntity = conversationEntity
                            )
                            _modelList.add(conversationModelWrap)
                        }
                    }
                    val topList = _modelList.filter {
                        it.conversationEntity?.isTop != true
                    }.toMutableList()
                    val listBySort = _modelList.filter {
                        it.conversationEntity?.isTop != true
                    }.sortedBy {
                        it.conversationEntity?.lastMessageTime
                    }.reversed().toMutableList()
                    modelWrapList.addAll(topList)
                    modelWrapList.addAll(listBySort)
                }
                mConversationListLiveData.postValue(modelWrapList)
            } catch (t: Throwable) {
                t.printStackTrace()
            } finally {
                mSwipeRefreshLiveData.postValue(true)
            }
        }
    }


//
//    /**
//     * 加载全部的会话列表 分页
//     */
//    fun loadAllConversation() {
//        //获取全部的会话
//        launchUI {
//            try {
//                val sortList = withContext(Dispatchers.IO) {
//                    val conversationList = mutableListOf<EMConversation>()
//                    val maps = EMClient.getInstance().chatManager().allConversations
//                    maps.forEach {
//                        conversationList.add(it.value)
//                    }
//                    // 过滤聊天 或者未读的聊天会话
//                    val filterList = conversationList.filter {
//                        val timCur = System.currentTimeMillis() - it.lastMessage.msgTime
//                        if (mMessageSettingIsOpen) {
//                            return@filter timCur <= mMessageSettingDays * 24 * 60 * 60 * 1000
//                        }
//                       return@filter true
//                    }.toMutableList()
//                    // 按照时间排序
//                    filterList.sortBy {
//                        it.lastMessage.msgTime
//                    }
//                    filterList.reversed()
//                }
//                updateConversationEntityList(sortList)
//                val list = sortConversationList(sortList)
//                //将置顶的提升到顶部
//                list.sortBy {
//                    it.conversationEntity?.isTop != true
//                }
//                mConversationListLiveData.postValue(list)
//            } catch (t: Throwable) {
//                t.printStackTrace()
//            } finally {
//                mSwipeRefreshLiveData.postValue(true)
//            }
//        }
//    }
//
//    /**
//     * 更新本地存储的会话列表的用户信息
//     */
//    private suspend fun updateConversationEntityList(conversations: List<EMConversation>) {
//        withContext(Dispatchers.IO) {
//            conversations.forEach { emConversation ->
//                val userName = emConversation.conversationId()
//                var conversationEntity = mConversationDao?.queryByUserName(userName)
//                if (conversationEntity == null) {
//                    conversationEntity = ConversationEntity(userName = userName)
//                    conversationEntity.lastMessageTime =
//                        if (emConversation.lastMessage != null) emConversation.lastMessage.msgTime else 0
//                    mConversationDao?.insert(conversationEntity)
//                } else {
//                    conversationEntity.lastMessageTime =
//                        if (emConversation.lastMessage != null) emConversation.lastMessage.msgTime else 0
//                    mConversationDao?.update(conversationEntity)
//                }
//            }
//        }
//    }
//
//
//    /**
//     * 处理会话的列表
//     */
//    private suspend fun sortConversationList(conversations: List<EMConversation>): MutableList<ConversationModelWrap> {
//        val conversationModelWrapList = mutableListOf<ConversationModelWrap>()
//        withContext(Dispatchers.IO) {
//            val conversationEntityList = mConversationDao?.queryAll()
//            conversations.forEach {
//                val entity = conversationEntityList?.find { entity ->
//                    entity.userName == it.conversationId()
//                }
//                conversationModelWrapList.add(ConversationModelWrap(it, entity))
//            }
//
//        }
//        return conversationModelWrapList
//    }


    fun makeConversationTop(item: ConversationModelWrap) {
        launchUI {
            try {
                withContext(Dispatchers.IO) {
                    if (item.conversationEntity != null) {
                        item.conversationEntity?.isTop = !(item.conversationEntity?.isTop ?: false)
                        mConversationDao?.update(item.conversationEntity)
                    }
                }
                topConversationResult.postValue(true)
            } catch (e: Throwable) {
                e.printStackTrace()
                topConversationResult.postValue(false)
            }
        }
    }


    /**
     * 删除会话
     */
    fun deleteConversation(wrap: ConversationModelWrap) {
        launchUI {
            try {
                withContext(Dispatchers.IO) {
                    val result =
                        EMClient.getInstance().chatManager()
                            .deleteConversation(wrap.emConversation?.conversationId(), true)
                    if (result) {
                        val conversationEntity =
                            mConversationDao?.queryByUserName(
                                wrap.emConversation?.conversationId() ?: ""
                            )
                        if (conversationEntity != null) {
                            mConversationDao?.delete(conversationEntity)
                        }
                        deleteConversationResult.postValue(wrap)
                    }
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                deleteConversationResult.postValue(null)
            }

        }

    }

}
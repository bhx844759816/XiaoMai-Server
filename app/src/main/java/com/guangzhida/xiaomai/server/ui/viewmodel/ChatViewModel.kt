package com.guangzhida.xiaomai.server.ui.viewmodel

import android.R.attr.action
import androidx.lifecycle.MutableLiveData
import com.guangzhida.xiaomai.server.BaseApplication
import com.guangzhida.xiaomai.server.base.BaseViewModel
import com.guangzhida.xiaomai.server.data.InjectorUtils
import com.guangzhida.xiaomai.server.utils.LogUtils
import com.guangzhida.xiaomai.server.utils.ToastUtils
import com.hyphenate.EMCallBack
import com.hyphenate.EMMessageListener
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMCmdMessageBody
import com.hyphenate.chat.EMConversation
import com.hyphenate.chat.EMMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File


/**
 * 聊天界面的ViewModel
 */
class ChatViewModel : BaseViewModel() {

    private val pageSize = 20
    private var mChatUserName: String? = null;
    private val mRepository = InjectorUtils.getChatRepository()
    var conversation: EMConversation? = null
    val mInitConversationLiveData = MutableLiveData<List<EMMessage>>()
    val receiveMessageLiveData = MutableLiveData<EMMessage>() //接收到消息
    val haveMoreDataLiveData = MutableLiveData<List<EMMessage>>() //是否有更多数据
    val refreshResultLiveData = MutableLiveData<Boolean>() //下拉刷新回调
    val sendMessageSuccessLiveData = MutableLiveData<EMMessage>() //发送消息成功


    private val onEMMessageListener = object : EMMessageListener {
        override fun onMessageRecalled(messages: MutableList<EMMessage>?) {
            //测回消息
        }

        override fun onMessageChanged(message: EMMessage?, change: Any?) {
            //消息改变
        }

        override fun onCmdMessageReceived(messages: MutableList<EMMessage>?) {
            //接收到透传消息
        }

        override fun onMessageReceived(messages: MutableList<EMMessage>?) {
            messages?.let {
                innerOnMessageReceived(it)
            }
        }

        override fun onMessageDelivered(messages: MutableList<EMMessage>?) {
        }

        override fun onMessageRead(messages: MutableList<EMMessage>?) {
            //消息已读
        }
    }


    /**
     * 初始化会话对象
     */
    fun init(userName: String) {
        mChatUserName = userName
        conversation = EMClient.getInstance().chatManager()
            .getConversation(userName, EMConversation.EMConversationType.Chat, true)
        conversation?.markAllMessagesAsRead()
    }


    /**
     * 发送文本消息
     * @param friendId 好友ID
     * @param content 消息文本内容
     */
    fun sendTextMessage(content: String, userName: String) {
        if (conversation == null) {
            return
        }
        launchUI {
            try {
                withContext(Dispatchers.IO) {
                    val txtBody = EMMessage.createTxtSendMessage(content, userName)
                    EMClient.getInstance().chatManager().sendMessage(txtBody)
                    sendMessageSuccessLiveData.postValue(txtBody)
                }
            } catch (e: Throwable) {
                defUI.toastEvent.postValue("发送消息失败")
            }
        }
    }

    /**
     * 拉取最新的消息
     */
    fun pullNewMessage() {
        launchUI {
            try {
                val listResult = withContext(Dispatchers.IO) {
                    val messages = conversation!!.loadMoreMsgFromDB(
                        if (conversation!!.allMessages.size == 0) "" else conversation!!.allMessages[0].msgId,
                        Int.MAX_VALUE
                    )
                    conversation?.markAllMessagesAsRead()
                    messages
                }
                if (listResult != null && listResult.isNotEmpty()) {
                    haveMoreDataLiveData.postValue(listResult)
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }

        }
    }

    /**
     * 发送语音
     */
    fun sendVoiceMessage(
        file: File,
        timeLen: Long,
        userName: String
    ) {
        if (conversation == null) {
            return
        }
        launchUI {
            try {
                withContext(Dispatchers.IO) {
                    val voiceBody =
                        EMMessage.createVoiceSendMessage(
                            file.absolutePath,
                            timeLen.toInt(),
                            userName
                        )
                    EMClient.getInstance().chatManager().sendMessage(voiceBody)
                    sendMessageSuccessLiveData.postValue(voiceBody)
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                defUI.toastEvent.postValue("发送消息失败")
            }

        }
    }

    /**
     * 发送图片消息
     */
    fun sendPicMessage(
        file: File, userName: String
    ) {
        if (conversation == null) {
            return
        }
        launchUI {
            try {
                withContext(Dispatchers.IO) {
                    val picBody =
                        EMMessage.createImageSendMessage(file.absolutePath, false, userName)
                    conversation?.appendMessage(picBody)
                    sendMessageSuccessLiveData.postValue(picBody)
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                defUI.toastEvent.postValue("发送消息失败")
            }
        }
    }


    /**
     * 加载消息
     */
    fun initLocalMessage() {
        launchUI {
            try {
                if (conversation != null) {
                    val listResult = withContext(Dispatchers.IO) {
                        val msgCount = conversation!!.allMessages?.size ?: 0
                        if (msgCount < conversation!!.allMsgCount && msgCount < pageSize) {
                            var msgId: String? = null
                            if (conversation!!.allMessages != null && conversation!!.allMessages.size > 0) {
                                msgId = conversation!!.allMessages[0].msgId
                            }
                            val list = conversation!!.loadMoreMsgFromDB(msgId, pageSize - msgCount)

                        }
                        conversation!!.allMessages
                    }
                    if (listResult != null) {
                        mInitConversationLiveData.postValue(listResult)
                    }
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 加载此消息id后面的全部消息
     */
    fun initLocalMessage(msgId: String, pageSize: Int) {
        launchUI {
            try {
                withContext(Dispatchers.IO) {
                    val list = conversation?.loadMoreMsgFromDB(msgId, pageSize)
                }
            } catch (e: Throwable) {

            }

        }
    }

    /**
     * 拉取更多数据
     */
    fun loadMoreMessage() {
        launchUI {
            try {
                if (conversation != null) {
                    val listResult = withContext(Dispatchers.IO) {
                        val messageList = conversation!!.allMessages
                        EMClient.getInstance().chatManager().fetchHistoryMessages(
                            mChatUserName, EMConversation.EMConversationType.Chat, pageSize,
                            if (messageList != null && messageList.size > 0) messageList[0].msgId else ""
                        )
                        val messages = conversation!!.loadMoreMsgFromDB(
                            if (conversation!!.allMessages.size == 0) "" else conversation!!.allMessages[0].msgId,
                            pageSize
                        )
                        messages
                    }
                    if (listResult != null && listResult.isNotEmpty()) {
                        haveMoreDataLiveData.postValue(listResult)
                    }
                    refreshResultLiveData.postValue(true)
                } else {
                    refreshResultLiveData.postValue(false)
                }
            } catch (e1: Exception) {
                refreshResultLiveData.postValue(false)
            }
        }
    }

    /**
     * 接收到好友发来的消息
     */
    fun innerOnMessageReceived(messages: List<EMMessage>) {
        for (message in messages) {
            var username: String? = null
            username =
                if (message.chatType == EMMessage.ChatType.GroupChat || message.chatType == EMMessage.ChatType.ChatRoom) {
                    message.to
                } else {
                    message.from
                }
            if (username == mChatUserName || message.to == mChatUserName || message.conversationId() == mChatUserName) {
                receiveMessageLiveData.postValue(message)
                conversation?.markMessageAsRead(message.msgId)
            }
        }
    }

    /**
     * 添加监听
     */
    fun addListener() {
        EMClient.getInstance().chatManager().addMessageListener(onEMMessageListener)
    }

    /**
     * 移除监听
     */
    fun removeListener() {
        EMClient.getInstance().chatManager().removeMessageListener(onEMMessageListener)
    }

    fun getUserNetworkState() {
        launchUI {
            try {
                withContext(Dispatchers.IO) {
                    LogUtils.i("发送透传消息=${BaseApplication.instance().mUserModel}")
                    val cmdMessage =
                        EMMessage.createSendMessage(EMMessage.Type.CMD)
                    val cmdBody = EMCmdMessageBody("getUserNetworkState")
                    cmdBody.deliverOnlineOnly(true)
                    cmdMessage.to = mChatUserName
                    cmdMessage.addBody(cmdBody)
                    cmdMessage.setAttribute(
                        "serverName",
                        BaseApplication.instance().mUserModel?.mobilePhone
                    )
                    EMClient.getInstance().chatManager().sendMessage(cmdMessage)
                    cmdMessage.setMessageStatusCallback(object : EMCallBack {
                        override fun onSuccess() {
                            LogUtils.i("发送透传消息成功 to $mChatUserName")
                            defUI.toastEvent.postValue("发送获取用户网络状况请求")
                        }

                        override fun onProgress(progress: Int, status: String?) {
                        }

                        override fun onError(code: Int, error: String?) {
                            LogUtils.i("发送透传消息失败 to $error")
                        }
                    });
                }
            } catch (t: Throwable) {
                t.printStackTrace()
            }

        }
    }
}
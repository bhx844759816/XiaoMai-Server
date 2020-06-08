package com.guangzhida.xiaomai.server.chat

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Process
import com.google.gson.Gson
import com.guangzhida.xiaomai.server.BaseApplication
import com.guangzhida.xiaomai.server.event.LiveDataBus
import com.guangzhida.xiaomai.server.event.LiveDataBusKey
import com.guangzhida.xiaomai.server.event.messageCountChangeLiveData
import com.guangzhida.xiaomai.server.model.CmdMessageModel
import com.guangzhida.xiaomai.server.room.AppDataBase
import com.guangzhida.xiaomai.server.room.entity.ConversationEntity
import com.guangzhida.xiaomai.server.ui.MainActivity
import com.guangzhida.xiaomai.server.utils.LogUtils
import com.guangzhida.xiaomai.server.utils.ToastUtils
import com.hyphenate.EMMessageListener
import com.hyphenate.chat.*
import java.util.concurrent.Executors

/**
 * 聊天的帮助类
 */
object ChatHelper {
    var notifier: NotificationUtils? = null
    lateinit var appContext: Context
    private val mExecutor = Executors.newSingleThreadExecutor()

    private val mConversationDao by lazy {
        AppDataBase.invoke(BaseApplication.instance().applicationContext).conversationDao()
    }
    private val mGson by lazy {
        Gson()
    }


    /**
     * 消息接收的监听
     */
    private val myEMMessageListener = object : EMMessageListener {
        override fun onMessageRecalled(messages: MutableList<EMMessage>?) {

        }

        override fun onMessageChanged(message: EMMessage?, change: Any?) {
        }

        override fun onCmdMessageReceived(messages: MutableList<EMMessage>?) {
            LogUtils.i("收到透传消息=${messages.toString()}")
            messages?.let {
                it.forEach { message ->
                    if (message.from == "admin") {
                        if (message.body is EMTextMessageBody) {
                            val messageBody = message.body as EMTextMessageBody
                            val cmdMsg = messageBody.message
                            val cmdMessageModel =
                                mGson.fromJson<CmdMessageModel>(cmdMsg, CmdMessageModel::class.java)
                            if (cmdMessageModel != null) {
                                LogUtils.i("设备报警通知")
                                notifier?.notify(cmdMessageModel.cmd, "设备报警")
                                notifier?.vibrateAndPlayTone()
                            }
                        }
                    } else {
                        val body = message.body
                        if (body is EMCmdMessageBody) {
                            if (body.action() == "replyUserNetworkState") {
                                addCmdMessageToConversation(message)
                            }
                        }

                    }
                }
            }
        }

        override fun onMessageReceived(messages: MutableList<EMMessage>?) {
            try {
                messages?.let {
                    it.forEach { message ->
                        val conversationEntity = mConversationDao?.queryByUserName(message.from)
                        if (conversationEntity == null) {
                            val entity = ConversationEntity(
                                userName = message.from
                            )
                            mConversationDao?.insert(entity)
                        }
                        //提示
                        if (message.isUnread) {
                            notifier?.notify(
                                message,
                                getUnReadMessageCount()
                            )
                            messageCountChangeLiveData.postValue(true)
                            notifier?.vibrateAndPlayTone()
                        }
                    }
                }
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }

        override fun onMessageDelivered(messages: MutableList<EMMessage>?) {
        }

        override fun onMessageRead(messages: MutableList<EMMessage>?) {
        }
    }


    /**
     * 初始化环信
     */
    fun init(context: Context, packageName: String) {
        val pid = Process.myPid()
        val processAppName =
            getAppName(context, pid)
        if (processAppName == null || !processAppName.equals(
                packageName,
                ignoreCase = true
            )
        ) {
            return
        }
        val options: EMOptions = initOption()
        EMClient.getInstance().init(context, options)
        appContext = context
        notifier = NotificationUtils(context)
        notifier?.setNotificationInfoProvider(object :
            NotificationUtils.EaseNotificationInfoProvider {
            override fun getLaunchIntent(message: EMMessage?): Intent {
                // you can set what activity you want display when user click the notification
                val intent =
                    Intent(appContext, MainActivity::class.java)
                intent.putExtra("userName", message?.from)
                return intent
            }

            override fun getSmallIcon(message: EMMessage?): Int {
                return 0
            }

            override fun getTitle(message: EMMessage?): String? {
                return null
            }

            override fun getLatestText(
                message: EMMessage?,
                fromUsersNum: Int,
                messageNum: Int
            ): String? {
                return null
            }

            override fun getDisplayedText(message: EMMessage?): String {
                return ""
            }

        })
        setGlobalListeners()
    }

    /**
     * 取消通知栏
     */
    fun cancelNotifyMessage() {
        notifier?.reset()
    }


    /**
     * 设置全局监听事件
     */
    private fun setGlobalListeners() {
        EMClient.getInstance().chatManager().addMessageListener(myEMMessageListener)
    }

    /**
     * 获取未读消息得个数
     */
    fun getUnReadMessageCount(): Int {
        return EMClient.getInstance().chatManager().unreadMessageCount
    }

    private fun getAppName(context: Context, pID: Int): String? {
        var processName: String? = null
        val am =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val l: List<*> = am.runningAppProcesses
        val i = l.iterator()
        while (i.hasNext()) {
            val info = i.next() as ActivityManager.RunningAppProcessInfo
            try {
                if (info.pid == pID) {
                    processName = info.processName
                    return processName
                }
            } catch (e: Exception) {
            }
        }
        return processName
    }

    /**
     * 添加检测的网络状况到本地会话
     */
    @Synchronized
    private fun addCmdMessageToConversation(message: EMMessage) {
        mExecutor.submit {
            try {
                val userName = message.getStringAttribute("userName")
                val content = message.getStringAttribute("content")
                if (content.isNotEmpty()) {
                    val conversation =
                        EMClient.getInstance().chatManager().getConversation(userName)
                    val addEMMessage = EMMessage.createReceiveMessage(EMMessage.Type.TXT)
                    addEMMessage.from = userName
                    addEMMessage.addBody(EMTextMessageBody(content))
                    conversation.appendMessage(addEMMessage)
                    LiveDataBus.with(LiveDataBusKey.CMD_MESSAGE_APPEND_KEY).postValue(addEMMessage)
                } else {
                    ToastUtils.ioToastShort("用户本地暂无保存的网络诊断信息")
                }
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }

    private fun initOption(): EMOptions {
        return EMOptions().apply {
            // 设置自动登录
            autoLogin = true
            // 设置是否需要发送已读回执
            requireAck = true
            // 设置是否需要发送回执，
            requireDeliveryAck = true
            // 设置是否根据服务器时间排序，默认是true
            isSortMessageByServerTime = false
            // 收到好友申请是否自动同意，如果是自动同意就不会收到好友请求的回调，因为sdk会自动处理，默认为true
            acceptInvitationAlways = false
            // 设置是否自动接收加群邀请，如果设置了当收到群邀请会自动同意加入
            isAutoAcceptGroupInvitation = false
            // 设置（主动或被动）退出群组时，是否删除群聊聊天记录
            isDeleteMessagesAsExitGroup = false
            // 设置是否允许聊天室的Owner 离开并删除聊天室的会话
            allowChatroomOwnerLeave(true)
        }
    }
}
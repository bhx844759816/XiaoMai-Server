package com.guangzhida.xiaomai.server.chat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.hyphenate.chat.EMChatService
import com.hyphenate.util.EMLog

/**
 * 开机广播启动聊天服务器
 */
class StartChatServiceReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED
            && intent?.action != "android.intent.action.QUICKBOOT_POWERON"
        ) {
            return
        }
        val startServiceIntent = Intent(context, EMChatService::class.java)
        startServiceIntent.putExtra("reason", "boot")
        try {
            context!!.startService(startServiceIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}
package com.guangzhida.xiaomai.server.ui.adapter

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.hyphenate.chat.EMMessage

/**
 * 聊天界面的适配器Type
 */
class ChatMultipleItem constructor(message: EMMessage) : MultiItemEntity {
    val mMessage = message
    var isVoicePlay: Boolean = false //是否播放语音

    override val itemType: Int
        get() = getItemTypeByMessage()

    private fun getItemTypeByMessage(): Int {
        return when (mMessage.direct()) {
            EMMessage.Direct.RECEIVE -> {
                LEFT_MESSAGE
            }
            EMMessage.Direct.SEND -> {
                RIGHT_MESSAGE
            }
            else -> {
                RIGHT_MESSAGE
            }
        }
    }

    companion object {
        const val LEFT_MESSAGE = 0 //左边 - 文本消息
        const val RIGHT_MESSAGE = 1 //左边 - 文本消息
//        const val LEFT_TEXT = 1 //左边 - 文本消息
//        const val RIGHT_TEXT = 2 //右边 - 文本消息
//        const val LEFT_IMG = 1 //左边 - 图片消息
//        const val RIGHT_IMG = 1 //右边 - 图片消息
//        const val LEFT_VOICE = 1 //左边 - 声音消息
//        const val RIGHT_VOICE = 1 //右边 - 声音消息
    }
}
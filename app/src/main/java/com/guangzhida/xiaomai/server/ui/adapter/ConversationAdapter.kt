package com.guangzhida.xiaomai.server.ui.adapter

import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.guangzhida.xiaomai.ktxlibrary.ext.clickN
import com.guangzhida.xiaomai.server.R
import com.guangzhida.xiaomai.server.ext.loadFilletRectangle
import com.guangzhida.xiaomai.server.http.BASE_URL
import com.guangzhida.xiaomai.server.model.ConversationModelWrap
import com.guangzhida.xiaomai.server.view.chat.SimpleCommonUtils
import com.hyphenate.chat.EMConversation
import com.hyphenate.chat.EMMessage
import com.hyphenate.chat.EMTextMessageBody
import com.hyphenate.util.DateUtils
import java.util.*

/**
 * 会话界面的Adapter
 */
class ConversationAdapter(data: MutableList<ConversationModelWrap>) :
    BaseQuickAdapter<ConversationModelWrap, BaseViewHolder>(
        R.layout.adapter_conversation_layout,
        data
    ) {
    var mClickContentCallBack: ((ConversationModelWrap) -> Unit)? = null
    var mLongClickContentCallBack: ((ConversationModelWrap, View) -> Unit)? = null

    override fun convert(helper: BaseViewHolder, item: ConversationModelWrap) {
        val center = helper.getView<View>(R.id.center)
        val parent = helper.getView<ConstraintLayout>(R.id.parent)
        val ivHeaderView = helper.getView<ImageView>(R.id.ivHeaderView)
        val tvName = helper.getView<TextView>(R.id.tvName)
        val tvChatMessage = helper.getView<TextView>(R.id.tvChatMessage)
        val tvTime = helper.getView<TextView>(R.id.tvTime)
        val tvChatUnReadMessageCount = helper.getView<TextView>(R.id.tvChatUnReadMessageCount)
        val emMessage = item.emConversation?.lastMessage
        val unReadMsgCount = item.emConversation?.unreadMsgCount ?: 0
        if (item.conversationEntity?.isTop == true) {
            parent.setBackgroundResource(R.drawable.shape_pressed_bg)
        } else {
            parent.setBackgroundResource(R.drawable.shape_pressed_normal_bg)
        }
        if (unReadMsgCount > 0) {
            tvChatUnReadMessageCount.visibility = View.VISIBLE
            val content = if (unReadMsgCount > 99) {
                "99+"
            } else {
                unReadMsgCount.toString()
            }
            tvChatUnReadMessageCount.text = content
        } else {
            tvChatUnReadMessageCount.visibility = View.GONE
        }
        if (emMessage != null) {
            item.conversationEntity?.let {
                //设置备注或者昵称
                tvName.text = it.nickName
                ivHeaderView.loadFilletRectangle(
                    BASE_URL.substring(0, BASE_URL.length - 1) + it.avatarUrl,
                    holder = R.mipmap.icon_default_header
                )
                if (emMessage.type == EMMessage.Type.TXT) {
                    SimpleCommonUtils.spannableEmoticonFilter(
                        tvChatMessage,
                        ((emMessage.body) as EMTextMessageBody).message
                    )
                } else {
                    tvChatMessage.text = getMessageDigest(emMessage)
                }
                tvTime.text =
                    DateUtils.getTimestampString(Date(emMessage.msgTime))
            }

        }
        parent.setOnLongClickListener {
            mLongClickContentCallBack?.invoke(item, center)
            return@setOnLongClickListener true
        }
        //点击跳转到聊天
        parent.clickN {
            mClickContentCallBack?.invoke(item)
        }
    }


    private fun getMessageDigest(
        message: EMMessage
    ): String? {
        return when (message.type) {
            EMMessage.Type.LOCATION -> "[地址]"
            EMMessage.Type.IMAGE -> "[图片]"
            EMMessage.Type.VOICE -> "[语音]"
            EMMessage.Type.VIDEO -> "[视频]"
            EMMessage.Type.FILE -> "[文件]"
            else -> {
                return ""
            }
        }
    }
}
package com.guangzhida.xiaomai.server.ui.adapter

import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.guangzhida.xiaomai.server.R
import com.guangzhida.xiaomai.server.ext.loadFilletRectangle
import com.guangzhida.xiaomai.server.http.BASE_URL
import com.guangzhida.xiaomai.server.model.ChatMessageRecordModel
import com.hyphenate.util.DateUtils
import java.util.*

class ChatMessageRecordAdapter(list: MutableList<ChatMessageRecordModel>) :
    BaseQuickAdapter<ChatMessageRecordModel, BaseViewHolder>(
        R.layout.adapter_chat_message_record_layout, list
    ) {
    var mContentClickCallBack: ((ChatMessageRecordModel) -> Unit)? = null
    override fun convert(helper: BaseViewHolder, item: ChatMessageRecordModel) {
        val parent = helper.getView<ConstraintLayout>(R.id.parent)
        val ivHeaderView = helper.getView<ImageView>(R.id.ivHeaderView)
        ivHeaderView.loadFilletRectangle(
            BASE_URL.substring(0, BASE_URL.length - 1) + item.userEntity.avatarUrl,
            holder = R.mipmap.icon_default_header
        )
        //设置备注或者昵称
        helper.setText(R.id.tvName, item.userEntity.nickName)
        helper.setText(R.id.tvSubTitle, item.message)
        helper.setText(R.id.tvTime, DateUtils.getTimestampString(Date(item.atTime)))
        parent.setOnClickListener {
            mContentClickCallBack?.invoke(item)
        }
    }

}
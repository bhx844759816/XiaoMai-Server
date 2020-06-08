package com.guangzhida.xiaomai.chatlibrary.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.guangzhida.xiaomai.chatlibrary.R

/**
 * 选项卡（文件 视频 图片）
 */
class ChatExtendMenu @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(
    context, attrs, defStyleAttr
) {
    init {
        LayoutInflater.from(context).inflate(R.layout.layout_chat_extend_menu, this)
    }
}
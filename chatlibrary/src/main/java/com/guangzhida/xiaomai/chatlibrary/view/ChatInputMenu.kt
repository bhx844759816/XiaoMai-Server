package com.guangzhida.xiaomai.chatlibrary.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import com.guangzhida.xiaomai.chatlibrary.R
import com.guangzhida.xiaomai.easechatlibrary.hideKeyboard
import kotlinx.android.synthetic.main.layout_chat_input_menu.view.*

/**
 * 包含输入框 表情  选项卡(图片文件)
 */
class ChatInputMenu @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(
    context, attrs, defStyleAttr
) {
    private var mListener: ChatInputMenuListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_chat_input_menu, this)
        chatPrimaryMenu.setChatPrimaryMenuListener(object :
            ChatPrimaryMenu.ChatPrimaryMenuListener {
            override fun onSendBtnClicked(content: String?) {
                mListener?.onSendMessage(content)
            }

            override fun onPressToSpeakBtnTouch(v: View?, event: MotionEvent?): Boolean {
                if (mListener != null) {
                    return mListener!!.onPressToSpeakBtnTouch(v, event)
                }
                return false
            }

            override fun onToggleVoiceBtnClicked() {
                toggleVoiceBtn()
            }

            override fun onToggleExtendClicked() {
                toggleExtendBtn()
            }

            override fun onToggleEmojiIconClicked() {
                toggleEmojiBtn()
            }
        })
    }

    /**
     * 切换点击声音的
     */
    private fun toggleVoiceBtn() {
        extend_menu_container.visibility = View.GONE
    }
    fun init(){
        chatEmojiGroupMenu.init()
    }
    /**
     * 点击+号
     */
    private fun toggleExtendBtn() {
        if (extend_menu_container.visibility == View.GONE) {
            hideKeyboard()
            handler.postDelayed({
                chatExtendMenu.visibility = View.VISIBLE
                extend_menu_container.visibility = View.VISIBLE
                chatEmojiGroupMenu.visibility = View.GONE
            }, 50)
        } else {
            if (chatEmojiGroupMenu.visibility == View.VISIBLE) {
                chatEmojiGroupMenu.visibility = View.GONE;
                chatExtendMenu.visibility = View.VISIBLE;
            } else {
                extend_menu_container.visibility = View.GONE;
            }
        }
    }

    /**
     * 切换点击表情
     */
    private fun toggleEmojiBtn() {
        if (extend_menu_container.visibility == View.GONE) {
            hideKeyboard()
            handler.postDelayed({
                extend_menu_container.visibility = View.VISIBLE
                chatExtendMenu.visibility = View.GONE
                chatEmojiGroupMenu.visibility = View.VISIBLE
                chatEmojiGroupMenu.init()
            }, 50)
        } else {
            if (chatEmojiGroupMenu.visibility == View.VISIBLE) {
                chatEmojiGroupMenu.visibility = View.GONE;
                extend_menu_container.visibility = View.GONE;
            } else {
                chatEmojiGroupMenu.visibility = View.VISIBLE;
                chatExtendMenu.visibility = View.GONE;
            }
        }
    }

    interface ChatInputMenuListener {
        /**
         * when send message button pressed
         *
         * @param content
         * message content
         */
        fun onSendMessage(content: String?)

        /**
         * when speak button is touched
         * @param v
         * @param event
         * @return
         */
        fun onPressToSpeakBtnTouch(v: View?, event: MotionEvent?): Boolean
    }
}

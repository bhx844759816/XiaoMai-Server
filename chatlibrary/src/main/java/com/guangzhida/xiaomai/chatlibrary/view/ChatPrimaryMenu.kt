package com.guangzhida.xiaomai.chatlibrary.view

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import com.guangzhida.xiaomai.chatlibrary.R
import com.guangzhida.xiaomai.easechatlibrary.addTextChangedListener
import com.guangzhida.xiaomai.easechatlibrary.hideKeyboard
import kotlinx.android.synthetic.main.layout_chat_primary_menu.view.*

/**
 * 输入框 语音和发送
 */
class ChatPrimaryMenu @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(
    context, attrs, defStyleAttr
) {
    private val mContext = context
    private var mListener: ChatPrimaryMenuListener? = null
    private var inputManager: InputMethodManager? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_chat_primary_menu, this)
        inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        //点击声音按钮
        btn_set_mode_voice.setOnClickListener {
            setModeVoice()
            iv_face_normal.visibility = View.VISIBLE
            iv_face_checked.visibility = View.INVISIBLE
            mListener?.onToggleVoiceBtnClicked()
        }
        //点击键盘按钮
        btn_set_mode_keyboard.setOnClickListener {
            setModeKeyboard()
            iv_face_normal.visibility = View.VISIBLE
            iv_face_checked.visibility = View.INVISIBLE
            mListener?.onToggleVoiceBtnClicked()
        }
        //按住说话
        btn_press_to_speak.setOnTouchListener { v, event ->
            if (mListener != null) {
                 mListener!!.onPressToSpeakBtnTouch(v, event)
            }
            false
        }
        //点击表情按钮 - 正常状态
        iv_face_normal.setOnClickListener {
            mListener?.onToggleEmojiIconClicked()
        }
        //点击表情按钮 - 选中状态
        iv_face_checked.setOnClickListener {
            mListener?.onToggleEmojiIconClicked()
        }
        //点击+号
        btn_more.setOnClickListener {
            edittext_layout.visibility = View.VISIBLE
            btn_set_mode_voice.visibility = View.VISIBLE
            btn_set_mode_keyboard.visibility = View.GONE
            btn_press_to_speak.visibility = View.GONE
            iv_face_normal.visibility = View.VISIBLE
            mListener?.onToggleExtendClicked()
        }
        //发送按钮
        btn_send.setOnClickListener {
            val content = et_sendmessage.text.toString().trim()
            mListener?.onSendBtnClicked(content)
        }
        //监听输入框
        et_sendmessage.addTextChangedListener {
            afterTextChanged {
                val text = et_sendmessage.text.toString().trim()
                if (text.isEmpty()) {
                    btn_more.visibility = View.GONE
                    btn_send.visibility = View.VISIBLE
                } else {
                    btn_more.visibility = View.VISIBLE
                    btn_send.visibility = View.GONE
                }
            }
        }
        //点击表情切换
        rl_face.setOnClickListener {
            toggleFaceImage()
            mListener?.onToggleEmojiIconClicked()
        }
    }

    private fun toggleFaceImage() {
        if (iv_face_normal.visibility == View.VISIBLE) {
            iv_face_normal.visibility = View.GONE
            iv_face_checked.visibility = View.VISIBLE
        } else {
            iv_face_normal.visibility = View.VISIBLE
            iv_face_checked.visibility = View.GONE
        }
    }

    /**
     * show voice icon when speak bar is touched
     *
     */
    private fun setModeVoice() {
        this.hideKeyboard()
        edittext_layout.visibility = View.GONE
        btn_set_mode_voice.visibility = View.GONE
        btn_set_mode_keyboard.visibility = View.VISIBLE
        btn_send.visibility = View.GONE
        btn_more.visibility = View.VISIBLE
        btn_press_to_speak.visibility = View.VISIBLE
        iv_face_normal.visibility = View.VISIBLE
        iv_face_checked.visibility = View.INVISIBLE
    }

    /**
     * 设置键盘
     */
    private fun  setModeKeyboard(){
        edittext_layout.visibility = View.VISIBLE
        btn_set_mode_keyboard.visibility = View.GONE
        btn_set_mode_voice.visibility = View.VISIBLE
        et_sendmessage.requestFocus()
        btn_press_to_speak.visibility = View.GONE
        if (TextUtils.isEmpty(et_sendmessage.text)) {
            btn_more.visibility = View.VISIBLE
            btn_send.visibility = View.GONE
        } else {
            btn_more.visibility = View.GONE
            btn_send.visibility = View.VISIBLE
        }
    }


    /**
     * 设置回调听
     */
    public fun setChatPrimaryMenuListener(listener: ChatPrimaryMenuListener) {
        mListener = listener
    }

    interface ChatPrimaryMenuListener {
        /**
         * 发送消息
         */
        fun onSendBtnClicked(content: String?)

        /**
         * 长按发送语言
         */
        fun onPressToSpeakBtnTouch(v: View?, event: MotionEvent?): Boolean


        /**
         * 点击声音
         */
        fun onToggleVoiceBtnClicked()

        /**
         *  点击展开
         */
        fun onToggleExtendClicked()

        /**
         * 点击表情
         */
        fun onToggleEmojiIconClicked()
    }

}
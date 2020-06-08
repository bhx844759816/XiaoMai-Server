package com.guangzhida.xiaomai.server.ui

import android.widget.SeekBar
import com.guangzhida.xiaomai.ktxlibrary.ext.gone
import com.guangzhida.xiaomai.ktxlibrary.ext.sharedpreference.getSpValue
import com.guangzhida.xiaomai.ktxlibrary.ext.sharedpreference.putSpValue
import com.guangzhida.xiaomai.ktxlibrary.ext.visible
import com.guangzhida.xiaomai.server.MESSAGE_SETTING_DAY
import com.guangzhida.xiaomai.server.MESSAGE_SETTING_SWITCH
import com.guangzhida.xiaomai.server.R
import com.guangzhida.xiaomai.server.base.BaseActivity
import com.guangzhida.xiaomai.server.ui.viewmodel.MessageSettingViewModel
import kotlinx.android.synthetic.main.activity_message_setting_layout.*

/**
 * 消息设置的界面
 */
class MessageSettingActivity : BaseActivity<MessageSettingViewModel>() {
    override fun getLayoutId(): Int = R.layout.activity_message_setting_layout

    override fun initView() {
        val isOpen = getSpValue(MESSAGE_SETTING_SWITCH, 0)
        val days = getSpValue(MESSAGE_SETTING_DAY, 7)
        toolBar.setNavigationOnClickListener {
            finish()
        }
        cbHideConversation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                llConversationSetting.visible()
                sbHideConversationDays.progress = days - 1
                putSpValue(MESSAGE_SETTING_SWITCH, 1)
            } else {
                llConversationSetting.gone()
                putSpValue(MESSAGE_SETTING_SWITCH, 0)
            }
        }
        sbHideConversationDays.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val progress = (seekBar?.progress ?: 0) + 1
                putSpValue(MESSAGE_SETTING_DAY, progress)
                tvHideConversationDays.text = buildString {
                    append(progress)
                    append("天内未联系")
                }
            }
        })

        cbHideConversation.isChecked = isOpen == 1
        if (cbHideConversation.isChecked) {
            sbHideConversationDays.progress = days - 1
            tvHideConversationDays.text = buildString {
                append(days)
                append("天内未联系")
            }
        }

    }

}
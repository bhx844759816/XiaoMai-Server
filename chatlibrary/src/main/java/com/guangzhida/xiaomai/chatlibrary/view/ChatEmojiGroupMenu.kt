package com.guangzhida.xiaomai.chatlibrary.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.guangzhida.xiaomai.chatlibrary.R
import com.sj.emoji.DefEmoticons
import github.ll.emoticon.common.adapter.emoticonadapter.DeleteBtnPageFactory
import github.ll.emotionboard.adpater.EmoticonPacksAdapter
import github.ll.emotionboard.data.Emoticon
import github.ll.emotionboard.data.EmoticonPack
import github.ll.emotionboard.utils.getResourceUri
import kotlinx.android.synthetic.main.layout_chat_emoji_group_menu.view.*


/**
 * 表情容器
 */
class ChatEmojiGroupMenu @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(
    context, attrs, defStyleAttr
) {

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_chat_emoji_group_menu, this)
        init()
    }


    fun getEmoji(context: Context): EmoticonPack<Emoticon> {
        val emojiArray = mutableListOf<Emoticon>()
        DefEmoticons.sEmojiArray.take(30).mapTo(emojiArray) {
            val emoticon = Emoticon()
            emoticon.code = it.emoji
            emoticon.uri = context.getResourceUri(it.icon)
            return@mapTo emoticon
        }
        val pack = EmoticonPack<Emoticon>()
        pack.emoticons = emojiArray
        pack.iconUri = context.getResourceUri(R.drawable.icon_emoji)
        val factory = DeleteBtnPageFactory<Emoticon>()
        factory.deleteIconUri = context.getResourceUri(R.drawable.icon_del)
        factory.line = 3
        factory.row = 7
        pack.pageFactory = factory
        return pack
    }

    fun init() {
        val list = getEmoji(context)
        val packs = mutableListOf<EmoticonPack<Emoticon>>();
        packs.add(list)
        val adapter = EmoticonPacksAdapter(packs)
        emoticonsBoard.setAdapter(adapter)
    }
}
package com.guangzhida.xiaomai.server.utils

import android.content.Context
import com.guangzhida.xiaomai.server.R
import com.guangzhida.xiaomai.server.view.chat.DeleteBtnPageFactory
import com.sj.emoji.DefEmoticons
import github.ll.emotionboard.adpater.EmoticonPacksAdapter
import github.ll.emotionboard.data.Emoticon
import github.ll.emotionboard.data.EmoticonPack
import github.ll.emotionboard.interfaces.OnEmoticonClickListener
import github.ll.emotionboard.utils.getResourceUri

/**
 * 获取Emoji资源
 */
object AdapterUtils {


    fun getCommonAdapter(
        context: Context,
        emoticonClickListener: OnEmoticonClickListener<Emoticon>?
    ): EmoticonPacksAdapter {
        val packs = mutableListOf<EmoticonPack<Emoticon>>()
        packs.add(getEmoji(context))
//        packs.add(getXhsPageSetEntity())
//        packs.add(getGoodGoodStudyPageSetEntity(context))
//        packs.add(getKaomojiPageSetEntity(context))
        val adapter = EmoticonPacksAdapter(packs)
        adapter.clickListener = emoticonClickListener
        return adapter
    }

    /**
     * 获取系统自带的Emoji
     */
    fun getEmoji(context: Context): EmoticonPack<Emoticon> {
        val emojiArray = mutableListOf<Emoticon>()
        DefEmoticons.sEmojiArray.take(60).mapTo(emojiArray) {
            val emoticon = Emoticon()
            emoticon.code = it.emoji
            emoticon.uri = context.getResourceUri(it.icon)
            return@mapTo emoticon
        }
        val pack = EmoticonPack<Emoticon>()
        pack.emoticons = emojiArray
        pack.iconUri = context.getResourceUri(R.mipmap.icon_emoji)
        val factory = DeleteBtnPageFactory<Emoticon>()
        factory.deleteIconUri = context.getResourceUri(R.mipmap.icon_del)
        factory.line = 3
        factory.row = 7
        pack.pageFactory = factory
        return pack
    }
}
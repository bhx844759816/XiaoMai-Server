package com.guangzhida.xiaomai.server.model

import com.guangzhida.xiaomai.server.room.entity.ConversationEntity
import com.hyphenate.chat.EMConversation

/**
 * 会话的数据
 */
data class ConversationModelWrap(
    var emConversation: EMConversation?,
    var conversationEntity: ConversationEntity?
)
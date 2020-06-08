package com.guangzhida.xiaomai.server.model

import com.guangzhida.xiaomai.server.room.entity.ConversationEntity

data class ChatMessageRecordModel(
    val messageId: String,
    val message: String,
    val atTime: Long,
    val userEntity: ConversationEntity
)
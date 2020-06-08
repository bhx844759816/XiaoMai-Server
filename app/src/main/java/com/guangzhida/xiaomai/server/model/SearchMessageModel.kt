package com.guangzhida.xiaomai.server.model

import com.guangzhida.xiaomai.server.room.entity.ConversationEntity

data class SearchMessageModel(
    val entity: ConversationEntity,
    var messageCount:Int
)
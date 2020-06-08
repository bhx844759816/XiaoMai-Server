package com.guangzhida.xiaomai.server.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ConversationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userName: String = "",//用户id
    var avatarUrl: String = "", //头像
    var nickName: String = "", //昵称
    var isTop: Boolean = false, //是否置顶
    var lastMessageTime: Long =0 //最后条消息得时间用于排序
)

//userName ->用户id
//用户头像
//用户昵称
//用户性别
//是否置顶
//lastMessageTime 最后条消息得时间用于排序


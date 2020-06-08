package com.guangzhida.xiaomai.server.model

/**
 * 聊天的用户信息对象
 *
 */
data class ChatUserModel(
    val age: Int,
    val attr1: Any,
    val attr2: Any,
    val attr3: Any,
    val createTime: Long,
    val createUser: Any,
    val headId: Any,
    val headUrl: String?,
    val id: String,
    val isDelete: Boolean,
    val message: Any,
    val mobilePhone: String,
    val nickName: String,
    val note: Any,
    val password: String,
    val sex: Int,
    val updateDate: Any,
    val updateUser: Any,
    val campusNetworkNum: Any,
    val campusNetworkPwd: Any,
    val onlineTime: Any,
    val signature: String? //个性签名
)

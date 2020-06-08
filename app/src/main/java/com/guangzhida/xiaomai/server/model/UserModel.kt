package com.guangzhida.xiaomai.server.model

data class UserModel(
    val id: String,
    val schoolId: String, //注册时选择的学校ID
    val username: String?,
    val name: String?,
    val mobilePhone: String?,
    val headId: String?,
    val headUrl: String?,
    val nickName: String?,
    val sex: String?,
    val age: String?,
    val isOnline: Int,
    val createTime: Long
)



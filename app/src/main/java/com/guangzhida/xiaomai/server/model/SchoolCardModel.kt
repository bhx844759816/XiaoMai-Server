package com.guangzhida.xiaomai.server.model

/**
 * 校园卡办理的Model对象
 */
data class SchoolCardModel(
    val schoolId: String,
    val userName: String?,
    val mobilePhone: String?,
    val schoolName: String?,
    val iid: String,
    val createTime: Long,
    val idCardNo: String?,
    var status: String
)


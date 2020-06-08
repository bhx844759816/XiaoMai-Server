package com.guangzhida.xiaomai.server.model

/**
 * 学校详情
 */
data class SchoolDetailModel(
    val iid: String, //id
    val managerId: String,//网管ID
    val schoolName: String, //学校名称
    val successNum: String,//正常数量
    val errorNum: String//异常数量

)

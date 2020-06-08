package com.guangzhida.xiaomai.server.model

/**
 * 设备详情
 */
data class DeviceDetailModel(
    val id: String,
    val name: String,
    val internetAddress:String,
    val isTelnet: Boolean,
    val ip: String,
    val port: String,
    val note: String,
    val status: Int,
    val schoolId: String
)

//{"id":"5","name":"设备5","isTelnet":false,"ip":"122.51.167.92","port":"8081","createUser":null,
//    "createTime":1588227009000,"updateTime":null,"updateUser":null,"isDelete":false,"note":null,
//    "attr1":null,"attr2":null,"attr3":null,"status":1,"schoolId":"2"
//}